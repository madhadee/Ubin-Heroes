package com.mygdx.game.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.models.CardObject;
import com.mygdx.game.models.UserObject;
import com.mygdx.game.utils.CSVReader;
import com.mygdx.game.utils.Constants;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class VictoryActivity extends BaseActivity implements View.OnClickListener{
    //UI Variables
    private CircleImageView mProfilePic;
    private TextView mPrestigeGained;
    private TextView mPrestigePoints;

    private TextView mUserWinCount;

    //Declare listener variable
    private ValueEventListener mUserListener;

    //Experience Map
    HashMap<Integer,Integer> experience = new HashMap<>();

    //Declare PrestigePoints
    private double prestigePoint = 0;
    private ArrayList<Integer> deck = new ArrayList<>();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory);

        //Initialize UI
        initUI();

        //retrieve prestige point gained
        prestigePoint = getIntent().getIntExtra("prestigeGained",0);
        deck = getIntent().getIntegerArrayListExtra("Deck");
        mPrestigeGained.setText(String.valueOf(prestigePoint));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Retrieve experience table
        retrieveExperienceTable();

        //Update prestige points
        updatePrestige();

        //Update card objects
        updateCardObject();

        //Retrieve user's data from snapshot
        loadUserData();
    }

    @Override
    public void onClick (View v){
        int id = v.getId();
        switch (id){
            case R.id.btnHome:
                startActivity(new Intent(VictoryActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                break;
        }
    }

    /**
     * Update and add prestige points to user Firebase account
     */
    private void updatePrestige(){
        mRootReference.child(Constants.DB_REF_USERS).child(mUserFirebase.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int currentPoint = Integer.parseInt(dataSnapshot.child(Constants.DB_REF_PRESTIGE_POINTS).getValue().toString());
                int lvl = Integer.parseInt(dataSnapshot.child(Constants.DB_REF_PRESTIGE_LVL).getValue().toString());
                lvl = experienceCalculator(currentPoint, (int) prestigePoint, lvl);

                //check level to determine
                String prestigeTitle = calculateTitle(lvl);

                currentPoint += prestigePoint;
                dataSnapshot.child(Constants.DB_REF_PRESTIGE_POINTS).getRef().setValue(currentPoint);
                dataSnapshot.child(Constants.DB_REF_PRESTIGE_LVL).getRef().setValue(lvl);
                dataSnapshot.child(Constants.DB_REF_PRESTIGE_TITLE).getRef().setValue(prestigeTitle);
                int winCount = Integer.parseInt(dataSnapshot.child(Constants.DB_REF_WIN_COUNT).getValue().toString());
                dataSnapshot.child(Constants.DB_REF_WIN_COUNT).getRef().setValue(winCount + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get data from exp table and rank
     */
    private void retrieveExperienceTable(){
        InputStream inputStream = getResources().openRawResource(R.raw.experience_table);
        CSVReader csvFile = new CSVReader(inputStream);
        experience = csvFile.read();
    }

    /**
     * Decrement and delete card object used in the previous game session from Firebase
     */
    private void updateCardObject(){
        mRootReference.child(Constants.DB_REF_CARD_OBJECT).child(mUserFirebase.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    int cardID = userSnapshot.getValue(CardObject.class).getCardID();
                    int qty = userSnapshot.getValue(CardObject.class).getQuantity() - 1;
                    for (int i =0; i< deck.size(); i++){
                        if (deck.get(i) == cardID){
                            if (qty < 0) {
                                userSnapshot.getRef().child("quantity").setValue(0);
                            } else {
                                userSnapshot.getRef().child("quantity").setValue(qty);
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                        case Constants.DB_REF_DISTRICT_RANK:
                            userObject.setDistrictRank(Integer.valueOf(userSnapshot.getValue().toString()));
                            break;
                        case Constants.DB_REF_PRESTIGE_POINTS:
                            userObject.setPrestigePoints(Integer.valueOf(userSnapshot.getValue().toString()));
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
     * Algorithm for a user increase prestige rank
     * @param currentPrestige of user
     * @param prestigeGained prestige points
     * @param currentlvl of user
     * @return
     */
    private int experienceCalculator(int currentPrestige, int prestigeGained, int currentlvl){
        //experience
        int getExpLimit = experience.get(currentlvl);
        int totalprestige = currentPrestige + prestigeGained;
        if (totalprestige >= getExpLimit) {
            currentlvl += 1;
        }
        return currentlvl;
    }

    /**
     * To calculate and return user title based on level
     * @param lvl of user
     * @return user title
     */
    private String calculateTitle(int lvl) {
        if (lvl < 20) {
            return "Bronze";
        } else if (lvl < 40) {
            return "Silver";
        } else if (lvl < 60) {
            return "Gold";
        } else if (lvl < 80) {
            return "Platinum";
        } else {
            return "Master";
        }
    }

    /**
     * Initialise UI elements from XML
     */
    private void initUI(){
        //Initialize UI components
        mProfilePic = findViewById(R.id.civProfilePic);
        mPrestigeGained = findViewById(R.id.tvPrestigeGained);
        mPrestigePoints = findViewById(R.id.tvTotalPrestige);

        mUserWinCount = findViewById(R.id.tvTotalWins);

        //Button listeners
        findViewById(R.id.btnHome).setOnClickListener(this);
    }

    /**
     * Populate profile picture and user information into UI elements
     * @param userObject from Firebase
     */
    private void populateUserInfo(UserObject userObject){
        //Set profile pic from Google URL
        Glide.with(getApplicationContext()).load(userObject.getProfilePicUrl())
                .apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).into(mProfilePic);
        mUserWinCount.setText(String.valueOf(userObject.getWinCount()));
        mPrestigePoints.setText(String.valueOf(userObject.getPrestigePoints()));
    }

    /**
     * Destroy listener for battery and memory management
     */
    @Override
    protected void onStop() {
        super.onStop();
        mRootReference.removeEventListener(mUserListener);
    }
}