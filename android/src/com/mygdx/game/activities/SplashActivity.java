package com.mygdx.game.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.mygdx.game.R;
import com.mygdx.game.utils.Constants;

public class SplashActivity extends BaseActivity {
    // Shared prefs
    private SharedPreferences mPreferences;
    boolean mFlagFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mPreferences = getSharedPreferences(Constants.SHARED_PREF_PATH, MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                authenticateCheck();
            }
        }, Constants.SPLASH_TIME_OUT);
    }

    /**
     * Login automatically at splash screen for returning users
     */
    private void authenticateCheck(){
        if (mUserFirebase != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            mFlagFirstTime = mPreferences.getBoolean(Constants.FIRST_LOGIN,true);
            // for first time users
            if (mFlagFirstTime){
                startActivity(new Intent(getApplicationContext(), SlidersActivity.class));
                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putBoolean(Constants.FIRST_LOGIN,false);
                preferencesEditor.apply();
                finish();
            } else {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }
    }
}
