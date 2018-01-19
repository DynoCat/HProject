package com.example.x.googleapi_prototype;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by x on 07/01/18.
 */

public class ActivityRecognitionService extends IntentService {

    private static final String TAG = "HARService";

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }

    public ActivityRecognitionService(String pName) {
        super(pName);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent pIntent) {
        if(ActivityRecognitionResult.hasResult(pIntent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(pIntent);
            handleDetectedActivity(result.getProbableActivities());
        }
    }

    private void handleDetectedActivity(List<DetectedActivity> pProbableActivities) {
        for(DetectedActivity aActivity : pProbableActivities) {
            SharedPreferences aSharedPref = getSharedPreferences("activity_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor aEditor = aSharedPref.edit();
            switch(aActivity.getType()) {
                case DetectedActivity.STILL:
                    Log.d(TAG, "handleDetectedActivity: STILL" + aActivity.getConfidence());
                    aEditor.putString("still", Integer.toString(aActivity.getConfidence()));
                    break;
                case DetectedActivity.TILTING:
                    Log.d(TAG, "handleDetectedActivity: TILTING" + aActivity.getConfidence());
                    aEditor.putString("tilting", Integer.toString(aActivity.getConfidence()));
                    break;
                case DetectedActivity.ON_FOOT:
                    Log.d(TAG, "handleDetectedActivity: ON_FOOT" + aActivity.getConfidence());
                    aEditor.putString("on_foot", Integer.toString(aActivity.getConfidence()));
                    break;
                case DetectedActivity.RUNNING:
                    Log.d(TAG, "handleDetectedActivity: RUNNING" + aActivity.getConfidence());
                    aEditor.putString("running", Integer.toString(aActivity.getConfidence()));
                    break;
                case DetectedActivity.WALKING:
                    Log.d(TAG, "handleDetectedActivity: WALKING" + aActivity.getConfidence());
                    aEditor.putString("walking", Integer.toString(aActivity.getConfidence()));
                    break;
                case DetectedActivity.ON_BICYCLE:
                    Log.d(TAG, "handleDetectedActivity: ON_BICYCLE" + aActivity.getConfidence());
                    aEditor.putString("on_bicycle", Integer.toString(aActivity.getConfidence()));
                    break;
                case DetectedActivity.IN_VEHICLE:
                    Log.d(TAG, "handleDetectedActivity: IN_VEHICLE" + aActivity.getConfidence());
                    aEditor.putString("in_vehicle", Integer.toString(aActivity.getConfidence()));
                    break;
                case DetectedActivity.UNKNOWN:
                    Log.d(TAG, "handleDetectedActivity: UNKNOWN" + aActivity.getConfidence());
                    aEditor.putString("unknown", Integer.toString(aActivity.getConfidence()));
                    break;
            }
            aEditor.apply();
        }
    }
}
