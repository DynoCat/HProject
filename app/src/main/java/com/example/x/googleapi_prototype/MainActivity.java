package com.example.x.googleapi_prototype;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.x.googleapi_prototype.util.ActivityConstants;
import com.example.x.googleapi_prototype.util.ActivityData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener //, ActivityResultReceiver.Receiver
{

    private static final String TAG = "Main Activity - ";

    public GoogleApiClient mApiClient;
    private Handler mGUIHandler;

    private String mActivityStartTime = null;
    private String mActivityEndTime = null;

    private int mPreviousActivity = -1;
    private ActivityData mPreviousActivityObject;

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

        mApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .build();
        mApiClient.connect();

        mGUIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle reply = msg.getData();
                ArrayList<ActivityData> aActivityDataList = reply.getParcelableArrayList("data_array"); //2
                Log.d(TAG, "Size: " + aActivityDataList.size());

                if(mPreviousActivity == -1) {
                    mPreviousActivityObject = aActivityDataList.get(0); //1
                    mPreviousActivity = aActivityDataList.get(0).getIntActivityType(); //1
                }

                if(mPreviousActivity == aActivityDataList.get(0).getIntActivityType()) {
                    mActivityStartTime = mPreviousActivityObject.getFormattedTimeStamp();
                    mActivityEndTime = aActivityDataList.get(0).getFormattedTimeStamp();
                    String aCurrentElapsedTime = getElapsedActivityTime(mActivityStartTime,
                                                                        mActivityEndTime,
                                                                        mPreviousActivityObject);
                    Toast.makeText(MainActivity.this, aCurrentElapsedTime, Toast.LENGTH_SHORT).show();
                } else {
                    switch(aActivityDataList.get(0).getIntActivityType()) {
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
        for(int aIndex = 1; aIndex < pActivityList.size() - 1; aIndex++) {
            if(pActivityList.get(aIndex).getActivityConfidence() > aHighestConfidence) {
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
    }

    @Override
    protected void onPause() {
        Log.d(TAG, " onPause()");
        super.onPause();
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

            if(aDiffDays == 0 && aDiffHours == 0 && aDiffMinutes == 0) {
                aFinalElapsedTime = Long.toString(aDiffSeconds) + " seconds.";
            }
            else if(aDiffDays == 0 && aDiffHours == 0) {
                aFinalElapsedTime = Long.toString(aDiffMinutes)
                        + " minute(s) and " + Long.toString(aDiffSeconds)
                        + " second(s).";
            }
            else if(aDiffDays == 0) {
                aFinalElapsedTime = Long.toString(aDiffHours)
                        + " hour(s), " + Long.toString(aDiffMinutes)
                        + " minute(s), and " + Long.toString(aDiffSeconds)
                        + " second(s).";
            }
            else {
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
}
