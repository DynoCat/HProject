package com.example.x.googleapi_prototype.util;

/**
 * Created by x on 16/01/18.
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ActivityResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public ActivityResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver pReceiver) {
        mReceiver = pReceiver;
    }

    @Override
    protected void onReceiveResult(int pResultCode, Bundle pResultData) {
        if(mReceiver != null) {
            mReceiver.onReceiveResult(pResultCode, pResultData);
        }
    }
}
