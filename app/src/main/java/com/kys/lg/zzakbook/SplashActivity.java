package com.kys.lg.zzakbook;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


            handler.sendEmptyMessageDelayed(0,3000);

    }

    Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent i= new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
            finish();
        }
    };





}
