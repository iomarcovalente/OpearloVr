package marco.io.opearlovr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.vrtoolkit.cardboard.CardboardActivity;

import org.rajawali3d.cardboard.RajawaliCardboardRenderer;
import org.rajawali3d.cardboard.RajawaliCardboardView;

import java.util.List;

public class StoryLineActivity extends CardboardActivity
        implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener {

    public OverlayView mOverlayView;

    private static final int SPEECH_REQUEST_CODE = 0;

//    private ProgressBar mProgressBar;
    private int mDelay = 500;

//    public Button beginBtn;
//    public Button switchBtn;

    private static final String TAG = "MyStt3Activity";
    private SpeechRecognizer sr;
//    public String speechResult = "...";
    public int picAddress;
    public String[] introAudio;

//    String[] introChapter;

    String[] oscarOpt = {"oscar","offender","pistorius","man","him","his","he","purchase"};
    String[] ladyOpt = {"lady","witness","her","she","her"};
    //int messageCount=introChapterOne.length;
    // to keep current Index of text
    //int currentIndex=-1;
    int tap = 0;
    public boolean intro = false;
    public boolean loading = true;

    private MediaPlayer mMediaPlayer;
    //private final Handler handler = new Handler(); // for visible updates on a progress bar for mp3 files

    //intro mp3 https://s3.amazonaws.com/iomarco-projects/introRec.mp3
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_story_line);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);

        RajawaliCardboardView view = (RajawaliCardboardView) findViewById(R.id.rajawali_cardboardView);
        setCardboardView(view);

        picAddress = getIntent().getExtras().getInt(MainActivity.EXTRA_PICADDRESS);
        introAudio = getIntent().getExtras().getStringArray(MainActivity.EXTRA_INTROAUDIO);

        final RajawaliCardboardRenderer renderer = new MyRenderer(this, picAddress);

        view.setRenderer(renderer);
        view.setSurfaceRenderer(renderer);

//        introChapter = res.getStringArray(R.array.chapterOne);

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

        mOverlayView = (OverlayView) findViewById(R.id.overlay);

        mOverlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String s = "";
//                for(int index = 0; index<((ViewGroup)view).getChildCount(); ++index) {
//                    View nextChild = ((ViewGroup)view).getChildAt(index);
//                    s += nextChild.getId();
//                    s += " ";
//                }
//                Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
                if (loading) {
                    Toast.makeText(getBaseContext(), "Still Loading", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!loading && intro) {
//                    try {
//                        mOverlayView.setContent(introChapter[introIdx]);
//                        introIdx++;
//                    } catch (Exception e){
//                        intro = false;
//                        e.printStackTrace();
//                    }
                    if (!mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                    } else {
                        mMediaPlayer.pause();
                    }
                }
                if (!loading && !intro) {
                    mMediaPlayer.reset();
                    startSpeechRecognizer();
                }
            }
        });

//        beginBtn = (Button) findViewById(R.id.beginBtn);
//        switchBtn = (Button) findViewById(R.id.switchBtn);
//        textSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);
//        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
//        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
//
//            public View makeView() {
//                // create new textView and set the properties like color, size etc
//                TextView myText = new TextView(StoryLineActivity.this);
//                myText.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//                myText.setTextSize(18);
//                myText.setTextColor(Color.WHITE) ;
//                return myText;
//            }
//        });
//
//        // Declare the in and out animations and initialize them
//        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
//        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
//        // set the animation type of textSwitcher
//        textSwitcher.setInAnimation(in);
//        textSwitcher.setOutAnimation(out);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

//        Intent intent = getIntent();
//        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
//        textView = (TextView) findViewById(R.id.fullscreen_content);
//        textView.setText(message);

//        mImageView = (ImageView) findViewById(R.id.imageView);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//        sr = SpeechRecognizer.createSpeechRecognizer(this);
//        sr.setRecognitionListener(new listener());
//
//
//        beginBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startSpeechRecognizer();
//            }
//        });
//
//        switchBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentIndex++;
//                // If index reaches maximum reset it
//                if(currentIndex==messageCount)
//                    currentIndex=0;
//                textSwitcher.setText(textToShow[currentIndex]);
//            }
//        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        new LoadCrimeScene().execute();
//        try {
//            mMediaPlayer.setDataSource(introMp3[introIdx]);
//            mMediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (mMediaPlayer != null) mMediaPlayer.release();
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        if( tap == 2 ) {
            setResult(RESULT_OK,data);
        }
        else setResult(RESULT_CANCELED, data);
        super.finish();
    }
    /*
    @Override
    protected void onResume(){
        super.onResume();

    }
    */


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        loading = mOverlayView.setProgress(percent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (!loading && !intro) {
            mOverlayView.setBackgroundColor(Color.argb(0, 49, 89, 106));
            mOverlayView.setContent("");
            intro = true;
        }
        mMediaPlayer.start();
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        /** MediaPlayer onCompletion event handler. Method which calls when song playing is complete*/
        intro = false;
        if (tap == 2) {
            finish();
        }
    }

    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndOfSpeech");
        }
        public void onError(int error) {
            Log.d(TAG,  "error " +  error);
            //textView.setText("error " + error);
        }
        public void onResults(Bundle results)
        {
            String str = new String();

            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            str = data.get(0).toString();
//            if (data.get(0).toLowerCase().contains("oscar")|data.get(0).equals("more")){
//                currentIndex++;
//                // If index reaches maximum reset it
//                if(currentIndex==messageCount)
//                    currentIndex=0;
//                textSwitcher.setText(textToShow[currentIndex]);
//            }
            if (stringContainsItemFromList(str.toLowerCase(),oscarOpt)){
                try {
                    mMediaPlayer.setDataSource(introAudio[1]);
                    mMediaPlayer.prepare();
                    tap++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (stringContainsItemFromList(str.toLowerCase(),ladyOpt)){
                try {
                    mMediaPlayer.setDataSource(introAudio[2]);
                    mMediaPlayer.prepare();
                    tap++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < data.size(); i++) {
                Log.d(TAG, "result " + data.get(i));
            }
            //textView.setText("results: "+String.valueOf(data.size()));
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    public void startSpeechRecognizer() {

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
            sr.startListening(intent);
            Log.i("111111","11111111");

    }

    class LoadCrimeScene extends AsyncTask<Void,Integer ,Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            loading = mOverlayView.setProgress(values[0]);
//            if (!loading) intro = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // simulating long-running operation
//            for (int j = 0, i = 0; i < 100; j = (int)((Math.random())*10)+5) {
//                sleep();
//                i += j;
//                publishProgress(i);
//            }
            sleep();
            try {
                mMediaPlayer.setDataSource(introAudio[0]);
                mMediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

        }

        private void sleep() {
            try {
                Thread.sleep(mDelay);
            } catch (InterruptedException e) {
                System.out.println("* * * An error occurred : "+e+" * * *");
            }
        }
    }

//    // Create an intent that can start the Speech Recognizer activity
//    private void displaySpeechRecognizer() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//    // Start the activity, the intent will be populated with the speech text
//        startActivityForResult(intent, SPEECH_REQUEST_CODE);
//    }

//    // This callback is invoked when the Speech Recognizer returns.
//    // This is where you process the intent and extract the speech text from the intent.
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,
//                                    Intent data) {
//        String spokenText = "";
//        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
//            List<String> results = data.getStringArrayListExtra(
//                    RecognizerIntent.EXTRA_RESULTS);
//            spokenText = results.get(0);
//            textView.setText("You said:\n" + spokenText);
//        }
//        if(spokenText != "") {
//            if ( spokenText.equals("next") ) new LoadCrimeScene().execute(R.mipmap.ferry);
//            else Toast.makeText(this, "Choice not valid", Toast.LENGTH_SHORT).show();
//
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    public static boolean stringContainsItemFromList(String inputString, String[] items)
    {
        for(int i =0; i < items.length; i++)
        {
            if(inputString.contains(items[i]))
            {
                return true;
            }
        }
        return false;
    }
}
