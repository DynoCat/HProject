package com.example.x.googleapi_prototype;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.hardware.*;

import com.example.x.googleapi_prototype.util.ActivityConstants;
import com.example.x.googleapi_prototype.util.ActivityData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener //, ActivityResultReceiver.Receiver
{

    private static final String TAG = "Main Activity - ";

    public GoogleApiClient mApiClient;
    private Handler mGUIHandler;

    private SensorManager mSensorManager;
    boolean mActivityRunning;

    private String mActivityStartTime = null;
    private String mActivityEndTime = null;

    private int mPreviousActivity = -1;
    private ActivityData mPreviousActivityObject;

    private TextView mProgress;
    private ProgressBar mProgressBar;
    private int mPedometerStatus = 0;
    private Handler mProgressHandler = new Handler();


    /*
        WRITE ABOUT BROADCAST RECEIVER VS RESULT REC. IN FAVOUR OF RESULT REC - Result rec. is used when only our own (1) application needs the data
        whereas broadcast receiver can pass to multiple apps.
     */

    TextView stillProbabilityTextView, tiltingProbabilityTextView,
            onFootProbabilityTextView, runningProbabilityTextView,
            walkingProbabilityTextView, onBicycleProbabilityTextView,
            inVehicleProbabilityTextView, unknownActivityProbabilityTextView;

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stillProbabilityTextView = (TextView) findViewById(R.id.stillProbabilityTextView);
        tiltingProbabilityTextView = (TextView) findViewById(R.id.tiltingProbabilityTextView);
        onFootProbabilityTextView = (TextView) findViewById(R.id.onFootProbabilityTextView);
        runningProbabilityTextView = (TextView) findViewById(R.id.runningProbabilityTextView);
        walkingProbabilityTextView = (TextView) findViewById(R.id.walkingProbabilityTextView);
        onBicycleProbabilityTextView = (TextView) findViewById(R.id.onBicycleProbabilityTextView);
        inVehicleProbabilityTextView = (TextView) findViewById(R.id.inVehicleProbabilityTextView);
        unknownActivityProbabilityTextView = (TextView) findViewById(R.id.unknownActivityProbabilityTextView);
        mImageView = (ImageView) findViewById(R.id.mImageView);

        mProgress = (TextView) findViewById(R.id.mProgress);
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);

        mApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .build();
        mApiClient.connect();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mGUIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(mPedometerStatus <= 100) {
                    mGUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences aSharedPref = getSharedPreferences("pedometer_data", Context.MODE_PRIVATE);
                            int aPercent = (aSharedPref.getInt("pedometer_count", 0) * 100) / 6000;
                            mProgressBar.setProgress(aPercent);
                            mProgress.setText(Integer.toString(aPercent)+ "%");
                            mPedometerStatus = aPercent;
                            Log.d(TAG, "HANDLER - PEDOMETER COUNT: " + aSharedPref.getInt("pedometer_count", 0));
                        }
                    });
                } else {
                    mProgress.setText("You have reached your daily objective.");
                }
                Bundle reply = msg.getData();
                ArrayList<ActivityData> aActivityDataList = reply.getParcelableArrayList("data_array"); //2
                Log.d(TAG, "Size: " + aActivityDataList.size());

                if (mPreviousActivity == -1) {
                    mPreviousActivityObject = aActivityDataList.get(0); //1
                    mPreviousActivity = aActivityDataList.get(0).getIntActivityType(); //1
                }

                if (mPreviousActivity == aActivityDataList.get(0).getIntActivityType()) {
                    mActivityStartTime = mPreviousActivityObject.getFormattedTimeStamp();
                    mActivityEndTime = aActivityDataList.get(0).getFormattedTimeStamp();
                    String aCurrentElapsedTime = getElapsedActivityTime(mActivityStartTime,
                            mActivityEndTime,
                            mPreviousActivityObject);
                    //Toast.makeText(MainActivity.this, aCurrentElapsedTime, Toast.LENGTH_SHORT).show();
                    notifyElapsedTime(aCurrentElapsedTime);

                } else {
                    switch (aActivityDataList.get(0).getIntActivityType()) {
                        case ActivityConstants.ACTIVITY_IN_VEHICLE:
                            mImageView.setImageResource(R.drawable.in_vehicle);
                            break;
                        case ActivityConstants.ACTIVITY_ON_BICYCLE:
                            mImageView.setImageResource(R.drawable.on_bicycle);
                            break;
                        case ActivityConstants.ACTIVITY_ON_FOOT:
                            mImageView.setImageResource(R.drawable.on_foot);
                            break;
                        case ActivityConstants.ACTIVITY_STILL:
                            mImageView.setImageResource(R.drawable.still);
                            break;
                        case ActivityConstants.ACTIVITY_UNKNOWN:
                            mImageView.setImageResource(R.drawable.unknown_activity);
                            break;
                        case ActivityConstants.ACTIVITY_TILTING:
                            mImageView.setImageResource(R.drawable.tilting_activity);
                            break;
                        case ActivityConstants.ACTIVITY_WALKING:
                            mImageView.setImageResource(R.drawable.walking_activity);
                            break;
                        case ActivityConstants.ACTIVITY_RUNNING:
                            mImageView.setImageResource(R.drawable.running_activity);
                            break;
                        default:
                            mImageView.setImageDrawable(null);
                    }
                    mPreviousActivity = aActivityDataList.get(0).getIntActivityType(); //2
                    mPreviousActivityObject = aActivityDataList.get(0);
                }
            }
        };
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Intent intent = new Intent(MainActivity.this, ActivityRecognitionService.class);
        intent.putExtra("messenger", new Messenger(mGUIHandler));
        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);
        Log.d(TAG, "onConnected()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, " - Connection failed (onConnectionFailed()");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended: retrying");
        mApiClient.connect();
    }

    public ActivityData maxConfidence(ArrayList<ActivityData> pActivityList) {
        int aHighestConfidence = pActivityList.get(0).getActivityConfidence();
        String c = pActivityList.get(0).getStringActivityType();
        Log.d(TAG, " " + aHighestConfidence + "  " + c);
        ActivityData aMostProbableActivity = pActivityList.get(0);
        for (int aIndex = 1; aIndex < pActivityList.size() - 1; aIndex++) {
            if (pActivityList.get(aIndex).getActivityConfidence() > aHighestConfidence) {
                aMostProbableActivity = pActivityList.get(aIndex);
            }
        }
        Log.d(TAG, "Activity is " + aMostProbableActivity.getStringActivityType() + " " + aMostProbableActivity.getIntActivityType());
        return aMostProbableActivity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " onResume()");

        mActivityRunning = true;
        Sensor aCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(aCountSensor != null) {
            mSensorManager.registerListener(this, aCountSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Pedometer not available.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, " onPause()");
        super.onPause();
        mActivityRunning = false;
    }

    public String getElapsedActivityTime(String pActivityStartTime, String pActivityEndTime, ActivityData pActivityObject) {

        Date aD1;
        Date aD2;
        SimpleDateFormat aFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

        String aFinalElapsedTime = "";
        try {
            aD1 = aFormat.parse(pActivityStartTime);
            aD2 = aFormat.parse(pActivityEndTime);

            long aDiff = aD2.getTime() - aD1.getTime();

            long aDiffSeconds = aDiff / 1000 % 60;
            long aDiffMinutes = aDiff / (60 * 1000) % 60;
            long aDiffHours = aDiff / (60 * 60 * 1000) % 24;
            long aDiffDays = aDiff / (24 * 60 * 60 * 1000);

            if (aDiffDays == 0 && aDiffHours == 0 && aDiffMinutes == 0) {
                aFinalElapsedTime = Long.toString(aDiffSeconds) + " seconds.";
            } else if (aDiffDays == 0 && aDiffHours == 0) {
                aFinalElapsedTime = Long.toString(aDiffMinutes)
                        + " minute(s) and " + Long.toString(aDiffSeconds)
                        + " second(s).";
            } else if (aDiffDays == 0) {
                aFinalElapsedTime = Long.toString(aDiffHours)
                        + " hour(s), " + Long.toString(aDiffMinutes)
                        + " minute(s), and " + Long.toString(aDiffSeconds)
                        + " second(s).";
            } else {
                aFinalElapsedTime = Long.toString(aDiffDays)
                        + " day(s), " + Long.toString(aDiffHours)
                        + " hour(s), " + Long.toString(aDiffMinutes)
                        + " minute(s), and " + Long.toString(aDiffSeconds)
                        + " second(s).";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new StringBuffer("You have been ")
                .append(pActivityObject.getStringActivityType())
                .append(" for ").append(aFinalElapsedTime).toString();
    }

    public void notifyElapsedTime(String pMessage) {
        NotificationManager aNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent aIntent = new Intent(this, MainActivity.class);
        PendingIntent aPendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), aIntent, 0);
        Notification aNotification = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("ActivityRecogniser")
                .setContentText(pMessage)
                .setContentIntent(aPendingIntent).build();
        aNM.notify(0, aNotification);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (mActivityRunning) {
            SharedPreferences aSharedPref = getSharedPreferences("pedometer_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor aEditor = aSharedPref.edit();
            aEditor.putInt("pedometer_count", (int) sensorEvent.values[0]);
            aEditor.apply();
            Log.d(TAG, "PEDOMETER COUNT: " + sensorEvent.values[0]);
            //stillProbabilityTextView.setText(String.valueOf(sensorEvent.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}