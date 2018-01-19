package com.example.x.googleapi_prototype;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.x.googleapi_prototype.util.ActivityResultReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.vision.text.Text;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityResultReceiver.Receiver {

    public GoogleApiClient mApiClient;
    public ActivityResultReceiver mReceiver;

    /*
        WRITE ABOUT BROADCAST RECEIVER VS RESULT REC. IN FAVOUR OF RESULT REC - Result rec. is used when only our own (1) application needs the data
        whereas broadcast receiver can pass to multiple apps.
     */

    TextView stillProbabilityTextView;
    TextView tiltingProbabilityTextView;
    TextView onFootProbabilityTextView;
    TextView runningProbabilityTextView;
    TextView walkingProbabilityTextView;
    TextView onBicycleProbabilityTextView;
    TextView inVehicleProbabilityTextView;
    TextView unknownActivityProbabilityTextView;

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

//        mReceiver = new ActivityResultReceiver(new Handler());
//        mReceiver.setReceiver(this);
        mApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .build();
        mApiClient.connect();


        //mReceiver.setReceiver(this);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Intent intent = new Intent(MainActivity.this, ActivityRecognitionService.class);
//        intent.putExtra("receiverTag", mReceiver);
//        intent.putExtra("nameTag", "MainActivity");
        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);
        //Intent aIntent = getIntent();
//        String still = aIntent.getStringExtra("STILL");
//        String tilting = aIntent.getStringExtra("TILTING");
//        String onFoot = aIntent.getStringExtra("ON_FOOT");
//        String running = aIntent.getStringExtra("RUNNING");
//        String walking = aIntent.getStringExtra("WALKING");
//        String onBicycle = aIntent.getStringExtra("ON_BICYCLE");
//        String inVehicle = aIntent.getStringExtra("IN_VEHICLE");
//        String unknown = aIntent.getStringExtra("UNKNOWN");

        SharedPreferences aSharedPreference = getSharedPreferences("activity_data", Context.MODE_PRIVATE);
        stillProbabilityTextView.setText(aSharedPreference.getString("still", ""));
        tiltingProbabilityTextView.setText(aSharedPreference.getString("tilting", ""));
        onFootProbabilityTextView.setText(aSharedPreference.getString("on_foot", ""));
        runningProbabilityTextView.setText(aSharedPreference.getString("running", ""));
        walkingProbabilityTextView.setText(aSharedPreference.getString("walking", ""));
        onBicycleProbabilityTextView.setText(aSharedPreference.getString("on_bicycle", ""));
        inVehicleProbabilityTextView.setText(aSharedPreference.getString("in_vehicle", ""));
        unknownActivityProbabilityTextView.setText(aSharedPreference.getString("unknown", ""));

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d("googleapi_prototype", " received result from ActivityRecognitionService= " + resultData.getString("ServiceTag"));
    }
}
