package com.example.dutchpengdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DutchpengSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 1500);
    }

    class SplashHandler implements Runnable {

        @Override
        public void run() {

            if(FirebaseAuth.getInstance().getCurrentUser() != null){

                startActivity(new Intent(DutchpengSplashActivity.this, MainActivity.class));

            }else{

                startActivity(new Intent(DutchpengSplashActivity.this, LoginActivity.class));

            }

            finish();

        }
    }

}