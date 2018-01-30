package com.example.x.googleapi_prototype;

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
import android.widget.TextView;

import com.example.x.googleapi_prototype.util.ActivityConstants;
import com.example.x.googleapi_prototype.util.ActivityData;
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
                ArrayList<ActivityData> arr = reply.getParcelableArrayList("data_array");
                Log.d(TAG, "Size: " + arr.size());

                for(ActivityData aData : arr) {
                    switch(aData.getIntActivityType()) {
                        case ActivityConstants.ACTIVITY_IN_VEHICLE:
                            inVehicleProbabilityTextView.setText("In vehicle: " + aData.getActivityConfidence());
                            break;
                        case ActivityConstants.ACTIVITY_ON_BICYCLE:
                            onBicycleProbabilityTextView.setText("On bicycle: " + aData.getActivityConfidence());
                            break;
                        case ActivityConstants.ACTIVITY_ON_FOOT:
                            onFootProbabilityTextView.setText("On foot: " + aData.getActivityConfidence());
                            break;
                        case ActivityConstants.ACTIVITY_STILL:
                            stillProbabilityTextView.setText("Still: " + aData.getActivityConfidence());
                            break;
                        case ActivityConstants.ACTIVITY_UNKNOWN:
                            unknownActivityProbabilityTextView.setText("Unknown: " + aData.getActivityConfidence());
                            break;
                        case ActivityConstants.ACTIVITY_TILTING:
                            tiltingProbabilityTextView.setText("Tilting: " + aData.getActivityConfidence());
                            break;
                        case ActivityConstants.ACTIVITY_WALKING:
                            walkingProbabilityTextView.setText("Walking: " + aData.getActivityConfidence());
                            break;
                        case ActivityConstants.ACTIVITY_RUNNING:
                            runningProbabilityTextView.setText("Running: " + aData.getActivityConfidence());
                            break;
                        default:
                            runningProbabilityTextView.setText("");
                            walkingProbabilityTextView.setText("");
                            tiltingProbabilityTextView.setText("");
                            unknownActivityProbabilityTextView.setText("");
                            stillProbabilityTextView.setText("");
                            onFootProbabilityTextView.setText("");
                            onBicycleProbabilityTextView.setText("");
                            inVehicleProbabilityTextView.setText("");
                    }
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
}
