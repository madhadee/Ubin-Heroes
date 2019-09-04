package com.mygdx.game.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.models.UserObject;
import com.mygdx.game.utils.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private DatabaseReference mUserRef;

    //UI Variables
    private CircleImageView mProfilePic;
    private TextView mUsername;
    private TextView mPrestigeTitle;
    private TextView mPrestigeLevel;

    //Declare listener variable
    private ValueEventListener mUserListener;

    //Declare Prestige Points
    private int prestigeLvl;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize UI
        initUI();
        idleMonster();
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
            case R.id.btnBattle:
                //player level as prestigeLvl
                Intent intent = new Intent(MainActivity.this, BattlePreparationActivity.class);
                intent.putExtra("prestigeLvl", prestigeLvl);
                intent.putExtra("player", mUsername.getText().toString());
                this.startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.btnRanking:
                startActivity(new Intent(MainActivity.this, RankingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnScan:
                startActivity(new Intent(MainActivity.this, ScanOptionsActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnInventory:
                startActivity(new Intent(MainActivity.this, InventoryActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnProfile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnHelp:
                startActivity(new Intent(MainActivity.this, HelpOptionsActivity.class));
                overridePendingTransition(0, 0);
                break;
        }
    }

    /**
     * Load in user profile data from Firebase
     */
    private void loadUserData(){
        mUserRef = mRootReference.child(Constants.DB_REF_USERS).child(mUserFirebase.getUid());
        //Store listener to be able to remove at onStop
        mUserListener = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Instance of User class
                UserObject userObject = new UserObject();
                //Iterate through all children within the snapshot and assign values accordingly
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    if (userSnapshot.getKey().contains(Constants.DB_REF_USERNAME)){
                        userObject.setUsername(String.valueOf(userSnapshot.getValue()));
                    }
                    else if (userSnapshot.getKey().equals(Constants.DB_REF_PROFILE_PIC)){
                        userObject.setProfilePicUrl(String.valueOf(mUserFirebase.getPhotoUrl()));
                    }

                    switch (userSnapshot.getKey()) {
                        case Constants.DB_REF_USERNAME:
                            userObject.setUsername(String.valueOf(userSnapshot.getValue()));
                            break;
                        case Constants.DB_REF_PRESTIGE_LVL:
                            userObject.setPrestigeLvl(Integer.valueOf(userSnapshot.getValue().toString()));
                            prestigeLvl = userObject.getPrestigeLvl();
                            break;
                        case Constants.DB_REF_PRESTIGE_TITLE:
                            userObject.setPrestigeTitle(String.valueOf(userSnapshot.getValue()));
                            break;
                        case Constants.DB_REF_PROFILE_PIC:
                            userObject.setProfilePicUrl(String.valueOf(mUserFirebase.getPhotoUrl()));
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
        mProfilePic = findViewById(R.id.civProfilePic);
        mUsername = findViewById(R.id.tvUsername);
        mPrestigeTitle = findViewById(R.id.tvPrestigeTitle);
        mPrestigeLevel = findViewById(R.id.tvPrestigeLvl);

        //Assign buttons
        findViewById(R.id.btnBattle).setOnClickListener(this);
        findViewById(R.id.btnRanking).setOnClickListener(this);
        findViewById(R.id.btnScan).setOnClickListener(this);
        findViewById(R.id.btnInventory).setOnClickListener(this);
        findViewById(R.id.btnProfile).setOnClickListener(this);
        findViewById(R.id.btnHelp).setOnClickListener(this);
    }

    /**
     * Load in ice and rock golem and play animation in loop on screen
     */
    private void idleMonster(){
        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        ImageView img1 = findViewById(R.id.vIdleMonster1);
        ImageView img2 = findViewById(R.id.vIdleMonster2);
        img1.setBackgroundResource(R.drawable.ic_menu_monster1);
        img2.setBackgroundResource(R.drawable.ic_menu_monster2);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation1 = (AnimationDrawable) img1.getBackground();
        AnimationDrawable frameAnimation2 = (AnimationDrawable) img2.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation1.start();
        frameAnimation2.start();
    }

    /**
     * Populate profile picture and user information into UI elements
     * @param userObject from Firebase
     */
    public void populateUserInfo(UserObject userObject){
        mUsername.setText(userObject.getUsername());
        //Set profile pic from Google URL
        Glide.with(getApplicationContext()).load(userObject.getProfilePicUrl())
                .apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).into(mProfilePic);
        mPrestigeTitle.setText(userObject.getPrestigeTitle());
        String prestigeLvl = getString(R.string.title_profile_prestige, userObject.getPrestigeLvl());
        mPrestigeLevel.setText(prestigeLvl);
    }

    /**
     * Destroy listener for battery and memory management
     */
    @Override
    protected void onStop() {
        super.onStop();
        mUserRef.removeEventListener(mUserListener);
    }
}