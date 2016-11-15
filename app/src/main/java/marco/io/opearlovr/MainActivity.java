package marco.io.opearlovr;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity  implements
        GoogleApiClient.OnConnectionFailedListener {


    public final static String EXTRA_MESSAGE = "io.marco.opearlovr.MESSAGE";
    public static final String ANONYMOUS = "anonymous";

    private Context mContext;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;

    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;

    private static final long INITIAL_ALARM_DELAY = 30 * 1000L;
    private static boolean NOTIFICATION_STATE = true;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    private static final String TAG_OPT = "OptionsDialog";

    @Override
    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("notifState", NOTIFICATION_STATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mContext=getApplicationContext();

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(MainActivity.this,
                AlarmNotificationReceiver.class);
        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, mNotificationReceiverIntent, 0);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
        }


        // Initialize UI elements
        final TextView usernameView = (TextView) findViewById(R.id.welcomeUser);
        usernameView.setText("Welcome to Virtual Crime " + mUsername );

        final Button optionsBtn =(Button)  findViewById(R.id.optionBtn);
        final Button quitBtn = (Button) findViewById(R.id.quitBtn);

        optionsBtn.setOnClickListener(new View.OnClickListener() {

            // Called when user clicks the options button
            public void onClick(View v) {
                OptionsDialogClass optionsDial = new OptionsDialogClass(MainActivity.this);
                optionsDial.show();
            }
        });
        quitBtn.setOnClickListener(new View.OnClickListener() {

            // Called when user clicks the quit button
            public void onClick(View v) {
                finish();
            }
        });

    }
    /** Called when the user clicks the start button */
    public void startNewGame(View view) {
        Intent intent = new Intent(this, StoryLineActivity.class);
/*        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();*/
        intent.putExtra(EXTRA_MESSAGE, "Loading\nnew story");
        startActivity(intent);
    }

    public class OptionsDialogClass extends Dialog implements
            android.view.View.OnClickListener {

        public Activity mActivity;
        public Switch soundSwitch, notifSwitch;
        public SeekBar speedBar;
        public Button signOutBtn;
        public Boolean mSwitch=false;

        public OptionsDialogClass(Activity activity) {
            super(activity);
            this.mActivity = activity;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (savedInstanceState != null) mSwitch = savedInstanceState.getBoolean("notifState");

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.options_dialog);
            soundSwitch = (Switch) findViewById(R.id.soundSwitch);
            notifSwitch = (Switch) findViewById(R.id.notifSwitch);
            speedBar = (SeekBar) findViewById(R.id.speedBar);
            signOutBtn = (Button) findViewById(R.id.signOutBtn);

            soundSwitch.setOnClickListener(this);
            speedBar.setOnClickListener(this);
            signOutBtn.setOnClickListener(this);

            notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub

                    if (buttonView.isChecked()) {
                        NOTIFICATION_STATE = true;
                        initNotifications(NOTIFICATION_STATE);
                    }
                    else {
                        NOTIFICATION_STATE = false;
                        initNotifications(NOTIFICATION_STATE);
                    }


                }
            });
            notifSwitch.setChecked(NOTIFICATION_STATE);
            initNotifications(NOTIFICATION_STATE);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.notifSwitch:
//                    initNotifications(notifSwitch.isChecked());
//                    break;
                case R.id.signOutBtn:
                    Toast.makeText(mContext, "Signing Out...",Toast.LENGTH_SHORT).show();
                    mFirebaseAuth.signOut();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    mFirebaseUser = null;
                    mUsername = ANONYMOUS;
                    startActivity(new Intent(mContext, SignInActivity.class));
                    break;
//                case R.id.soundSwitch:
//                    mAlarmManager.set(AlarmManager.RTC_WAKEUP,
//                            System.currentTimeMillis() + INITIAL_ALARM_DELAY,
//                            mNotificationReceiverPendingIntent);
//                    break;
//                case R.id.speedBar:
//                    if (mAlarmManager != null)
//                        mAlarmManager.cancel(mNotificationReceiverPendingIntent);
//                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    public void initNotifications(Boolean b) {
        Toast.makeText(mContext, "initializing notifications ... ",Toast.LENGTH_SHORT).show();
        if (b && mAlarmManager != null) {
            mAlarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                    AlarmManager.INTERVAL_DAY,
                    mNotificationReceiverPendingIntent);
        }
        else if (!b && mAlarmManager != null)
            mAlarmManager.cancel(mNotificationReceiverPendingIntent);
    }
    /*@Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first

        // Save the note's current draft, because the activity is stopping
        // and we want to be sure the current note progress isn't lost.
        ContentValues values = new ContentValues();
        values.put(NotePad.Notes.COLUMN_NAME_NOTE, getCurrentNoteText());
        values.put(NotePad.Notes.COLUMN_NAME_TITLE, getCurrentNoteTitle());

        getContentResolver().update(
                mUri,    // The URI for the note to update.
                values,  // The map of column names and new values to apply to them.
                null,    // No SELECT criteria are used.
                null     // No WHERE columns are used.
        );
    }*/
    /*@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
        savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }*/

   /* public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
        mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
    }*/

   /* // TO SAVE KEY-VALUE SETS OF DATA
    Context context = getActivity();
    SharedPreferences sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE); // get an handle to a SharedPreferences file
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putInt(getString(R.string.saved_options), options);
    editor.commit(); // write on shared preferences

    int defaultValue = getResources().getInteger(R.string.saved_options_default);
    long highScore = sharedPref.getInt(getString(R.string.saved_options), defaultValue);

   */

    // TO SAVE DATA ON THE EXTERNAL STORAGE
    /*// Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Checks if external storage is available to at least read
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }*/

    // TO CHECK THE NETWORK CONNECTION
    /*ConnectivityManager connMgr = (ConnectivityManager)
            getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
        // fetch data
    } else {
        // display error
    }*/
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
