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

import com.example.x.googleapi_prototype.util.ActivityConstants;
import com.example.x.googleapi_prototype.util.ActivityData;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener //, ActivityResultReceiver.Receiver
{

    private static final String TAG = "Main Activity - ";
    public GoogleApiClient mApiClient;
    private Handler mGUIHandler;

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
                ArrayList<ActivityData> aActivityDataList = reply.getParcelableArrayList("data_array");
                Log.d(TAG, "Size: " + aActivityDataList.size());

                ActivityData aMostProbableActivity = maxConfidence(aActivityDataList);

                switch(aMostProbableActivity.getIntActivityType()) {
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
        Log.d(TAG, " - onConnectionSuspended");
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
}
