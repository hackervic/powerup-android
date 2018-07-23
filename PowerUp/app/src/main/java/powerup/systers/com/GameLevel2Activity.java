/**
 * @desc presents the user with a dialogue scenario and updates the scenario
 * with more questions and answers as needed. Also updates power/health bars.
 */

package powerup.systers.com;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import powerup.systers.com.datamodel.Answer;
import powerup.systers.com.datamodel.Question;
import powerup.systers.com.datamodel.Scenario;
import powerup.systers.com.datamodel.SessionHistory;
import powerup.systers.com.db.DatabaseHandler;
import powerup.systers.com.kill_the_virus_game.KillTheVirusGame;
import powerup.systers.com.kill_the_virus_game.KillTheVirusSessionManager;
import powerup.systers.com.kill_the_virus_game.KillTheVirusTutorials;
import powerup.systers.com.powerup.PowerUpUtils;
import powerup.systers.com.save_the_blood_game.SaveTheBloodTutorialActivity;
import powerup.systers.com.vocab_match_game.VocabMatchTutorials;

@SuppressLint("NewApi")
public class GameLevel2Activity extends Activity {

    public Activity gameActivityInstance;
    @BindView(R.id.questionView)
    public TextView questionTextView;
    @BindView(R.id.scenarioNameEditText)
    public TextView scenarioNameTextView;
    @BindView(R.id.askerImageView)
    public ImageView npcImageView;
    @BindView(R.id.continueButtonGoesToMap)
    public Button goToMap;
    @BindView(R.id.mainListView)
    public ListView mainListView;
    @BindView(R.id.eye_view)
    public ImageView eyeImageView;
    @BindView(R.id.skin_view)
    public ImageView skinImageView;
    @BindView(R.id.hair_view)
    public ImageView hairImageView;
    @BindView(R.id.dress_view)
    public ImageView clothImageView;
    @BindView(R.id.progress_health)
    public ProgressBar health;
    @BindView(R.id.progress_healing)
    public ProgressBar healing;
    @BindView(R.id.progress_invisibility)
    public ProgressBar invisibility;
    @BindView(R.id.progress_telepathy)
    public ProgressBar telepathy;
    @Nullable
    @BindView(R.id.acc_view)
    public ImageView accessoryImageView;
    private Context context;
    private DatabaseHandler mDbHandler;
    private List<Answer> answers;
    private Scenario scene;
    private Scenario prevScene;
    private ArrayAdapter<String> listAdapter;

    public GameLevel2Activity() {
        gameActivityInstance = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        new ScenarioOverLevel2Activity(this).saveActivityOpenedStatus(false);
        context = GameLevel2Activity.this;
        super.onCreate(savedInstanceState);
        setmDbHandler(new DatabaseHandler(this));
        getmDbHandler().open();
        setContentView(R.layout.game_activity);
        ButterKnife.bind(this);

        if(new KillTheVirusSessionManager(this).isKillTheVirusOpened()){
            startActivity(new Intent(GameLevel2Activity.this, KillTheVirusGame.class));
            overridePendingTransition(R.animator.fade_in_custom, R.animator.fade_out_custom);
        }

        // Find the ListView resource.
        listAdapter = new ArrayAdapter<>(this, R.layout.simplerow, new ArrayList<String>());
        answers = new ArrayList<>();
        scene = getmDbHandler().getScenario();
        findViewById(R.id.root).setBackground(getResources().getDrawable(PowerUpUtils.SCENARIO_BACKGROUNDS[scene.getId() - 5]));
        SessionHistory.currScenePoints = 0;

        questionTextView.setMovementMethod(new ScrollingMovementMethod());

        //Updating the progress values
        health.setProgress(SessionHistory.progressHealth);
        healing.setProgress(SessionHistory.progressHealing);
        invisibility.setProgress(SessionHistory.progressInvisibility);
        telepathy.setProgress(SessionHistory.progressTelepathy);

        //Checking if the value of progress bars is max
        if(SessionHistory.progressHealth >= 100)
            health.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.correct_answer)));
        if(SessionHistory.progressHealing >= 100)
            healing.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.correct_answer)));
        if(SessionHistory.progressInvisibility >= 100)
            invisibility.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.correct_answer)));
        if(SessionHistory.progressTelepathy >= 100)
            telepathy.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.correct_answer)));

        String eyeImageName = getResources().getString(R.string.hs_eyes);
        eyeImageName = eyeImageName + getmDbHandler().getAvatarEye();
        R.drawable ourRID = new R.drawable();
        java.lang.reflect.Field photoNameField;
        try {
            photoNameField = ourRID.getClass().getField(eyeImageName);
            eyeImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException error) {
            error.printStackTrace();
        }

        String skinImageName = getResources().getString(R.string.hs_skin);
        skinImageName = skinImageName + getmDbHandler().getAvatarSkin();
        try {
            photoNameField = ourRID.getClass().getField(skinImageName);
            skinImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException error) {
            error.printStackTrace();
        }

        String clothImageName = getResources().getString(R.string.hs_dress_avatar);
        clothImageName = clothImageName + getmDbHandler().getAvatarCloth();
        try {
            photoNameField = ourRID.getClass().getField(clothImageName);
            clothImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException error) {
            error.printStackTrace();
        }

        String hairImageName = getResources().getString(R.string.hs_hair);
        hairImageName = hairImageName + getmDbHandler().getAvatarHair();
        try {
            photoNameField = ourRID.getClass().getField(hairImageName);
            hairImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException error) {
            error.printStackTrace();
        }

        getmDbHandler().setAvatarAccessory(getmDbHandler().getAvatarAccessory());

        String accessoryImageName = getResources().getString(R.string.hs_acc);
        accessoryImageName = accessoryImageName + getmDbHandler().getAvatarAccessory();
        try {
            photoNameField = ourRID.getClass().getField(accessoryImageName);
            accessoryImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException error) {
            error.printStackTrace();
        }

        // Update Scene
        updateScenario(0);
        updateQA();
        //Scene is Replayed

        if (scene.getReplayed() == 1)
            goToMap.setAlpha((float) 1.0);

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter(listAdapter);
        mainListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        if (answers.get(position).getNextQuestionID() > 0) {
                            // Next Question
                            SessionHistory.currQID = answers.get(position)
                                    .getNextQuestionID();
                            updatePoints(position);
                            updateProgressBars(position);
                            updateQA();
                        } else if (answers.get(position).getNextQuestionID() == -8) {
                            updatePoints(position);
                            updateProgressBars(position);
                            getmDbHandler().setCompletedScenario(scene.getId());
                            updateScenario(-8);
                        } else if (answers.get(position).getNextQuestionID() == -10) {
                            updatePoints(position);
                            updateProgressBars(position);
                            getmDbHandler().setCompletedScenario(scene.getId());
                            updateScenario(-10);
                        } else if (answers.get(position).getNextQuestionID() == -11) {
                            updatePoints(position);
                            updateProgressBars(position);
                            getmDbHandler().setCompletedScenario(scene.getId());
                            updateScenario(-11);
                        }
                        else {
                            if (SessionHistory.currSessionID == -1) {
                                // Check to make sure all scenes are completed
                                SessionHistory.currSessionID = 8;
                            }
                            updatePoints(position);
                            updateProgressBars(position);
                            getmDbHandler().setCompletedScenario(scene.getId());
                            updateScenario(0);
                        }
                    }
                });
    }

    @OnClick(R.id.progress_health)
    public void clickHealth(){
        if(SessionHistory.progressHealth >= 100) {
            showDialog("Health");
            SessionHistory.progressHealth = 0;
            health.setProgress(0);
            health.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.powerup_dark_blue)));
        }
    }

    @OnClick(R.id.progress_healing)
    public void clickHealing(){
        if(SessionHistory.progressHealing >= 100) {
            showDialog("Healing");
            SessionHistory.progressHealing = 0;
            healing.setProgress(0);
            healing.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.powerup_dark_blue)));
        }
    }

    @OnClick(R.id.progress_telepathy)
    public void clickTelepathy(){
        if(SessionHistory.progressTelepathy >= 100) {
            showDialog("Telepathy");
            SessionHistory.progressTelepathy = 0;
            telepathy.setProgress(0);
            telepathy.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.powerup_dark_blue)));
        }
    }

    @OnClick(R.id.progress_invisibility)
    public void clickInvisibility(){
        if(SessionHistory.progressInvisibility >= 100) {
            showDialog("Invisibility");
            SessionHistory.progressInvisibility= 0;
            invisibility.setProgress(0);
            invisibility.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.powerup_dark_blue)));
        }
    }

    @OnClick(R.id.continueButtonGoesToMap)
    public void clickGoToMap(){
        if (SessionHistory.currScenePoints != 0) {
            gotToMapDialogue();
            SessionHistory.totalPoints -= SessionHistory.currScenePoints;
            goToMap.setClickable(false);
            getmDbHandler()
                    .setReplayedScenario(scene.getScenarioName());
            goToMap.setAlpha((float) 0.0);
        } else {
            Intent intent = new Intent(getApplicationContext(), MapLevel2Activity.class);
            finish();
            startActivity(intent);
        }
    }

    /**
     * Add karma points to the session.
     *
     * @param position the current question user is on
     */
    private void updatePoints(int position) {
        // Update the Scene Points
        SessionHistory.currScenePoints += answers.get(position).getPoints();
        // Update Total Points
        SessionHistory.totalPoints += answers.get(position).getPoints();
    }

    /**
     * Updates the progress bars according to points given for the chosen answer
     * Healing & Health decrease if points for the chosen answer is 1 which reflects a bad choice
     * Invisibility & Telepathy continuously increase by different amounts depending on the quality of chosen answer
     * @param position the current question user is on
     */
    private void updateProgressBars(int position){
        //get the points for the chosen answer
        int points = answers.get(position).getPoints();

        if(points == 1) {
            SessionHistory.progressHealing -= (points * 2);
            SessionHistory.progressHealth-= (points * 4);
        }
        else {
            SessionHistory.progressHealing += (points * 2);
            SessionHistory.progressHealth += (points*20);
        }
        SessionHistory.progressInvisibility += (points*2);
        SessionHistory.progressTelepathy += (points*4);

        if(SessionHistory.progressHealth >= 100) {
            health.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.correct_answer)));
        }
        if (SessionHistory.progressHealing >= 100) {
            healing.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.correct_answer)));
        }
        if (SessionHistory.progressInvisibility >= 100) {
            invisibility.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.correct_answer)));
        }
        if (SessionHistory.progressTelepathy >= 100) {
            telepathy.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.correct_answer)));
        }
        health.setProgress(SessionHistory.progressHealth);
        healing.setProgress(SessionHistory.progressHealing);
        invisibility.setProgress(SessionHistory.progressInvisibility);
        telepathy.setProgress(SessionHistory.progressTelepathy);

    }

    /**
     * Used to show dialog box when a progress bar reaches it's maximum value
     * @param progress the progress bar whose maximum value is reached
     */
    public void showDialog(String progress){
        AlertDialog.Builder builder = new AlertDialog.Builder(GameLevel2Activity.this);
        builder.setTitle("Congratulations!")
                .setMessage("You have reached maximum value for " + progress + " and has earned 5 extra karma points");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                SessionHistory.totalPoints+=5;
            }
        });
        AlertDialog dialog = builder.create();
        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        drawable.setAlpha(200);
        dialog.getWindow().setBackgroundDrawable(drawable);
        dialog.show();
    }

    /**
     * Finish, replay, or go to another scenario as needed. Updates the
     * question and answer if the last scenario has not yet been reached.
     *
     * @param type coding scheme for .csv files, -8 means killvirus game, -10 means vocabmatch, 0 means scenario completion
     */
    private void updateScenario(int type) {
        if (ScenarioOverLevel2Activity.scenarioActivityDone == 1) {
            new ScenarioOverLevel2Activity().scenarioOverActivityInstance.finish();
        }
        if (scene != null)
            prevScene = getmDbHandler().getScenarioFromID(scene.getId());
        scene = getmDbHandler().getScenario();
        // Play the scenario first time
        if (scene.getReplayed() == 0) {
            // goToMap Mechanics
            goToMap.setAlpha((float) 1.0);
        }
        SessionHistory.currQID = scene.getFirstQuestionID();
        scenarioNameTextView.setText(scene.getScenarioName());
        // If completed check if it is last scene
        if (prevScene != null && prevScene.getCompleted() == 1) {
            SessionHistory.prevSessionID = scene.getId();
            SessionHistory.currSessionID = scene.getNextScenarioID();
            if (type == 0) {
                startActivity(new Intent(GameLevel2Activity.this, ScenarioOverLevel2Activity.class));
                overridePendingTransition(R.animator.fade_in_custom, R.animator.fade_out_custom);
            } else if (type == -8) {
                new KillTheVirusSessionManager(this).saveKillTheVirusOpenedStatus(true);
                startActivity(new Intent(GameLevel2Activity.this, KillTheVirusTutorials.class));
                overridePendingTransition(R.animator.fade_in_custom, R.animator.fade_out_custom);
            } else if (type == -10) {
                startActivity(new Intent(GameLevel2Activity.this, VocabMatchTutorials.class));
                overridePendingTransition(R.animator.fade_in_custom, R.animator.fade_out_custom);
            } else if (type == -11) {
                startActivity(new Intent(GameLevel2Activity.this, SaveTheBloodTutorialActivity.class));
                overridePendingTransition(R.animator.fade_in_custom, R.animator.fade_out_custom);
            }
        }
    }

    /**
     * Replace the current scenario with another question/answer.
     */
    private void updateQA() {
        listAdapter.clear();
        getmDbHandler().getAllAnswer(answers, SessionHistory.currQID);
        for (Answer ans : answers) {
            listAdapter.add(ans.getAnswerDescription());
        }
        Question questions = getmDbHandler().getCurrentQuestion();
        questionTextView.setText(questions.getQuestionDescription());
    }

    public DatabaseHandler getmDbHandler() {
        return mDbHandler;
    }

    public void setmDbHandler(DatabaseHandler mDbHandler) {
        this.mDbHandler = mDbHandler;
    }

    /**
     * Goes back to the map when user presses back button
     */
    @Override
    public void onBackPressed() {
        if (SessionHistory.currScenePoints != 0) {
            // clears the activities that were created after the found instance of the required activity
            gotToMapDialogue();
        } else {
            // The flag FLAG_ACTIVITY_CLEAR_TOP checks if an instance of the activity is present and it
            // clears the activities that were created after the found instance of the required activity
            startActivity(new Intent(GameLevel2Activity.this, MapLevel2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
    }

    public void gotToMapDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameLevel2Activity.this);
        builder.setTitle(context.getResources().getString(R.string.start_title_message))
                .setMessage(getResources().getString(R.string.game_to_map_message));
        builder.setPositiveButton(getString(R.string.game_confirm_message), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(GameLevel2Activity.this, MapLevel2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                SessionHistory.totalPoints -= SessionHistory.currScenePoints;
                finish();
                getmDbHandler().setReplayedScenario(scene.getScenarioName());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goToMap.setAlpha(1f);
                goToMap.setClickable(true);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        drawable.setAlpha(200);
        dialog.getWindow().setBackgroundDrawable(drawable);
        dialog.show();
    }
}