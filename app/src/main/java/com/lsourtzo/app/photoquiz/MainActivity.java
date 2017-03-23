package com.lsourtzo.app.photoquiz;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Level;


import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.x;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.lsourtzo.app.photoquiz.R.id.radiobutton_1;
import static com.lsourtzo.app.photoquiz.R.id.radiobutton_2;
import static com.lsourtzo.app.photoquiz.R.id.radiobutton_3;
import static com.lsourtzo.app.photoquiz.R.id.radiobutton_4;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    // this Variable NO needs to save on state -----------------------------------------------------------------

    int[] clocknames = new int[]{R.drawable.cl12, R.drawable.cl11, R.drawable.cl10, R.drawable.cl09, R.drawable.cl08, R.drawable.cl07, R.drawable.cl06, R.drawable.cl05, R.drawable.cl04, R.drawable.cl03, R.drawable.cl02, R.drawable.cl01, R.drawable.cl12};
    public String[] ScoreTableArray2 = new String[2]; // Use this string to seperate the question in to fields
    // this Variable needs to save on state -----------------------------------------------------------------

    int QNumber = 0; // total questions number
    int CNumber = 0;  // total correct number
    int Qtype = 0;   // Layer Type Number
    boolean local = true; // true = local mode  --   false = internet mode
    String PlayerName = "Player";

    int TotalQuestionsInFile = 0; // total qouestion that we parse in to local file
    int TotalQuestions = 40; // The number of questions that we want to play
    int LevelQuestions = 5; // the number of questions that we want to answear in any level
    int NextQuestionNumber = 0; // count the question thar we already play.
    int LevelNumber = 0; // Here I will keep the level number
    int TotalScore = 0;
    int LevelQuestionsScore = 0;
    long LevelTime = 60000;
    String ScoreTableString;
    String ScoreTableString2;

    public String[] QuestionArray = new String[12]; // Use this string to seperate the question in to fields
    private ArrayList<String[]> QuestionsArrayList = new ArrayList<String[]>();
    public String[] ScoreTableArray = new String[2]; // Use this string to seperate the question in to fields
    private ArrayList<String[]> ScoreTableArrayList = new ArrayList<String[]>();
    public int[] AnswearArray = new int[4];   // use this to randomize the answears line.
    public int[] RandomQuestionNumber = new int[TotalQuestions]; // This Array store an random sequence of number from 1 to TotalQuestions and declare the question that we will saw
    int rows; //counting the lines of text files

    int AudioId; // this Variables is for media player
    int AudioId2; // this Variables is for media player
    MediaPlayer mPlayer; // this Variables is for media player
    MediaPlayer mPlayer2; // this Variables is for media player

    int sec;
    CountDownTimer yourCountDownTimer;
    long tempmilis;
    boolean onstart = true;
    boolean onrestore;
    boolean onresume;
    boolean counttimerhasstoped = false;
    boolean timehasend = false;

    String QuestionLangSet;


    //// Where our porgram start.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CheckLanguageMehtod();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this return the total number of file that there is in to local txt file
        try {
            GetNumberOfQuestions();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if those question is less than total question that we want to play and adjust them.
        TotalQuestionsInFile = rows;
        if (TotalQuestionsInFile < TotalQuestions) {
            TotalQuestions = TotalQuestionsInFile;
        }
        CreateRandomQuestionList();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        // Integers
        savedInstanceState.putInt("QNumberS", QNumber);
        savedInstanceState.putInt("CNumberS", CNumber);
        savedInstanceState.putInt("QtypeS", Qtype);
        savedInstanceState.putInt("TotalQuestionsInFileS", TotalQuestionsInFile);
        savedInstanceState.putInt("TotalQuestionsS", TotalQuestions);
        savedInstanceState.putInt("LevelQuestionsS", LevelQuestions);
        savedInstanceState.putInt("NextQuestionNumberS", NextQuestionNumber);
        savedInstanceState.putInt("LevelNumberS", LevelNumber);
        savedInstanceState.putInt("TotalScoreS", TotalScore);
        savedInstanceState.putInt("LevelQuestionsScoreS", LevelQuestionsScore);
        savedInstanceState.putLong("LevelTimeS", LevelTime);
        savedInstanceState.putInt("rowsS", rows);
        savedInstanceState.putInt("AudioIdS", AudioId);
        savedInstanceState.putString("PlayerNameS", PlayerName);
        savedInstanceState.putString("ScoreTableStringS", ScoreTableString);
        savedInstanceState.putString("ScoreTableString2S", ScoreTableString2);
        savedInstanceState.putString("QuestionLangSetS", QuestionLangSet);
        //Booleans
        savedInstanceState.putBoolean("localS", local);
        //Media
        if (mPlayer != null && mPlayer.isPlaying()) {
            savedInstanceState.putBoolean("MediaPlayerStatusS", true);
            savedInstanceState.putInt("mPlayerS", mPlayer.getCurrentPosition());
            mPlayer.stop();
        } else {
            savedInstanceState.putBoolean("MediaPlayerStatusS", false);
        }
        //Arrays
        savedInstanceState.putStringArray("QuestionArrayS", QuestionArray);
        savedInstanceState.putIntArray("RandomQuestionNumberS", RandomQuestionNumber);
        savedInstanceState.putIntArray("AnswearArrayS", AnswearArray);
        //Animation clock
        //Log.d("Save timer", "sec :" + sec);
        savedInstanceState.putInt("secS", sec);
        savedInstanceState.putLong("tempmilisS", tempmilis);
        savedInstanceState.putIntArray("clocknamesS", clocknames);
        savedInstanceState.putBoolean("onstartS", onstart);
        savedInstanceState.putBoolean("timehasendS", timehasend);

        savedInstanceState.putBoolean("onresumeS", onresume);

        if (yourCountDownTimer != null) {
            counttimerhasstoped = true;
            yourCountDownTimer.cancel();
        }
        savedInstanceState.putBoolean("counttimerhasstopedS", counttimerhasstoped);

        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        Qtype = savedInstanceState.getInt("QtypeS");
        if (Qtype != 0) {

            QNumber = savedInstanceState.getInt("QNumberS");
            CNumber = savedInstanceState.getInt("CNumberS");

            TotalQuestionsInFile = savedInstanceState.getInt("TotalQuestionsInFileS");
            TotalQuestions = savedInstanceState.getInt("TotalQuestionsS");
            LevelQuestions = savedInstanceState.getInt("LevelQuestionsS");
            NextQuestionNumber = savedInstanceState.getInt("NextQuestionNumberS");
            LevelNumber = savedInstanceState.getInt("LevelNumberS");
            TotalScore = savedInstanceState.getInt("TotalScoreS");
            LevelQuestionsScore = savedInstanceState.getInt("LevelQuestionsScoreS");
            LevelTime = savedInstanceState.getLong("LevelTimeS");
            rows = savedInstanceState.getInt("rowsS");
            AudioId = savedInstanceState.getInt("AudioIdS");
            PlayerName = savedInstanceState.getString("PlayerNameS");
            ScoreTableString = savedInstanceState.getString("ScoreTableStringS");
            ScoreTableString2 = savedInstanceState.getString("ScoreTableString2S");
            QuestionLangSet = savedInstanceState.getString("QuestionLangSetS");
            //Booleans
            local = savedInstanceState.getBoolean("localS");
            onresume = savedInstanceState.getBoolean("onresumeS");
            //Media
            if (savedInstanceState.getBoolean("MediaPlayerStatusS")) {
                int pos = savedInstanceState.getInt("mPlayerS");
                mPlayer = MediaPlayer.create(this, AudioId);
                mPlayer.seekTo(pos);
            }
            //Arrays
            QuestionArray = savedInstanceState.getStringArray("QuestionArrayS");
            RandomQuestionNumber = savedInstanceState.getIntArray("RandomQuestionNumberS");
            AnswearArray = savedInstanceState.getIntArray("AnswearArrayS");
            //Animation clock
            sec = savedInstanceState.getInt("secS");
            clocknames = savedInstanceState.getIntArray("clocknamesS");
            tempmilis = savedInstanceState.getLong("tempmilisS");
            onstart = savedInstanceState.getBoolean("onstartS");
            timehasend = savedInstanceState.getBoolean("timehasendS");
            onrestore = true;

            timerrestore();
        }
    }

    protected void onResume() {
        super.onResume();
        if (!onstart && !onrestore) {
            timerrestore();
        }
        onresume = true;
        // Log.d("NextQuestionNumber1", " onresume: "+onresume);
        if (onrestore) {
            onrestore = false;
        }
        UnFocusEditText(R.id.NameText);
    }

    // time restore after orientation changed or close screen
    public void timerrestore() {
        if (!timehasend) {
            SetClock((int) ((60 - tempmilis / 1000) / 5));
            Log.d("Restore timer", "sec :" + (60 - tempmilis / 1000) / 5);
            ClockAnimation(tempmilis);
        }
        hidealllayers();
        try {
            ShowQuestionLayer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // this method will create the list of random numbers for questions
    public void CreateRandomQuestionList() {
        ArrayList<Integer> a = new ArrayList<>();
        for (int i = 1; i <= TotalQuestionsInFile; i++) { //to generate from 0-10 inclusive.
            //For 0-9 inclusive, remove the = on the <=
            a.add(i);
        }
        Collections.shuffle(a);
        for (int i = 0; i < TotalQuestions; i++) { //to generate from 0-10 inclusive.
            //For 0-9 inclusive, remove the = on the <=
            RandomQuestionNumber[i] = a.get(i);
        }
    }

    //creating random question answer
    public void CreateRandomAnswearList() {
        ArrayList<Integer> a = new ArrayList<>();
        for (int i = 2; i <= 5; i++) { //to generate from 0-10 inclusive.
            //For 0-9 inclusive, remove the = on the <=
            a.add(i);
        }
        Collections.shuffle(a);
        for (int i = 0; i <= 3; i++) { //to generate from 0-10 inclusive.
            //For 0-9 inclusive, remove the = on the <=

            Log.d("AnswearArrayFill", "AnswearArrayFill = " + i + " " + a.get(i));
            AnswearArray[i] = a.get(i);
        }
    }


    //this method it gona count the questions in txt file and pass questions in to array list
    public void GetNumberOfQuestions() throws IOException {
        rows = 0;
        java.util.Scanner input = new java.util.Scanner(getAssets().open(QuestionLangSet)).useDelimiter("\\A");
        // pre-read in the number of rows/columns
        while (input.hasNext()) {
            ++rows;
            QuestionArray = input.nextLine().split("\\t");
            QuestionsArrayList.add(QuestionArray);
        }
        input.close();
    }

    // This method split  the question in to QuestionArray
    // @NQN = Next Question Number
    public void GetQuestionInToArray(int NQN) throws IOException {
        Log.d("QuestionArrayFill", "RandomQuestionNumber[NQN - 1] - 1 = " + (NQN - 2));
        QuestionArray = QuestionsArrayList.get(RandomQuestionNumber[NQN - 1] - 1);
    }


    // When OK Button clicked ...
    public void OKButtonMethod(View v) throws IOException, InterruptedException {
        hidealllayers();
        onresume = false; // cancel onresume status when any key pressed
        Log.d("Qtype", "Qtype = " + Qtype);
        switch (Qtype) {
            case 0: // StartupScreen
                LevelNumber = 1;
                timehasend = true;
                PlayerName = EdittTextReturn(R.id.NameText);
                UnFocusEditText(R.id.NameText);
                CheckLevel();
                break;
            case 1: // RadioButton Screen
                CheckRadioAnswer();
                UnCheckRadioButton();
                NextQuestion();
                break;
            case 2: // CheckBox Screen
                CheckBoxAnswer();
                UnCheckAllCheckBox();
                NextQuestion();
                break;
            case 3: // EditText Screen
                CheckEditTextAnswer();
                UnCheckText();
                UnFocusEditText(R.id.EditBox);
                NextQuestion();
                break;
            case 4: // Final Results Screen
                Restart();
                break;
            case 5: // Level Layer
                LevelNumber = LevelNumber + 1;
                LevelQuestionsScore = 0;
                CNumber = 0;
                QNumber = 0;
                ClockStartup();
                NextQuestion();
                break;
            case 6: // Level Layer
                LevelNumber = 9;
                Log.d("LevelNumber", "LevelNumber = " + LevelNumber);
                GetScoreTable();
                CheckLevel();
                break;
        }

    }

    //this method will be se visibility in layers as gone to set app ready for next question
    public void hidealllayers() {
        ScrollViewGone(R.id.StartupLayout);
        ScrollViewGone(R.id.RadioGroupLayout);
        ScrollViewGone(R.id.CheckBoxLayout);
        ScrollViewGone(R.id.EditTextLayout);
        ScrollViewGone(R.id.FinalResultLayout);
        ScrollViewGone(R.id.StageLayers);
    }

    // this methode check RADIO  answeR ..
    public void CheckRadioAnswer() throws InterruptedException {
        String ans = "0";
        RadioGroup SelectionRadio = (RadioGroup) findViewById(R.id.QuestionRadioGroup);
        switch (SelectionRadio.getCheckedRadioButtonId()) {
            case R.id.radiobutton_1:
                ans = Integer.toString(AnswearArray[0] - 1);
                break;
            case R.id.radiobutton_2:
                ans = Integer.toString(AnswearArray[1] - 1);
                break;
            case R.id.radiobutton_3:
                ans = Integer.toString(AnswearArray[2] - 1);
                break;
            case R.id.radiobutton_4:
                ans = Integer.toString(AnswearArray[3] - 1);
                break;
        }
        if (ans.equals(QuestionArray[9])) {
            CorrectAnswear();
        } else {
            WrongAnswear();
        }
    }

    // this methode check CHECKBOX  answer ..
    public void CheckBoxAnswer() throws InterruptedException {
        int ca = 0;
        int counter = 0;
        if (QuestionArray[10].equals("null")) {
            ca = 1;
        } else {
            ca = 2;
        }
        CheckBox CB1 = (CheckBox) findViewById(R.id.checkbox_1);
        if (CB1.isChecked()) {
            if ((QuestionArray[9].equals(Integer.toString(AnswearArray[0] - 1))) || (QuestionArray[10].equals(Integer.toString(AnswearArray[0] - 1)))) {
                counter = counter + 1;
            } else {
                counter = counter - 10;
            }
        }
        CheckBox CB2 = (CheckBox) findViewById(R.id.checkbox_2);
        if (CB2.isChecked()) {
            if ((QuestionArray[9].equals(Integer.toString(AnswearArray[1] - 1))) || (QuestionArray[10].equals(Integer.toString(AnswearArray[1] - 1)))) {
                counter = counter + 1;
            } else {
                counter = counter - 10;
            }
        }
        CheckBox CB3 = (CheckBox) findViewById(R.id.checkbox_3);
        if (CB3.isChecked()) {
            if ((QuestionArray[9].equals(Integer.toString(AnswearArray[2] - 1))) || (QuestionArray[10].equals(Integer.toString(AnswearArray[2] - 1)))) {
                counter = counter + 1;
            } else {
                counter = counter - 10;
            }
        }
        CheckBox CB4 = (CheckBox) findViewById(R.id.checkbox_4);
        if (CB4.isChecked()) {
            if ((QuestionArray[9].equals(Integer.toString(AnswearArray[3] - 1)) || (QuestionArray[10].equals(Integer.toString(AnswearArray[3] - 1))))) {
                counter = counter + 1;
            } else {
                counter = counter - 10;
            }
        }
        if (ca == counter) {
            CorrectAnswear();
        } else {
            WrongAnswear();
        }
    }

    // this methode check EditText answer ..
    public void CheckEditTextAnswer() throws InterruptedException {
        String ET2 = EdittTextReturn(R.id.EditBox);
        if (ET2.equals(QuestionArray[9]) || ET2.equals(QuestionArray[10])) {
            CorrectAnswear();
        } else {
            WrongAnswear();
        }
    }

    //correct and wrong answear
    public void CorrectAnswear() throws InterruptedException {
        RadioAudioStop();
        RadioAudioStart2("correct");
        AnswearAnimation(R.id.correct);
        CNumber = CNumber + 1;
        LevelQuestionsScore = LevelQuestionsScore + Integer.parseInt(QuestionArray[11]);
    }

    public void WrongAnswear() throws InterruptedException {
        RadioAudioStop();
        RadioAudioStart2("wrong");
        AnswearAnimation(R.id.wrong);
    }


    //start up many things for next question
    public void NextQuestion() throws IOException {
        RadioAudioStop();
        CreateRandomAnswearList();
        NextQuestionNumber = NextQuestionNumber + 1;
        SetPhoto("paint");
        RadioAnimationStop();
        QNumber = QNumber + 1;
        ShowQuestionLayer();
    }

    public void ShowQuestionLayer() throws IOException {
        if (QNumber <= LevelQuestions && !timehasend) {
            GetQuestionInToArray(NextQuestionNumber);
            switch (QuestionArray[0]) {
                case "radio": // Call RadioButton Layout
                    SetRadioQuestion();
                    break;
                case "check": // Call CheckBox Screen
                    SetCheckBoxQuestion();
                    break;
                case "text": // Call EditText Screen
                    SetEditTextQuestion();
                    break;
            }
        } else { //Check Level and Final Result Screen
            CheckLevel();
        }
    }

    // This Method Apply question in Radio Question Layout
    public void SetRadioQuestion() {
        Qtype = 1;
        ScrollViewVis(R.id.RadioGroupLayout);
        SetTextView(QNumber + ". " + QuestionArray[1], R.id.RadioGroupQuestion);
        SetRadioButton(QuestionArray[AnswearArray[0]], radiobutton_1);
        SetRadioButton(QuestionArray[AnswearArray[1]], radiobutton_2);
        SetRadioButton(QuestionArray[AnswearArray[2]], radiobutton_3);
        SetRadioButton(QuestionArray[AnswearArray[3]], radiobutton_4);
        CheckMediatype();

    }

    // This Method Apply question in CheckBox Question Layout
    public void SetCheckBoxQuestion() {
        Qtype = 2;
        ScrollViewVis(R.id.CheckBoxLayout);
        SetTextView(QNumber + ". " + QuestionArray[1], R.id.CheckBoxQuestion);
        SetCheckBox(QuestionArray[AnswearArray[0]], R.id.checkbox_1);
        SetCheckBox(QuestionArray[AnswearArray[1]], R.id.checkbox_2);
        SetCheckBox(QuestionArray[AnswearArray[2]], R.id.checkbox_3);
        SetCheckBox(QuestionArray[AnswearArray[3]], R.id.checkbox_4);
        CheckMediatype();
    }

    // This Method Apply question in EditText Question Layout
    public void SetEditTextQuestion() {
        Qtype = 3;
        ScrollViewVis(R.id.EditTextLayout);
        SetTextView(QNumber + ". " + QuestionArray[1], R.id.EditTextQuestion);
        CheckMediatype();
    }


    //Check Level or GameOver

    public void CheckLevel() throws IOException {
        if (!timehasend) {
            yourCountDownTimer.cancel();
        }
        SetOver();
        switch (LevelNumber) {
            case 1://Stage 1 2/5 60sec
                LevelTime = 60000;
                TotalScore = 0;
                LevelQuestionsScore = 0;
                SetTextView(getString(R.string.Stage1), R.id.StageLayersText);
                ScrollViewVis(R.id.StageLayers);
                Qtype = 5;
                break;
            case 2://Stage 2 2/5 55sec
                LevelTime = 55000; // next level time
                if (CNumber >= 2) {
                    Pass(R.string.Stage2, 5);
                } else {
                    Cut(5);
                }
                break;
            case 3://Stage 3 3/5 55sec
                LevelTime = 55000;// next level time
                if (CNumber >= 2) {
                    Pass(R.string.Stage3, 4);
                } else {
                    Cut(4);
                }
                break;
            case 4://Stage 4 3/5 50sec
                LevelTime = 50000;// next level time
                if (CNumber >= 3) {
                    Pass(R.string.Stage4, 4);
                } else {
                    Cut(4);
                }
                break;
            case 5://Stage 5 4/5 50sec
                LevelTime = 50000;// next level time
                if (CNumber >= 3) {
                    Pass(R.string.Stage5, 3);
                } else {
                    Cut(3);
                }
                break;
            case 6://Stage 6 4/5 45sec
                LevelTime = 45000;// next level time
                if (CNumber >= 4) {
                    Pass(R.string.Stage6, 2);
                } else {
                    Cut(2);
                }
                break;
            case 7://Stage 7 5/5 40sec
                LevelTime = 40000;// next level time
                if (CNumber >= 4) {
                    Pass(R.string.Stage7, 1);
                } else {
                    Cut(1);
                }
                break;
            case 8://After Final Stage
                LevelTime = 60000;// next level time
                if (CNumber >= 5) {
                    FinalPass();
                } else {
                    FinalCut();
                }
                break;
            case 9://After Final Stage
                SetFinalResults();
                break;
        }
    }

    // when we pass to next levell
    public void Pass(int val, int timesd) {
        TotalScoreCalculator(timesd, 0);
        String DisplayText = getString(val) + "\n\n" +
                getString(R.string.QuestionLevelTotal) + LevelQuestionsScore + "\n" +
                getString(R.string.SecondsLeft) + sec + getString(R.string.Sec) + "\n" +
                getString(R.string.TimeBonus) + sec / timesd + " " + getString(R.string.Points) + "\n" +
                getString(R.string.TotalScore) + TotalScore + "\n";
        Qtype = 5;
        SetTextView(DisplayText, R.id.StageLayersText);
        ScrollViewVis(R.id.StageLayers);
        RadioAudioStart("win2");
    }

    // when we cut off next levell
    public void Cut(int timesd) {
        TotalScoreCalculator(timesd, 0);
        String DisplayText = getString(R.string.GameOver) + "\n\n" +
                getString(R.string.QuestionLevelTotal) + LevelQuestionsScore + "\n" +
                getString(R.string.SecondsLeft) + sec + getString(R.string.Sec) + "\n" +
                getString(R.string.TimeBonus) + sec / timesd + " " + getString(R.string.Points) + "\n" +
                getString(R.string.TotalScore) + TotalScore + "\n";
        Qtype = 6;
        SetTextView(DisplayText, R.id.StageLayersText);
        ScrollViewVis(R.id.StageLayers);
        RadioAudioStart("gameover");
    }

    // when we Finish all levell
    public void FinalPass() {
        TotalScoreCalculator(1, 100);
        String DisplayText = getString(R.string.Final) + "\n\n" +
                getString(R.string.QuestionLevelTotal) + LevelQuestionsScore + "\n" +
                getString(R.string.SecondsLeft) + sec + getString(R.string.Sec) + "\n" +
                getString(R.string.TimeBonus) + sec + " " + getString(R.string.Points) + "\n" +
                getString(R.string.TotalScore) + TotalScore + "\n";
        Qtype = 6;
        SetTextView(DisplayText, R.id.StageLayersText);
        ScrollViewVis(R.id.StageLayers);
        RadioAudioStart("win");
    }

    // when we cut off in last Level
    public void FinalCut() {
        TotalScoreCalculator(1, 0);
        String DisplayText = getString(R.string.GameOver) + "\n\n" +
                getString(R.string.QuestionLevelTotal) + LevelQuestionsScore + "\n" +
                getString(R.string.SecondsLeft) + sec + getString(R.string.Sec) + "\n" +
                getString(R.string.TimeBonus) + sec + " " + getString(R.string.Points) + "\n" +
                getString(R.string.TotalScore) + TotalScore + "\n";
        Qtype = 6;
        SetTextView(DisplayText, R.id.StageLayersText);
        ScrollViewVis(R.id.StageLayers);
        RadioAudioStart("gameover");
    }

    // Calculating total score
    public void TotalScoreCalculator(int timesd, int bonus) {
        //Log.d("NextQuestionNumber1", "NextQuestionNumber = "+NextQuestionNumber+" onrestore: "+onrestore+" onresume: "+onresume);
        if (!onrestore && !onresume) {
            //because they count one more when showing result page
            onresume = true;
            NextQuestionNumber = NextQuestionNumber - 1;
            //Log.d("NextQuestionNumber2", "NextQuestionNumber = "+NextQuestionNumber);
            // because we want want to change score after resume or restore
            TotalScore = (int) (TotalScore + sec / timesd + LevelQuestionsScore) + bonus;
        }

    }

    // this Method uncheck all check boxes
    public void UnCheckAllCheckBox() {
        UnCheckCheckBox(R.id.checkbox_1);
        UnCheckCheckBox(R.id.checkbox_2);
        UnCheckCheckBox(R.id.checkbox_3);
        UnCheckCheckBox(R.id.checkbox_4);
    }

    // this Method clear the previus text from edittext view
    public void UnCheckText() {
        SetEditText("", R.id.EditBox);
    }

    // This Method Apply Results in Final Result Layout
    public void SetFinalResults() throws IOException {
        Qtype = 4;
        TinyDB tinydb = new TinyDB(this);
        String data = tinydb.getString("ScoreTable");
        String[] separated = data.split("\n");
        ScoreTableString = "";
        ScoreTableString2 = "";
        for (int i = 0; i <= 9; ++i) {
            ScoreTableArray= (separated[i].split("\t"));
            ScoreTableString = ScoreTableString+ScoreTableArray[0]+"\n";
            ScoreTableString2 = ScoreTableString2+ScoreTableArray[1]+"\n";
        }
        SetTextView(ScoreTableString, R.id.FinalResaltNames);
        SetTextView(ScoreTableString2, R.id.FinalResaltScore);
        ScrollViewVis(R.id.FinalResultLayout);
    }

    // This Method restat game and call Startup Layout
    public void Restart() {
        CreateRandomQuestionList();
        SetClock(0);
        tempmilis = 0;
        sec = 0;
        Qtype = 0;
        QNumber = 0;
        CNumber = 0;
        NextQuestionNumber = 0;
        ScrollViewVis(R.id.StartupLayout);
    }

    // this method check media type of question and aply photo and audio
    public void CheckMediatype() {
        switch (QuestionArray[6]) {
            case "photo":
                SetPhoto(QuestionArray[7]);
                break;
            case "audio":
                RadioAnimationStart();
                RadioAudioStart(QuestionArray[8]);
                break;
            case "photoaudio":
                SetPhoto(QuestionArray[7]);
                RadioAnimationStart();
                RadioAudioStart(QuestionArray[8]);
                break;
            case "text":
                break;
        }
    }

    public void SetOver() {
        hidealllayers();
        RadioAnimationStop();
        RadioAudioStop();
        if (Qtype == 3) {
            UnFocusEditText(R.id.EditBox);
        }
        SetPhoto("paint");
    }


    // Set ScrollView Layer Visible - Gone... --------------------------------
    public void ScrollViewGone(int val) {
        ScrollView popup = (ScrollView) findViewById(val);
        popup.setVisibility(View.GONE);
        popup.scrollTo(0, 0);
    }

    public void ScrollViewVis(int val) {
        ScrollView popup = (ScrollView) findViewById(val);
        popup.setVisibility(View.VISIBLE);
    }

    // Set ImageView Visible - Gone... --------------------------------
    public void ImageViewGone(int val) {
        ImageView popup = (ImageView) findViewById(val);
        popup.setVisibility(View.GONE);
    }

    public void ImageViewVis(int val) {
        ImageView popup = (ImageView) findViewById(val);
        popup.setVisibility(View.VISIBLE);
    }

    // TextView Text changer ----------------------------------------------------
    public void SetTextView(String what, int where) {
        TextView newtext = (TextView) findViewById(where);
        newtext.setText(String.valueOf(what));
    }

    // EditText Text changer ----------------------------------------------------
    public void SetEditText(String what, int where) {
        EditText newtext = (EditText) findViewById(where);
        newtext.setText(String.valueOf(what));
    }

    // EditText Close keyboard after OK Button pressed  --------------------------------
    public void UnFocusEditText(int where) {
        EditText newtext = (EditText) findViewById(where);
        newtext.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(newtext.getWindowToken(), 0);
    }

    // RadioButton Text changer ----------------------------------------------------
    public void SetRadioButton(String what, int where) {
        RadioButton newtext = (RadioButton) findViewById(where);
        newtext.setText(String.valueOf(what));
    }

    // RadioButton Uncheck  ----------------------------------------------------
    public void UnCheckRadioButton() {
        RadioGroup radio = (RadioGroup) findViewById(R.id.QuestionRadioGroup);
        radio.clearCheck();
    }

    // CheckBox Text changer ----------------------------------------------------
    public void SetCheckBox(String what, int where) {
        CheckBox newtext = (CheckBox) findViewById(where);
        newtext.setText(String.valueOf(what));
    }

    // CheckBox UnCheck ----------------------------------------------------
    public void UnCheckCheckBox(int where) {
        CheckBox check = (CheckBox) findViewById(where);
        check.setChecked(false);
    }


    // EditTextReturn changer ----------------------------------------------------
    public String EdittTextReturn(int val) {
        EditText ET = (EditText) findViewById(val);
        return ET.getText().toString();
    }

    // Frame Photo changer ----------------------------------------------------
    public void SetPhoto(String val) {
        ImageView whoamiwith = (ImageView) findViewById(R.id.Photo);
        int resId = getResources().getIdentifier(val, "drawable", getPackageName()); // change string to id
        whoamiwith.setImageResource(resId);
    }

    // Frame Photo changer ----------------------------------------------------
    public void SetClock(int val) {
        ImageView whoamiwith = (ImageView) findViewById(R.id.clock1);
        whoamiwith.setImageResource(this.clocknames[val]);
    }


    // Radio Animation Start - Stop   ----------------------------------------------------
    public void RadioAnimationStart() {
        ImageView stereo = (ImageView) findViewById(R.id.radio);
        stereo.setBackgroundResource(R.drawable.radio);
        AnimationDrawable RadioAnimation = (AnimationDrawable) stereo.getBackground();
        RadioAnimation.start();
        ImageViewVis(R.id.radio);
        ImageViewGone(R.id.radio2);
    }

    public void RadioAnimationStop() {
        ImageView stereo = (ImageView) findViewById(R.id.radio);
        stereo.setBackgroundResource(R.drawable.radio);
        AnimationDrawable RadioAnimation = (AnimationDrawable) stereo.getBackground();
        RadioAnimation.stop();
        ImageViewVis(R.id.radio2);
        ImageViewGone(R.id.radio);
    }

    // Radio pressed button

    public void OnRadioButtonPressed(View view) {
        RadioAudioStop();
        RadioAudioStart(QuestionArray[8]);

    }

    // Radio Audio Start - Stop   ----------------------------------------------------
    public void RadioAudioStart(String val) {
        AudioId = getResources().getIdentifier(val, "raw", getPackageName()); // change string to id
        mPlayer = MediaPlayer.create(this, AudioId);

        try {
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }


        //Log.d("Start Media", "Start :" + mPlayer.isPlaying());
        if (!mPlayer.isPlaying())
            mPlayer.start();
    }

    public void RadioAudioStart2(String val) {
        AudioId2 = getResources().getIdentifier(val, "raw", getPackageName()); // change string to id
        mPlayer2 = MediaPlayer.create(this, AudioId2);

        try {
            mPlayer2.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }


        //Log.d("Start Media", "Start :" + mPlayer.isPlaying());
        if (!mPlayer2.isPlaying())
            mPlayer2.start();
    }

    public void RadioAudioStop() {

        if (mPlayer != null && mPlayer.isPlaying() == true) {
            mPlayer.stop();
        }

    }


    //ZoomIN Animation

    public void AnswearAnimation(final int val) throws InterruptedException {
        Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom);
        ImageViewVis(val);
        ImageView zoom = (ImageView) findViewById(val);
        zoom.startAnimation(zoomAnimation);
        zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ImageViewGone(val);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    //Clock Animation
    public void ClockAnimation(long time) {
        SetClock((int) ((60 - (int) (time / 1000)) / 5));
        if (!timehasend) {
            yourCountDownTimer = new CountDownTimer(time, 1000) {
                public void onTick(long millisUntilFinished) {
                    tempmilis = millisUntilFinished;
                    sec = (int) (millisUntilFinished / 1000);
                    //Log.d("timer", "sec :"+sec);
                    if (sec % 5 == 0) {
                        RadioAudioStart2("clocktick");
                        Log.d("timer", "sec :" + sec);
                        int remainsec = 60 - sec;
                        SetClock((int) (remainsec / 5));
                    }
                }

                public void onFinish() {
                    RadioAudioStart2("buzzer");
                    //QNumber = QNumber + 1;
                    UnCheckText();
                    UnCheckAllCheckBox();
                    UnCheckRadioButton();
                    timehasend = true;
                    tempmilis = 0;
                    sec = 0;
                    SetClock(0);
                    SetOver();
                    try {
                        ShowQuestionLayer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }
    }

    //clock startup
    public void ClockStartup() {
        onstart = false;
        timehasend = false;
        ClockAnimation(LevelTime);
    }


    // Score table mehtods
    // Writee to file
    private void GetScoreTable() throws IOException {
        //https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo
        //http://stackoverflow.com/questions/7057845/save-arraylist-to-sharedpreferences


        TinyDB tinydb = new TinyDB(this);

        //restore string
        String data = tinydb.getString("ScoreTable");

        // If string is empty
        if (data.equals("")) {
            Log.d("ScoreTable", "Data String is empty");
            // fill table with start values
            for (int i = 0; i <= 9; ++i) {
                data = data + "Player" + "\t" + "0" + "\n";
            }
            Log.d("ScoreTable", "Crerating Data String" + data);
        }
        //put string in to table
        ScoreTableArrayList.clear();
        String[] separated = data.split("\n");
        for (int i = 0; i <= 9; ++i) {
            ScoreTableArray= (separated[i].split("\t"));
            ScoreTableArrayList.add(i,ScoreTableArray);
        }

        //sort table
        // Sorting Procedure and adding new score
        ScoreTableArray = ScoreTableArrayList.get(9);
        if (Integer.parseInt(ScoreTableArray[1]) < TotalScore) {
            if (PlayerName.equals("")) PlayerName="No Name"; // check if player name is empty
            ScoreTableArray[0] = PlayerName;
            ScoreTableArray[1] = Integer.toString(TotalScore);
            ScoreTableArrayList.set(9,ScoreTableArray);
            int i = 8;
            boolean done=false;
            while (i >= 0 && !done ) {
                ScoreTableArray = ScoreTableArrayList.get(i);
                ScoreTableArray2=ScoreTableArrayList.get(i+1);
                if (Integer.parseInt(ScoreTableArray[1])<Integer.parseInt(ScoreTableArray2[1])) {
                    ScoreTableArrayList.set(i,ScoreTableArray2);
                    ScoreTableArrayList.set(i+1,ScoreTableArray);
                    i=i-1;
                }
                else{
                    done=true;
                }
            }
        }

        //save string
        data="";
        // Recreate data string
            for (int i = 0; i <= 9; ++i) {
                ScoreTableArray = ScoreTableArrayList.get(i);
                data = data + ScoreTableArray[0] + "\t" + ScoreTableArray[1] + "\n";
            }
        tinydb.putString("ScoreTable", data);

    }

    // Language methods
    // check locatrion and if it's greek set the question file this with greeks.

    public void CheckLanguageMehtod(){
        if ( Locale.getDefault().getLanguage().contentEquals("el")) {
            QuestionLangSet = "questionsfinal-el.txt";
        }else{
            QuestionLangSet = "questionsfinal.txt";
        }

    }




}



