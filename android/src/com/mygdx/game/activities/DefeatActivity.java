package com.mygdx.game.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.models.CardObject;
import com.mygdx.game.models.UserObject;
import com.mygdx.game.utils.Constants;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DefeatActivity extends BaseActivity implements View.OnClickListener{
    //UI Variables
    private CircleImageView mProfilePic;

    //Declare listener variable
    private ValueEventListener mUserListener;

    private ArrayList<Integer> deck = new ArrayList<>();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defeat);

        //Initialize UI
        initUI();

        //retrieve prestige point gained
        deck = getIntent().getIntegerArrayListExtra("Deck");
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                startActivity(new Intent(DefeatActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                break;
        }
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
                    for(int i =0; i< deck.size(); i++){
                        if(deck.get(i) == cardID){
                            if(qty < 0) {
                                userSnapshot.getRef().child("quantity").setValue(0);
                            }
                            else{
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
     * Initialise UI elements from XML
     */
    private void initUI(){
        //Initialize UI components
        mProfilePic = findViewById(R.id.civProfilePic);

        //Button listeners
        findViewById(R.id.btnHome).setOnClickListener(this);
    }

    /**
     * Populate user info from snapshot to UI
     */
    private void populateUserInfo(UserObject userObject){
        //Set profile pic from Google URL
        Glide.with(getApplicationContext()).load(userObject.getProfilePicUrl())
                .apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).into(mProfilePic);
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