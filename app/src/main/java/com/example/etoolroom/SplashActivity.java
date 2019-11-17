package com.example.etoolroom;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //initialisation
        mTextView =findViewById(R.id.textView);

        //Splash anim
        Animation fade = AnimationUtils.loadAnimation(this,R.anim.faded);
        mTextView.startAnimation(fade);

        //move to new Activity
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        },4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
