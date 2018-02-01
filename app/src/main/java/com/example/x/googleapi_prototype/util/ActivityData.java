package com.example.x.googleapi_prototype.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.io.Serializable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by x on 20/01/18.
 */

public class ActivityData implements Parcelable {

    private long mTimeStamp;

    private int mActivityConfidence;

    private int mIntActivityType;

    private String mStringActivityType;

    private String mFormattedTimeStamp;

    public ActivityData() {}

    public ActivityData(int pActivityConfidence, int pActivityType, String pStringType) {
        this.mIntActivityType = pActivityType;
        this.mActivityConfidence = pActivityConfidence;
        this.mStringActivityType = pStringType;
    }

    public ActivityData(ActivityRecognitionResult pResult, DetectedActivity pActivity) {
        this.mTimeStamp = pResult.getTime();
        this.mIntActivityType = pActivity.getType();
        this.mActivityConfidence = pActivity.getConfidence();
        this.mStringActivityType = intActivityTypeToString(pActivity.getType());
        this.mFormattedTimeStamp = epochToFormattedTimestamp(this.mTimeStamp);
    }

    public ActivityData(Parcel pParcel) {
        this.mTimeStamp = pParcel.readLong();
        this.mIntActivityType = pParcel.readInt();
        this.mActivityConfidence = pParcel.readInt();
        this.mStringActivityType = pParcel.readString();
        this.mFormattedTimeStamp = pParcel.readString();

    }

    public static final Creator<ActivityData> CREATOR = new Creator<ActivityData>() {
        @Override
        public ActivityData createFromParcel(Parcel in) {
            return new ActivityData(in);
        }

        @Override
        public ActivityData[] newArray(int size) {
            return new ActivityData[size];
        }
    };

    public String toString() {
        return new StringBuffer("DateTimeEpoch: ")
                .append(this.mTimeStamp)
                .append(" DateTimeFormatted: ")
                .append(epochToFormattedTimestamp(this.mTimeStamp))
                .append(" activityID: ")
                .append(this.mIntActivityType)
                .append(" activityType: ")
                .append(this.mStringActivityType)
                .append(" activityConfidence:")
                .append(this.mActivityConfidence).toString();
    }

    private String epochToFormattedTimestamp(long pEpoch){
        SimpleDateFormat aDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        String aDateTime = aDateFormat.format(pEpoch);

        return aDateTime;
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

    public int getIntActivityType() {
        return mIntActivityType;
    }

    public void setIntActivityType(int mActivityType) {
        this.mIntActivityType = mActivityType;
    }

    public String getStringActivityType() {
        return mStringActivityType;
    }

    public void setStringActivityType(String mStringActivityType) {
        this.mStringActivityType = mStringActivityType;
    }

    public String getFormattedTimeStamp() {
        return mFormattedTimeStamp;
    }

    public void setFormattedTimeStamp(String mFormattedTimeStamp) {
        this.mFormattedTimeStamp = mFormattedTimeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pDest, int pFlags) {
        pDest.writeLong(mTimeStamp);
        pDest.writeInt(mIntActivityType);
        pDest.writeInt(mActivityConfidence);
        pDest.writeString(mStringActivityType);
        pDest.writeString(mFormattedTimeStamp);
    }
}
