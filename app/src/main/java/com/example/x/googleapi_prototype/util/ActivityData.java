package com.example.x.googleapi_prototype.util;

import java.text.SimpleDateFormat;
import java.io.Serializable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by x on 20/01/18.
 */

public class ActivityData implements Serializable {

    private long mTimeStamp;

    private int mActivityConfidence;

    private int mActivityType;

    public ActivityData(ActivityRecognitionResult pResult, DetectedActivity pActivity) {
        this.mTimeStamp = pResult.getTime();
        this.mActivityType = pActivity.getType();
        this.mActivityConfidence = pActivity.getConfidence();
    }

    public String toString() {
        SimpleDateFormat aDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        String aDateTime = aDateFormat.format(mTimeStamp);
        return new StringBuffer("DateTimeEpoch: ")
                .append(this.mTimeStamp)
                .append(" DateTimeFormatted: ")
                .append(aDateTime)
                .append(" activityID: ")
                .append(this.mActivityType)
                .append(" activityType: ")
                .append(intActivityTypeToString(this.mActivityType))
                .append(" activityConfidence:")
                .append(this.mActivityConfidence).toString();
    }

    private String intActivityTypeToString(int mActivityType) {
        switch(mActivityType) {
            case ActivityConstants.ACTIVITY_IN_VEHICLE:
                return "IN_VEHICLE";
            case ActivityConstants.ACTIVITY_ON_BICYCLE:
                return "ON_BICYCLE";
            case ActivityConstants.ACTIVITY_ON_FOOT:
                return "ON_FOOT";
            case ActivityConstants.ACTIVITY_STILL:
                return "STILL";
            case ActivityConstants.ACTIVITY_UNKNOWN:
                return "UNKNOWN";
            case ActivityConstants.ACTIVITY_TILTING:
                return "TILTING";
            case ActivityConstants.ACTIVITY_WALKING:
                return "WALKING";
            case ActivityConstants.ACTIVITY_RUNNING:
                return "RUNNING";
        }
        return null;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public int getActivityConfidence() {
        return mActivityConfidence;
    }

    public void setActivityConfidence(int mActivityConfidence) {
        this.mActivityConfidence = mActivityConfidence;
    }

    public int getActivityType() {
        return mActivityType;
    }

    public void setActivityType(int mActivityType) {
        this.mActivityType = mActivityType;
    }
}
