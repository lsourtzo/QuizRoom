package com.lsourtzo.app.photoquiz;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.graphics.drawable.AnimationDrawable;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.data;
import static android.R.attr.password;
import static com.google.firebase.crash.FirebaseCrash.log;
import static com.lsourtzo.app.photoquiz.R.anim.zoom;
import static com.lsourtzo.app.photoquiz.R.anim.zoom2;
import static com.lsourtzo.app.photoquiz.R.anim.zoom3;

public class MainActivity extends AppCompatActivity {

    // this Variable NO needs to save on state -----------------------------------------------------------------
    boolean tempB;
    int tempI;

    int[] clocknames = new int[]{R.drawable.cl12, R.drawable.cl11, R.drawable.cl10, R.drawable.cl09, R.drawable.cl08, R.drawable.cl07, R.drawable.cl06, R.drawable.cl05, R.drawable.cl04, R.drawable.cl03, R.drawable.cl02, R.drawable.cl01, R.drawable.cl12};
    public String[] ScoreTableArray2 = new String[2]; // Use this string to seperate the question in to fields
    // this Variable needs to save on state -----------------------------------------------------------------

    int QNumber = 0; // total questions number
    int CNumber = 0;  // total correct number
    int Qtype = 0;   // Layer Type Number
    int loginMethod;   // Layer Type Number
    boolean local = true; // true = local mode  --   false = internet mode
    String PlayerName = "Player";
    String PlayerEmail;
    String PlayerPassword;

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
    boolean onlogin = true;
    boolean counttimerhasstoped = false;
    boolean timehasend = false;

    String QuestionLangSet;

    // initializing views
    ScrollView svStartupLayout;
    ScrollView svRadioGroupLayout;
    ScrollView svCheckBoxLayout;
    ScrollView svEditTextLayout;
    ScrollView svFinalResultLayout;
    ScrollView svStageLayers;
    ScrollView svLoginScreen;
    ImageView svradio;
    ImageView svradio2;
    ImageView svwrong;
    ImageView svcorrect;
    ImageView svgameover;
    ImageView svcongrats;
    ImageView svfw;
    ImageView svcrack;
    ImageView svOkButton;
    WebView svFinalResaltScore;
    WebView svFinalResaltNames;
    WebView svFBFinalResaltScore;
    WebView svFBFinalResaltNames;
    WebView svStageLayersText;
    TextView svEditTextQuestion;
    TextView svCheckBoxQuestion;
    TextView svRadioGroupQuestion;
    EditText svEditBox;
    EditText svNameText;
    EditText svEmailEditBox;
    EditText svPasswordEditBox;
    RadioButton svradiobutton_1;
    RadioButton svradiobutton_2;
    RadioButton svradiobutton_3;
    RadioButton svradiobutton_4;
    RadioGroup svQuestionRadioGroup;
    CheckBox svcheckbox_1;
    CheckBox svcheckbox_2;
    CheckBox svcheckbox_3;
    CheckBox svcheckbox_4;
    ImageView svPhoto;
    ImageView svclock1;
    AnimationDrawable fwAnim;

    //Firebase scoreboard

    String sFName;
    String sFScore;
    String sFUID;

    public static class Player {
        public String firebaseName;
        public String firebaseUID;
        public int firebaseScore;

        public Player() {
        }

        public Player(String firebaseName, String firebaseUID, int firebaseScore) {
            this.firebaseName = firebaseName;
            this.firebaseUID = firebaseUID;
            this.firebaseScore = firebaseScore;
        }

    }

    String tempuser;


    // email auth var
    private FirebaseDatabase database;
    private DatabaseReference scoresFirabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //google auth var
    GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 100;


    //// Where our porgram start.

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        CheckLanguageMehtod();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase scoreboard
        database = FirebaseDatabase.getInstance();
        scoresFirabase = database.getReference("score table new");
        Log.d("OnLogin", " Create: " + onlogin);
        /// onlogin =true;

        //Firebase  authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Signed_in:", user.getUid());
                } else {
                    // User is signed out
                    //Log.d("Signed_out:", user.getUid());
                }
                // ...
            }
        };


        //Firebase Google Authentication
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // initializing views
        svStartupLayout = (ScrollView) findViewById(R.id.StartupLayout);
        svRadioGroupLayout = (ScrollView) findViewById(R.id.RadioGroupLayout);
        svCheckBoxLayout = (ScrollView) findViewById(R.id.CheckBoxLayout);
        svEditTextLayout = (ScrollView) findViewById(R.id.EditTextLayout);
        svFinalResultLayout = (ScrollView) findViewById(R.id.FinalResultLayout);
        svStageLayers = (ScrollView) findViewById(R.id.StageLayers);
        svLoginScreen = (ScrollView) findViewById(R.id.LoginScreen);
        svradio = (ImageView) findViewById(R.id.radio);
        svradio2 = (ImageView) findViewById(R.id.radio2);
        svwrong = (ImageView) findViewById(R.id.wrong);
        svcorrect = (ImageView) findViewById(R.id.correct);
        svgameover = (ImageView) findViewById(R.id.gameover);
        svcongrats = (ImageView) findViewById(R.id.congratsulation);
        svfw = (ImageView) findViewById(R.id.fireworkimage);
        svcrack = (ImageView) findViewById(R.id.crack);
        svOkButton = (ImageView) findViewById(R.id.okbuttonimage);
        svFinalResaltScore = (WebView) findViewById(R.id.FinalResaltScore);
        svFinalResaltNames = (WebView) findViewById(R.id.FinalResaltNames);
        svFBFinalResaltScore = (WebView) findViewById(R.id.FBFinalResaltScore);
        svFBFinalResaltNames = (WebView) findViewById(R.id.FBFinalResaltNames);
        svStageLayersText = (WebView) findViewById(R.id.StageLayersText);
        svEditTextQuestion = (TextView) findViewById(R.id.EditTextQuestion);
        svCheckBoxQuestion = (TextView) findViewById(R.id.CheckBoxQuestion);
        svRadioGroupQuestion = (TextView) findViewById(R.id.RadioGroupQuestion);
        svEditBox = (EditText) findViewById(R.id.EditBox);
        svNameText = (EditText) findViewById(R.id.NameText);
        svEmailEditBox = (EditText) findViewById(R.id.EmailEditBox);
        svPasswordEditBox = (EditText) findViewById(R.id.PasswordEditBox);
        svradiobutton_1 = (RadioButton) findViewById(R.id.radiobutton_1);
        svradiobutton_2 = (RadioButton) findViewById(R.id.radiobutton_2);
        svradiobutton_3 = (RadioButton) findViewById(R.id.radiobutton_3);
        svradiobutton_4 = (RadioButton) findViewById(R.id.radiobutton_4);
        svQuestionRadioGroup = (RadioGroup) findViewById(R.id.QuestionRadioGroup);
        svcheckbox_1 = (CheckBox) findViewById(R.id.checkbox_1);
        svcheckbox_2 = (CheckBox) findViewById(R.id.checkbox_2);
        svcheckbox_3 = (CheckBox) findViewById(R.id.checkbox_3);
        svcheckbox_4 = (CheckBox) findViewById(R.id.checkbox_4);
        svPhoto = (ImageView) findViewById(R.id.Photo);
        svclock1 = (ImageView) findViewById(R.id.clock1);

        //we will override the application class to enable Firebase's offline mode before any database references are used (FirebaseDemoApplication.java)
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

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

        // fireworks animation

        svfw.setBackgroundResource(R.drawable.fireworklist);
        fwAnim = (AnimationDrawable) svfw.getBackground();


    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        // Integers
        savedInstanceState.putInt("QNumberS", QNumber);
        savedInstanceState.putInt("CNumberS", CNumber);
        savedInstanceState.putInt("QtypeS", Qtype);
        savedInstanceState.putInt("loginMethodS", loginMethod);
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
        savedInstanceState.putString("PlayerEmailS", PlayerEmail);
        savedInstanceState.putString("PlayerPasswordS", PlayerPassword);
        savedInstanceState.putString("ScoreTableStringS", ScoreTableString);
        savedInstanceState.putString("ScoreTableString2S", ScoreTableString2);
        savedInstanceState.putString("QuestionLangSetS", QuestionLangSet);
        savedInstanceState.putString("sFUIDS", sFUID);
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
        savedInstanceState.putBoolean("onloginS", onlogin);

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
        //if (Qtype == 0) {

        QNumber = savedInstanceState.getInt("QNumberS");
        CNumber = savedInstanceState.getInt("CNumberS");
        loginMethod = savedInstanceState.getInt("loginMethodS");

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
        PlayerEmail = savedInstanceState.getString("PlayerEmailS");
        PlayerPassword = savedInstanceState.getString("PlayerPasswordS");
        ScoreTableString = savedInstanceState.getString("ScoreTableStringS");
        ScoreTableString2 = savedInstanceState.getString("ScoreTableString2S");
        QuestionLangSet = savedInstanceState.getString("QuestionLangSetS");
        sFUID = savedInstanceState.getString("sFUIDS");
        //Booleans
        local = savedInstanceState.getBoolean("localS");
        onresume = savedInstanceState.getBoolean("onresumeS");
        onlogin = savedInstanceState.getBoolean("onloginS");
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
        //}
        onlogin = savedInstanceState.getBoolean("onloginS");
        Log.d("OnLogin", " onrestore: " + onlogin);
        if (!onlogin) { //hide login screen after resume
            ScrollViewGone(svLoginScreen);
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
        UnFocusEditText(svNameText);
        Log.d("OnLogin", " onresume: " + onlogin);
        if (!onlogin) {//hide login screen after resume
            ScrollViewGone(svLoginScreen);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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

    //creating random question answers
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

    // This method give me the next question
    // @NQN = Next Question Number
    public void GetQuestionInToArray(int NQN) throws IOException {
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
                UnFocusEditText(svNameText);
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
                UnFocusEditText(svEditBox);
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
                localScoreTable();
                pushInFirebase();
                CheckLevel();
                break;
        }

    }

    //this method will be se visibility in layers as gone to set app ready for next question
    public void hidealllayers() {
        ScrollViewGone(svStartupLayout);
        ScrollViewGone(svRadioGroupLayout);
        ScrollViewGone(svCheckBoxLayout);
        ScrollViewGone(svEditTextLayout);
        ScrollViewGone(svFinalResultLayout);
        ScrollViewGone(svStageLayers);
    }

    // this methode check RADIO  answeR ..
    public void CheckRadioAnswer() throws InterruptedException {
        String ans = "0";
        switch (svQuestionRadioGroup.getCheckedRadioButtonId()) {
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
        if (svcheckbox_1.isChecked()) {
            if ((QuestionArray[9].equals(Integer.toString(AnswearArray[0] - 1))) || (QuestionArray[10].equals(Integer.toString(AnswearArray[0] - 1)))) {
                counter = counter + 1;
            } else {
                counter = counter - 10;
            }
        }
        if (svcheckbox_2.isChecked()) {
            if ((QuestionArray[9].equals(Integer.toString(AnswearArray[1] - 1))) || (QuestionArray[10].equals(Integer.toString(AnswearArray[1] - 1)))) {
                counter = counter + 1;
            } else {
                counter = counter - 10;
            }
        }
        if (svcheckbox_3.isChecked()) {
            if ((QuestionArray[9].equals(Integer.toString(AnswearArray[2] - 1))) || (QuestionArray[10].equals(Integer.toString(AnswearArray[2] - 1)))) {
                counter = counter + 1;
            } else {
                counter = counter - 10;
            }
        }
        if (svcheckbox_4.isChecked()) {
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
        String ET2 = EdittTextReturn(svEditBox);
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
        AnswearAnimation(svcorrect);
        CNumber = CNumber + 1;
        LevelQuestionsScore = LevelQuestionsScore + Integer.parseInt(QuestionArray[11]);
    }

    public void WrongAnswear() throws InterruptedException {
        RadioAudioStop();
        RadioAudioStart2("wrong");
        AnswearAnimation(svwrong);
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
        ScrollViewVis(svRadioGroupLayout);
        SetTextView(QNumber + ". " + QuestionArray[1], svRadioGroupQuestion);
        SetRadioButton(QuestionArray[AnswearArray[0]], svradiobutton_1);
        SetRadioButton(QuestionArray[AnswearArray[1]], svradiobutton_2);
        SetRadioButton(QuestionArray[AnswearArray[2]], svradiobutton_3);
        SetRadioButton(QuestionArray[AnswearArray[3]], svradiobutton_4);
        CheckMediatype();

    }

    // This Method Apply question in CheckBox Question Layout
    public void SetCheckBoxQuestion() {
        Qtype = 2;
        ScrollViewVis(svCheckBoxLayout);
        SetTextView(QNumber + ". " + QuestionArray[1], svCheckBoxQuestion);
        SetCheckBox(QuestionArray[AnswearArray[0]], svcheckbox_1);
        SetCheckBox(QuestionArray[AnswearArray[1]], svcheckbox_2);
        SetCheckBox(QuestionArray[AnswearArray[2]], svcheckbox_3);
        SetCheckBox(QuestionArray[AnswearArray[3]], svcheckbox_4);
        CheckMediatype();
    }

    // This Method Apply question in EditText Question Layout
    public void SetEditTextQuestion() {
        Qtype = 3;
        ScrollViewVis(svEditTextLayout);
        SetTextView(QNumber + ". " + QuestionArray[1], svEditTextQuestion);
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
                setWebView("<font color=\"#5e3807\">" + getString(R.string.Stage1) + "<br /></font>", svStageLayersText);
                ScrollViewVis(svStageLayers);
                Qtype = 5;
                break;
            case 2://Stage 2 2/5 55sec
                LevelTime = 55000; // next level time
                if (CNumber >= 2) {
                    Pass(R.string.Stage2, 0);
                } else {
                    Cut(0);
                }
                break;
            case 3://Stage 3 3/5 55sec
                LevelTime = 55000;// next level time
                if (CNumber >= 2) {
                    Pass(R.string.Stage3, 1);
                } else {
                    Cut(0);
                }
                break;
            case 4://Stage 4 3/5 50sec
                LevelTime = 50000;// next level time
                if (CNumber >= 3) {
                    Pass(R.string.Stage4, 1);
                } else {
                    Cut(0);
                }
                break;
            case 5://Stage 5 4/5 50sec
                LevelTime = 50000;// next level time
                if (CNumber >= 3) {
                    Pass(R.string.Stage5, 2);
                } else {
                    Cut(1);
                }
                break;
            case 6://Stage 6 4/5 45sec
                LevelTime = 45000;// next level time
                if (CNumber >= 4) {
                    Pass(R.string.Stage6, 2);
                } else {
                    Cut(1);
                }
                break;
            case 7://Stage 7 5/5 40sec
                LevelTime = 40000;// next level time
                if (CNumber >= 4) {
                    Pass(R.string.Stage7, 3);
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
        try {
            congratsAnimation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TotalScoreCalculator(timesd, 0);
        String DisplayText = "<font color=\"#5e3807\">" + getString(val) + "<br />" + "<br />" +
                "<b>" + getString(R.string.QuestionLevelTotal) + " " + "</b>" + LevelQuestionsScore + "<br />" +
                "<b>" + getString(R.string.SecondsLeft) + "</b>" + " " + sec + getString(R.string.Sec) + "<br />" +
                "<b>" + getString(R.string.TimeBonus) + "</b>" + " " + sec * timesd + " " + getString(R.string.Points) + "<br />" +
                "<b>" + getString(R.string.TotalScore) + "</b>" + " " + TotalScore + "<br /></font>";
        Qtype = 5;
        setWebView(DisplayText, svStageLayersText);
        ScrollViewVis(svStageLayers);
        RadioAudioStart("win2");
    }

    // when we cut off next levell
    public void Cut(int timesd) {
        TotalScoreCalculator(timesd, 0);
        String DisplayText = "<font color=\"#5e3807\"><b>" + getString(R.string.QuestionLevelTotal) + "</b>" + " " + LevelQuestionsScore + "<br />" +
                "<b>" + getString(R.string.SecondsLeft) + "</b>" + " " + sec + getString(R.string.Sec) + "<br />" +
                "<b>" + getString(R.string.TimeBonus) + "</b>" + " " + sec * timesd + " " + getString(R.string.Points) + "<br />" +
                "<b>" + getString(R.string.TotalScore) + "</b>" + " " + TotalScore + "<br /></font>";
        Qtype = 6;
        setWebView(DisplayText, svStageLayersText);
        ScrollViewVis(svStageLayers);
        gameOver();
    }

    // when we Finish all levell
    public void FinalPass() {
        try {
            congratsAnimation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TotalScoreCalculator(4, 300);
        String DisplayText = "<font color=\"#5e3807\">" + getString(R.string.Final) + "<br />" + "<br />" +
                "<b>" + getString(R.string.QuestionLevelTotal) + "</b>" + " " + LevelQuestionsScore + "<br />" +
                "<b>" + getString(R.string.SecondsLeft) + "</b>" + " " + sec + getString(R.string.Sec) + "<br />" +
                "<b>" + getString(R.string.TimeBonus) + "</b>" + " " + sec * 4 + " " + getString(R.string.Points) + "<br />" +
                "<b>" + getString(R.string.TotalScore) + "</b>" + " " + TotalScore + "<br /></font>";
        Qtype = 6;
        setWebView(DisplayText, svStageLayersText);
        ScrollViewVis(svStageLayers);
        RadioAudioStart("win");
    }

    // when we cut off in last Level
    public void FinalCut() {
        TotalScoreCalculator(2, 0);
        String DisplayText = "<font color=\"#5e3807\"><b>" + getString(R.string.QuestionLevelTotal) + "</b>" + " " + LevelQuestionsScore + "<br />" +
                "<b>" + getString(R.string.SecondsLeft) + "</b>" + " " + sec + getString(R.string.Sec) + "<br />" +
                "<b>" + getString(R.string.TimeBonus) + "</b>" + " " + sec * 2 + " " + getString(R.string.Points) + "<br />" +
                "<b>" + getString(R.string.TotalScore) + "</b>" + " " + TotalScore + "<br /></font>";
        Qtype = 6;
        setWebView(DisplayText, svStageLayersText);
        ScrollViewVis(svStageLayers);
        gameOver();
    }

    //game Over effect

    public void gameOver() {
        try {
            gameOverAnimation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public void gameOverHide(View V) {
        ImageViewGone(svgameover);
        ImageViewGone(svcrack);
        ImageViewVis(svOkButton);
    }

    public void congratsHide(View V) {
        ImageViewGone(svcongrats);
        ImageViewGone(svfw);
        ImageViewVis(svOkButton);
        fwAnim.stop();
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
            TotalScore = (int) (TotalScore + sec * timesd + LevelQuestionsScore) + bonus;
        }

    }

    // this Method uncheck all check boxes
    public void UnCheckAllCheckBox() {
        UnCheckCheckBox(svcheckbox_1);
        UnCheckCheckBox(svcheckbox_2);
        UnCheckCheckBox(svcheckbox_3);
        UnCheckCheckBox(svcheckbox_4);
    }

    // this Method clear the previus text from edittext view
    public void UnCheckText() {
        SetEditText("", svEditBox);
    }

    // This Method Apply Results in Final Result Layout
    public void SetFinalResults() throws IOException {

        // LOCAL
        Qtype = 4;
        TinyDB tinydb = new TinyDB(this);
        String data = tinydb.getString("ScoreTable");
        String[] separated = data.split("\n");
        ScoreTableString = "<table width=\"100%\";>";
        ScoreTableString2 = "";
        for (int i = 0; i <= 9; ++i) {
            ScoreTableArray = (separated[i].split("\t"));
            if (i % 2 == 0) {
                ScoreTableString = ScoreTableString + "<tr style='background-color:#E2BC8B;color:#5e3807;'><td width=\"70%\"> " + (i + 1) + ". " + ScoreTableArray[0] + "</td>" + "<td>" + ScoreTableArray[1] + "</td></tr>";
            } else {
                ScoreTableString = ScoreTableString + "<tr style='background-color:#5e3807;color:#E2BC8B;'><td width=\"70%\"> " + (i + 1) + ". " + ScoreTableArray[0] + "</td>" + "<td>" + ScoreTableArray[1] + "</td></tr>";
            }
        }
        setWebView(ScoreTableString, svFinalResaltNames);


        // WEB

        firebaseScoretable();
        ScrollViewVis(svFinalResultLayout);
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
        ScrollViewVis(svStartupLayout);
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
            UnFocusEditText(svEditBox);
        }
        SetPhoto("paint");
    }


    // Set ScrollView Layer Visible - Gone... --------------------------------
    public void ScrollViewGone(ScrollView val) {
        val.setVisibility(View.GONE);
        val.scrollTo(0, 0);
    }

    public void ScrollViewVis(ScrollView val) {
        val.setVisibility(View.VISIBLE);
    }

    // Set ImageView Visible - Gone - panding... --------------------------------
    public void ImageViewGone(ImageView val) {
        val.setVisibility(View.GONE);
    }

    public void ImageViewInv(ImageView val) {
        val.setVisibility(View.INVISIBLE);
    }

    public void ImageViewVis(ImageView val) {
        val.setVisibility(View.VISIBLE);
    }

    public void ImageViewPading(ImageView val, int pad) {
        val.setPadding(pad, pad, pad, pad);
    }

    // TextView Text changer ----------------------------------------------------
    public void SetTextView(String what, TextView where) {
        where.setText(String.valueOf(what));
    }

    public void setWebView(String what, WebView where) {
        if (Build.VERSION.SDK_INT < 18) {
            where.clearView();
        } else {
            where.loadUrl("about:blank");
        }
        //where.getSettings().setUseWideViewPort(true);
        //where.getSettings().setLoadWithOverviewMode(true);
        where.loadData(String.valueOf(what), "text/html; charset=utf-8", "UTF-8");
        where.setBackgroundColor(Color.TRANSPARENT);
    }

    // EditText Text changer ----------------------------------------------------
    public void SetEditText(String what, EditText where) {
        where.setText(String.valueOf(what));
    }

    // EditTextReturn return ----------------------------------------------------
    public String EdittTextReturn(EditText val) {
        return val.getText().toString();
    }

    // EditText Close keyboard after OK Button pressed  --------------------------------
    public void UnFocusEditText(EditText where) {
        where.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(where.getWindowToken(), 0);
    }

    // RadioButton Text changer ----------------------------------------------------
    public void SetRadioButton(String what, RadioButton where) {
        where.setText(String.valueOf(what));
    }

    // RadioButton Uncheck  ----------------------------------------------------
    public void UnCheckRadioButton() {
        svQuestionRadioGroup.clearCheck();
    }

    // CheckBox Text changer ----------------------------------------------------
    public void SetCheckBox(String what, CheckBox where) {
        where.setText(String.valueOf(what));
    }

    // CheckBox UnCheck ----------------------------------------------------
    public void UnCheckCheckBox(CheckBox where) {
        where.setChecked(false);
    }

    // Frame Photo changer ----------------------------------------------------
    public void SetPhoto(String val) {
        int resId = getResources().getIdentifier(val, "drawable", getPackageName()); // change string to id
        svPhoto.setImageResource(resId);
    }

    // Frame Photo changer ----------------------------------------------------
    public void SetClock(int val) {
        svclock1.setImageResource(this.clocknames[val]);
    }


    // Radio Animation Start - Stop   ----------------------------------------------------
    public void RadioAnimationStart() {
        svradio.setBackgroundResource(R.drawable.radio);
        AnimationDrawable RadioAnimation = (AnimationDrawable) svradio.getBackground();
        RadioAnimation.start();
        ImageViewVis(svradio);
        ImageViewGone(svradio2);
    }

    public void RadioAnimationStop() {
        svradio.setBackgroundResource(R.drawable.radio);
        AnimationDrawable RadioAnimation = (AnimationDrawable) svradio.getBackground();
        RadioAnimation.stop();
        ImageViewVis(svradio2);
        ImageViewGone(svradio);
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

    public void AnswearAnimation(final ImageView val) throws InterruptedException {
        Animation zoomAnimation = AnimationUtils.loadAnimation(this, zoom);
        ImageViewVis(val);
        val.startAnimation(zoomAnimation);
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

    // GAME OVER ANIMATION

    public void gameOverAnimation() throws InterruptedException {
        Animation zoomAnimation2 = AnimationUtils.loadAnimation(this, zoom2);
        ImageViewVis(svgameover);
        svgameover.startAnimation(zoomAnimation2);
        Log.d("lpgd GameOver", "1");
        zoomAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ImageViewGone(svOkButton);
                RadioAudioStart("gameover");
                ImageViewVis(svgameover);
                Log.d("lpgd GameOver", "start");

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ImageViewVis(svcrack);
                RadioAudioStart("crack");
                Log.d("lpgd GameOver", "stop");

            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    // Vogratulation ANIMATION

    public void congratsAnimation() throws InterruptedException {
        Animation zoomAnimation3 = AnimationUtils.loadAnimation(this, zoom3);
        ImageViewVis(svcongrats);
        Log.d("lpgd Congrats", "1");
        svcongrats.startAnimation(zoomAnimation3);
        zoomAnimation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("lpgd Congrats", "Start");
                ImageViewGone(svOkButton);
                RadioAudioStart("ohyeah");
                ImageViewVis(svcongrats);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("lpgd Congrats", "Stop");
                fwAnim.start();
                ImageViewVis(svfw);

            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    //Clock Animation and timer
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


    // firebase scoretable

    private void pushInFirebase() {
        Log.d("pushInFirebase ", " sFUID" + sFUID + " loginMethod" + loginMethod);
        if ((sFUID != null) && (loginMethod != 3)) {
            tempB = false;
            final DatabaseReference player = database.getReference("score table new");
            Query pendingTasks = player.orderByChild("firebaseUID").equalTo(sFUID);
            pendingTasks.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot tasksSnapshot) {
                    for (DataSnapshot snapshot : tasksSnapshot.getChildren()) {
                        tempB = true;
                        if ((Long) snapshot.child("firebaseScore").getValue() < TotalScore) {
                            Log.d("pushInFirebase ", " IN 1");
                            snapshot.getRef().child("firebaseName").setValue(PlayerName);
                            snapshot.getRef().child("firebaseScore").setValue(TotalScore);
                        }
                    }
                    if (!tempB) {
                        Log.d("pushInFirebase ", " IN2 ");
                        String key = scoresFirabase.push().getKey();
                        scoresFirabase.child(key).child("firebaseName").setValue(PlayerName);
                        scoresFirabase.child(key).child("firebaseUID").setValue(sFUID);
                        scoresFirabase.child(key).child("firebaseScore").setValue(TotalScore);
                    }
                    firebaseScoretable();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }

    private void firebaseScoretable() {
        sFName = "</table>";
        sFScore = "";
        final DatabaseReference player = database.getReference("score table new");
        player.orderByChild("firebaseScore").limitToLast(30).addChildEventListener(new ChildEventListener() {
            int classification = 30;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Player playerRef = dataSnapshot.getValue(Player.class);
                Log.d("testing", "counter:" + classification + " Name:" + sFName);

                if (classification > 0) {
                    if (classification % 2 == 0) {
                        sFName = "<tr style='background-color:#E2BC8B;color:#5e3807;'><td width=\"70%\"> " + (classification) + ". " + playerRef.firebaseName + "</td>" + "<td>" + Integer.toString(playerRef.firebaseScore) + "</td></tr>" + sFName;
                    } else {
                        sFName = "<tr style='background-color:#5e3807;color:#E2BC8B;'><td width=\"70%\"> " + (classification) + ". " + playerRef.firebaseName + "</td>" + "<td>" + Integer.toString(playerRef.firebaseScore) + "</td></tr>" + sFName;
                    }
                    sFName = "<table width=\"100%\";>" + sFName;
                    setWebView(sFName, svFBFinalResaltNames);
                    classification = classification - 1;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                sFName = "";
                sFScore = "";
            }
        });


        // check firebase connectivity

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                } else {
                    setWebView(getString(R.string.noInternet), svFBFinalResaltNames);
                    setWebView("", svFBFinalResaltScore);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

    }


    // Local Score table mehtods

    private void localScoreTable() throws IOException {
        //https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo
        //http://stackoverflow.com/questions/7057845/save-arraylist-to-sharedpreferences

        TinyDB tinydb = new TinyDB(this);

        //restore string
        String data = tinydb.getString("ScoreTable");

        // If string is empty
        if (data.equals("")) {
            // fill table with start values
            for (int i = 0; i <= 9; ++i) {
                data = data + "Player" + "\t" + "0" + "\n";
            }
        }
        //put string in to table
        ScoreTableArrayList.clear();
        String[] separated = data.split("\n");
        for (int i = 0; i <= 9; ++i) {
            ScoreTableArray = (separated[i].split("\t"));
            ScoreTableArrayList.add(i, ScoreTableArray);
        }

        //sort table
        // Sorting Procedure and adding new score
        ScoreTableArray = ScoreTableArrayList.get(9);
        if (Integer.parseInt(ScoreTableArray[1]) < TotalScore) {
            if (PlayerName.equals("")) PlayerName = "No Name"; // check if player name is empty
            ScoreTableArray[0] = PlayerName;
            ScoreTableArray[1] = Integer.toString(TotalScore);
            ScoreTableArrayList.set(9, ScoreTableArray);
            int i = 8;
            boolean done = false;
            while (i >= 0 && !done) {
                ScoreTableArray = ScoreTableArrayList.get(i);
                ScoreTableArray2 = ScoreTableArrayList.get(i + 1);
                if (Integer.parseInt(ScoreTableArray[1]) < Integer.parseInt(ScoreTableArray2[1])) {
                    ScoreTableArrayList.set(i, ScoreTableArray2);
                    ScoreTableArrayList.set(i + 1, ScoreTableArray);
                    i = i - 1;
                } else {
                    done = true;
                }
            }
        }

        //save string
        data = "";
        // Recreate data string
        for (int i = 0; i <= 9; ++i) {
            ScoreTableArray = ScoreTableArrayList.get(i);
            data = data + ScoreTableArray[0] + "\t" + ScoreTableArray[1] + "\n";
        }
        tinydb.putString("ScoreTable", data);
    }

    // Language methods
    // check locatrion and if it's greek set the question file this with greeks.

    public void CheckLanguageMehtod() {
        if (Locale.getDefault().getLanguage().contentEquals("el")) {
            QuestionLangSet = "questionsfinal-el.txt";
        } else {
            QuestionLangSet = "questionsfinal.txt";
        }

    }


    // login screen methods

    // email registration method
    public void emailRegisterMethod(View v) {
        FirebaseAuth.getInstance().signOut();
        PlayerName = EdittTextReturn(svNameText);
        PlayerEmail = EdittTextReturn(svEmailEditBox);
        PlayerPassword = EdittTextReturn(svPasswordEditBox);

        if ((PlayerName.equals("")) || (PlayerName.length() >= 3 && PlayerName.length() <= 20)) {
            if (isInternetConected()) { // check for internet connection
                if (isEmailValid(PlayerEmail) && PlayerPassword.length() >= 6) { // check if email and password is in correct format
                    mAuth.createUserWithEmailAndPassword(PlayerEmail, PlayerPassword)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) { // check if registration is succesful
                                            Toast.makeText(MainActivity.this, getString(R.string.toastLoginMessage1a1), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, getString(R.string.toastLoginMessage1a2), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, getString(R.string.toastLoginMessage2), Toast.LENGTH_SHORT).show();
                                        loginMethod = 1; // email
                                        FirebaseUser user = task.getResult().getUser();
                                        sFUID = user.getUid();
                                        PlayerName = user.getDisplayName();
                                        if (PlayerName == null || PlayerName.equals("")) {
                                            PlayerName = EdittTextReturn(svNameText);
                                            if (PlayerName == null || PlayerName.equals("")) {
                                                PlayerName = PlayerEmail;
                                            }
                                        }
                                        Log.d("sFUID: ", " getUserID: " + sFUID);
                                        leaveLoginScreen();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(this, getString(R.string.toastLoginMessage3), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.toastLoginMessage4), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.toastNickNameCheck), Toast.LENGTH_SHORT).show();
        }

    }


    // email login method
    public void emailLoginMethod(View v) {
        FirebaseAuth.getInstance().signOut();
        PlayerName = EdittTextReturn(svNameText);
        PlayerEmail = EdittTextReturn(svEmailEditBox);
        PlayerPassword = EdittTextReturn(svPasswordEditBox);

        if ((PlayerName.equals("")) || (PlayerName.length() >= 3 && PlayerName.length() <= 20)) {
            if (isInternetConected()) { // check for internet connection
                if (isEmailValid(PlayerEmail) && PlayerPassword.length() >= 6) { // check if email and password is in correct format
                    mAuth.signInWithEmailAndPassword(PlayerEmail, PlayerPassword)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        if (task.getException() instanceof FirebaseAuthInvalidUserException) { // check if registration is succesful
                                            Toast.makeText(MainActivity.this, getString(R.string.toastLoginMessage1b1), Toast.LENGTH_SHORT).show();
                                            Log.d("Authentication Email :", "invalid user name");
                                        } else {
                                            Toast.makeText(MainActivity.this, getString(R.string.toastLoginMessage1b2), Toast.LENGTH_SHORT).show();
                                            Log.d("Authentication Email :", "other error");
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, getString(R.string.toastLoginMessage2b), Toast.LENGTH_SHORT).show();
                                        loginMethod = 1; // email
                                        FirebaseUser user = task.getResult().getUser();
                                        sFUID = user.getUid();
                                        PlayerName = user.getDisplayName();
                                        if (PlayerName == null || PlayerName.equals("")) {
                                            PlayerName = EdittTextReturn(svNameText);
                                            if (PlayerName == null || PlayerName.equals("")) {
                                                PlayerName = PlayerEmail;
                                            }
                                        }
                                        Log.d("sFUID: ", " getUserID: " + sFUID);
                                        leaveLoginScreen();

                                    }
                                }
                            });
                } else {
                    Toast.makeText(this, getString(R.string.toastLoginMessage3), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.toastLoginMessage4), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.toastNickNameCheck), Toast.LENGTH_SHORT).show();
        }

    }


    // login with google method
    public void googleLoginMethod(View v) {
        //Toast.makeText(MainActivity.this, getString(R.string.toastGoogleMessage1a1), Toast.LENGTH_SHORT).show();
        loginMethod = 2; //google
        PlayerName = EdittTextReturn(svNameText);
        if ((PlayerName.equals("")) || (PlayerName.length() >= 3 && PlayerName.length() <= 20)) {
            if (isInternetConected()) { // check for internet connection
                signIn();
            } else {
                Toast.makeText(this, getString(R.string.toastLoginMessage4), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.toastNickNameCheck), Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    public void googleSignOutMethod(View v){
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("logout", "User Logged out");
                                signIn();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("logout", "Google API Client Connection Suspended");
            }
        });
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                sFUID = account.getId();
                if (PlayerName.equals("")) {
                    PlayerName = account.getDisplayName();
                }
                firebaseAuthWithGoogle(account);
                Toast.makeText(MainActivity.this, getString(R.string.toastLoginMessage2b), Toast.LENGTH_SHORT).show();
                Log.d("Authentication G1 :", "correct" + " getUserID: " + sFUID);
            } else {
                Log.d("Authentication G1 :", "incorrect");
                Toast.makeText(this, getString(R.string.toastGoogleMessage1a1), Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Authentication G2 :", "correct");

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.d("Authentication G2 :", "incorrect");
                            //Log.w(TAG, "signInWithCredential", task.getException());
                            //Toast.makeText(GoogleSignInActivity.this, "Authentication failed.",
                            //Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
        leaveLoginScreen();

    }


    // No login  method
    public void noLoginMethod(View v) {
        if (PlayerName.length() >= 3 && PlayerName.length() <= 20) {
            loginMethod = 3; // no login
            PlayerName = EdittTextReturn(svNameText);
            leaveLoginScreen();
        } else {
            Toast.makeText(this, getString(R.string.toastNickNameCheck), Toast.LENGTH_SHORT).show();
        }
    }

    public void LoginScreenShowUp(View v) {
        Log.d("OnLogin", " login screen showup: " + onlogin);
        onlogin = true;
        ScrollViewVis(svLoginScreen);
    }


    /**
     * method is used for checking valid email id format.
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    //leave login page

    public void leaveLoginScreen() {
        Log.d("OnLogin", " leaveLoginScreen: " + onlogin);
        onlogin = false;
        UnFocusEditText(svNameText);
        UnFocusEditText(svEmailEditBox);
        UnFocusEditText(svPasswordEditBox);
        if (loginMethod == 1) {
            ScrollViewGone(svLoginScreen);
        } else if (loginMethod == 2) {
            ScrollViewGone(svLoginScreen);
        } else {
            ScrollViewGone(svLoginScreen);
        }
    }


    // this method return user id
    public String getUserID() {

        //Firebase  authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    tempuser = user.getUid();
                    Log.d("Signed_in:", user.getUid());
                } else {
                    tempuser = null;
                    // User is signed out
                    Log.d("Not Signed_in:", user.getUid());
                }
                // ...
            }
        };
        return tempuser;
    }


    // this method check if we are login in firebase
    public boolean isAuthenticated() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Authentication check :", "correct");
                    tempB = true;
                } else {
                    Log.d("Authentication check :", "incorrect");
                    tempB = false;
                }
                // ...
            }
        };
        return tempB;
    }


    // this method check if we are login in firebase
    public boolean isInternetConected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
        }
        return connected;
    }
}



