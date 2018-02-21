package com.example.x.googleapi_prototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by x on 21/02/18.
 */

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent aIntent = new Intent(this, MainActivity.class);

        startActivity(aIntent);
        finish();
//        try{
//            Thread.sleep(1500);
//            startActivity(aIntent);
//        } catch(InterruptedException e) {
//            e.printStackTrace();
//        }
//        finish();
    }
}
