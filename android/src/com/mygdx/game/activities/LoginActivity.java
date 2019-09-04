package com.mygdx.game.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mygdx.game.R;

import com.mygdx.game.utils.Constants;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    //Declare Google client
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize UI
        initUI();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.googleLogin:
                signIn();
                break;
        }
    }

    /**
     * Initialise UI elements from XML
     */
    private void initUI(){
        findViewById(R.id.googleLogin).setOnClickListener(this);
    }

    /**
     * Execute sign in function with default web client ID
     */
    private void signIn() {
        //Configure sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }
}