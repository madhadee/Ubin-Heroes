package com.mygdx.game.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.models.UserObject;
import com.mygdx.game.utils.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    //Declare UI variables
    private CircleImageView mProfilePic;
    private TextView mUsername;
    private TextView mPrestigeTitle;
    private TextView mPrestigeLevel;

    private TextView mUserRecycleCount;
    private TextView mUserWinCount;
    private TextView mUserUsername;
    private TextView mGeneralLocation;

    //Declare listener variable
    private ValueEventListener mUserListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initialize UI
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Retrieve user's data from snapshot
        loadUserData();
    }

    @Override
    public void onClick (View v){
        int id = v.getId();
        switch (id){
            case R.id.btnHome:
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnRanking:
                startActivity(new Intent(ProfileActivity.this, RankingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnScan:
                startActivity(new Intent(ProfileActivity.this, ScanOptionsActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnInventory:
                startActivity(new Intent(ProfileActivity.this, InventoryActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnLogout:
                logoutDialog();
                overridePendingTransition(0, 0);
                break;
        }
    }

    /**
     * Load in user profile data from Firebase
     */
    private void loadUserData(){
        //Store listener to be able to remove at onStop
        mUserListener = mRootReference.child(Constants.DB_REF_USERS).child(mUserFirebase.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Instance of User class
                UserObject userObject = new UserObject();
                //Iterate through snapshot for each children's value
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    Log.d(Constants.TAG_PROFILE_ACTIVITY, String.valueOf(userSnapshot));

                    switch (userSnapshot.getKey()){
                        case Constants.DB_REF_USERNAME:
                            userObject.setUsername(String.valueOf(userSnapshot.getValue()));
                            break;
                        case Constants.DB_REF_DISTRICT:
                            userObject.setDistrict(String.valueOf(userSnapshot.getValue()));
                            break;
                        case Constants.DB_REF_PRESTIGE_LVL:
                            userObject.setPrestigeLvl(Integer.valueOf(userSnapshot.getValue().toString()));
                            break;
                        case Constants.DB_REF_PRESTIGE_TITLE:
                            userObject.setPrestigeTitle(String.valueOf(userSnapshot.getValue()));
                            break;
                        case Constants.DB_REF_PROFILE_PIC:
                            userObject.setProfilePicUrl(String.valueOf(mUserFirebase.getPhotoUrl()));
                            break;
                        case Constants.DB_REF_RECYCLE_COUNT:
                            userObject.setRecyclingCount(Integer.valueOf(userSnapshot.getValue().toString()));
                            break;
                        case Constants.DB_REF_WIN_COUNT:
                            userObject.setWinCount(Integer.valueOf(userSnapshot.getValue().toString()));
                            break;
                    }
                }
                //Function to load UI from database
                populateUserInfo(userObject);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Initialise UI elements from XML
     */
    private void initUI(){
        //Initialize UI components
        mUsername = findViewById(R.id.tvUsername);
        mProfilePic = findViewById(R.id.civProfilePic);
        mPrestigeTitle = findViewById(R.id.tvPrestigeTitle);
        mPrestigeLevel = findViewById(R.id.tvPrestigeLvl);

        mUserRecycleCount = findViewById(R.id.tvProfileRecyclingCount);
        mUserWinCount = findViewById(R.id.tvProfileWinsCount);
        mUserUsername = findViewById(R.id.tvEditUsername);
        mGeneralLocation = findViewById(R.id.tvPostalLocation);

        //Button listeners
        findViewById(R.id.btnHome).setOnClickListener(this);
        findViewById(R.id.btnRanking).setOnClickListener(this);
        findViewById(R.id.btnScan).setOnClickListener(this);
        findViewById(R.id.btnInventory).setOnClickListener(this);
        findViewById(R.id.btnLogout).setOnClickListener(this);
    }

    /**
     * Populate user info from snapshot to UI
     * @param userObject Current user object
     */
    private void populateUserInfo(UserObject userObject){
        //Set profile pic from Google URL
        Glide.with(getApplicationContext()).load(userObject.getProfilePicUrl())
                .apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).into(mProfilePic);
        mUsername.setText(userObject.getUsername());
        mPrestigeTitle.setText(userObject.getPrestigeTitle());
        String prestigeLvl = getString(R.string.title_profile_prestige, userObject.getPrestigeLvl());
        mPrestigeLevel.setText(prestigeLvl);

        mUserRecycleCount.setText(String.valueOf(userObject.getRecyclingCount()));
        mUserWinCount.setText(String.valueOf(userObject.getWinCount()));
        mUserUsername.setText(userObject.getUsername());
        mGeneralLocation.setText(userObject.getDistrict());
    }

    /**
     * Sign out from Google account session
     */
    private void signOut(){
        //Fire base sign out
        mAuth.signOut();
        //Google sign out
        AuthUI.getInstance()
                .signOut(ProfileActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ProfileActivity.this, R.string.msg_logout_success, Toast.LENGTH_LONG).show();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });
    }

    /**
     * Show logout prompt for user to choose to confirm logout
     */
    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Constants.LOGOUT_TITLE)
                .setMessage(Constants.LOGOUT_MESSAGE)
                .setPositiveButton(Constants.LOGOUT_YES,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        signOut();
                    }
                })
                .setNegativeButton(Constants.LOGOUT_NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /**
     * Destroy listener for battery and memory management when activity ends
     */
    @Override
    protected void onStop() {
        super.onStop();
        mRootReference.removeEventListener(mUserListener);
    }
}