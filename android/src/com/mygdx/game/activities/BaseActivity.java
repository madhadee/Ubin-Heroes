package com.mygdx.game.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.utils.Constants;

/**
 * BaseActivity will handle authentication check to prevent multiple checks by child Activities.
 * The moment the user's account has been deleted from Firebase, user will automatically be directed to LoginActivity
 * Initializing of Firebase instance will also be done here to prevent repeated codes in child Activities
 */

public class BaseActivity extends AppCompatActivity{
    //Reference pointing to https://ubin-heroes.firebaseio.com/
    protected DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUsernameRef;

    //Declare authenticator to be used by child Activities
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUserFirebase;

    //Progress animation
    protected ProgressDialog mProgressDialog;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState()==NetworkInfo.State.CONNECTED) {
                    // we're connected
                } else {
                    try {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Info");
                        alertDialog.setMessage("Internet not available");
                        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        alertDialog.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        });
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    } catch (Exception e) {
                        Log.d(Constants.TAG_BASE_ACTIVITY, "Show Dialog: " + e.getMessage());
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize authenticator
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
        //Get instance of current user logged in
        mUserFirebase = mAuth.getCurrentUser();
        if (mUserFirebase != null){
            updateUI(mUserFirebase);
        } else {
            updateUI(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                // Successfully signed in
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult();
                firebaseAuthWithGoogle(account);
            } else {
                // Sign in failed
                if (response == null){
                    // User pressed back button
                    Log.d(Constants.TAG_BASE_ACTIVITY, "Sign in cancelled");
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //No internet connection
                    Log.d(Constants.TAG_BASE_ACTIVITY, "Check your internet connection");
                    return;
                }
                Log.d(Constants.TAG_BASE_ACTIVITY, "Sign in error: ", response.getError());
            }
        }
    }

    /**
     * @param acct
     * Authenticate with Google
     */
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(Constants.TAG_BASE_ACTIVITY, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.w(Constants.TAG_BASE_ACTIVITY, "signInWithCredential:success", task.getException());
                            FirebaseUser mUserFirebase = mAuth.getCurrentUser();
                            updateUI(mUserFirebase);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Constants.TAG_BASE_ACTIVITY, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }

    protected void updateUI(FirebaseUser mUserFirebase){
        hideProgressDialog();
        if (mUserFirebase != null){
            //User is signed in
            Log.d(Constants.TAG_BASE_ACTIVITY, "onAuthStateChanged: signed_in: " + mUserFirebase.getUid());
            mUsernameRef = mRootReference.child(Constants.DB_REF_USERS).child(mUserFirebase.getUid()).child(Constants.DB_REF_USERNAME);
            //Check if user already has a username to prevent multiple username creations
            mUsernameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null){
                        startActivity(new Intent(getBaseContext(), UsernameActivity.class));
                        finish();
                    } else {
                        toMain();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            //User is signed out
            Log.d(Constants.TAG_BASE_ACTIVITY, "onAuthStateChanged: signed_out");
        }
    }

    /**
     * Redirect to MainActivity
     */
    private void toMain(){
        if (this instanceof LoginActivity){
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            finish();
        }
    }

    /**
     * Functions for loading icon while verifying login
     */
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage(getString(R.string.msg_loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    /**
     * Unregister receiver when not needed for memory management
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}