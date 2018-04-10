package com.example.x.googleapi_prototype;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.x.googleapi_prototype.util.ActivityData;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(pIntent);
        handleDetectedActivity(result.getProbableActivities(), result, pIntent);
    }

    private void handleDetectedActivity(List<DetectedActivity> pProbableActivities, ActivityRecognitionResult pResult, @Nullable Intent pIntent) {
        SharedPreferences aSharedPref = getSharedPreferences("activity_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor aEditor = aSharedPref.edit();

        ActivityData aData;
        ArrayList<ActivityData> mActivityDataList = new ArrayList<>();
        for (DetectedActivity aActivity : pProbableActivities) {
            switch (aActivity.getType()) {
                case DetectedActivity.STILL:
                    Log.d(TAG, "handleDetectedActivity: STILL" + aActivity.getConfidence());
                    aEditor.putString("still", Integer.toString(aActivity.getConfidence()));

                    aData = new ActivityData(pResult, aActivity);
                    mActivityDataList.add(aData);
                    break;
                case DetectedActivity.TILTING:
                    Log.d(TAG, "handleDetectedActivity: TILTING" + aActivity.getConfidence());
                    aEditor.putString("tilting", Integer.toString(aActivity.getConfidence()));

                    aData = new ActivityData(pResult, aActivity);
                    mActivityDataList.add(aData);
                    break;
                case DetectedActivity.ON_FOOT:
                    Log.d(TAG, "handleDetectedActivity: ON_FOOT" + aActivity.getConfidence());
                    aEditor.putString("on_foot", Integer.toString(aActivity.getConfidence()));

                    aData = new ActivityData(pResult, aActivity);
                    mActivityDataList.add(aData);
                    break;
                case DetectedActivity.RUNNING:
                    Log.d(TAG, "handleDetectedActivity: RUNNING" + aActivity.getConfidence());
                    aEditor.putString("running", Integer.toString(aActivity.getConfidence()));

                    aData = new ActivityData(pResult, aActivity);
                    mActivityDataList.add(aData);
                    break;
                case DetectedActivity.WALKING:
                    Log.d(TAG, "handleDetectedActivity: WALKING" + aActivity.getConfidence());
                    aEditor.putString("walking", Integer.toString(aActivity.getConfidence()));

                    aData = new ActivityData(pResult, aActivity);
                    mActivityDataList.add(aData);
                    break;
                case DetectedActivity.ON_BICYCLE:
                    Log.d(TAG, "handleDetectedActivity: ON_BICYCLE" + aActivity.getConfidence());
                    aEditor.putString("on_bicycle", Integer.toString(aActivity.getConfidence()));

                    aData = new ActivityData(pResult, aActivity);
                    mActivityDataList.add(aData);
                    break;
                case DetectedActivity.IN_VEHICLE:
                    Log.d(TAG, "handleDetectedActivity: IN_VEHICLE" + aActivity.getConfidence());
                    aEditor.putString("in_vehicle", Integer.toString(aActivity.getConfidence()));

                    aData = new ActivityData(pResult, aActivity);
                    mActivityDataList.add(aData);
                    break;
                case DetectedActivity.UNKNOWN:
                    Log.d(TAG, "handleDetectedActivity: UNKNOWN" + aActivity.getConfidence());
                    aEditor.putString("unknown", Integer.toString(aActivity.getConfidence()));

                    aData = new ActivityData(pResult, aActivity);
                    mActivityDataList.add(aData);
                    break;
            }
            aEditor.apply();
        }
        Log.d(TAG, "Size: " + mActivityDataList.size());

        saveActivityDataToFile(mActivityDataList);

        Bundle aBundle = pIntent.getExtras();
        if (aBundle != null) {
            Messenger aMessenger = (Messenger) aBundle.get("messenger");
            Message msg = Message.obtain();
            aBundle.putParcelableArrayList("data_array", mActivityDataList);
            msg.setData(aBundle);
            try{
                aMessenger.send(msg);
            } catch (RemoteException e) {
                Log.i("error", "error");
            }
        }
    }

    private void saveActivityDataToFile(ArrayList<ActivityData> pActivityList) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d(TAG, "RETURNING");
            return;
        }
        // Create directory if it does not exist
        File aFileDir = new File(Environment.getExternalStorageDirectory() + "/activity_data/");
        if (!aFileDir.exists()) {
            aFileDir.mkdirs();
        }
        // Append to file
        File aFilePath = new File(aFileDir, "activities.txt");
        FileWriter aFW = null;
        try {
            aFW = new FileWriter(aFilePath, true);
            for (ActivityData aData : pActivityList) {
                aFW.append(aData.toString());
            }
            aFW.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (aFW != null) {
                    aFW.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
