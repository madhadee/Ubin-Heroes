package com.mygdx.game.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.models.UserObject;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.viewholder.RankingViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankingActivity extends BaseActivity implements View.OnClickListener {
    //Recylerview variables
    RecyclerView mRankingRecyclerView;
    LinearLayoutManager mLayoutManager;
    public FirebaseRecyclerAdapter<UserObject, RankingViewHolder> mRankingAdapter;

    //UI variables
    private CircleImageView mProfilePic;
    private TextView mUsername;
    private TextView mPrestigeTitle;
    private TextView mPrestigeLevel;

    //Declare listener variable
    private ValueEventListener mUserListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        //Initialize UI
        initUI();

        //Configure RecyclerView to reverse order
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRankingRecyclerView.setLayoutManager(mLayoutManager);

        //Fetching all users sorted by District Rank
        fetch();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Retrieve user's data from snapshot
        loadUserData();
        mRankingAdapter.startListening();
    }

    @Override
    public void onClick (View v){
        int id = v.getId();
        switch (id){
            case R.id.btnHome:
                startActivity(new Intent(RankingActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnScan:
                startActivity(new Intent(RankingActivity.this, ScanOptionsActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnInventory:
                startActivity(new Intent(RankingActivity.this, InventoryActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnProfile:
                startActivity(new Intent(RankingActivity.this, ProfileActivity.class));
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
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
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
        //Recyclerview
        mRankingRecyclerView = findViewById(R.id.rvDistrictUsers);

        //Initialize UI components
        mUsername = findViewById(R.id.tvUsername);
        mProfilePic = findViewById(R.id.civProfilePic);
        mPrestigeTitle = findViewById(R.id.tvPrestigeTitle);
        mPrestigeLevel = findViewById(R.id.tvPrestigeLvl);

        //Button listeners
        findViewById(R.id.btnHome).setOnClickListener(this);
        findViewById(R.id.btnScan).setOnClickListener(this);
        findViewById(R.id.btnInventory).setOnClickListener(this);
        findViewById(R.id.btnProfile).setOnClickListener(this);
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
    }

    /**
     * Fetch all user objects from Firebase and load it into adapter
     */
    private void fetch(){
        Query query = mRootReference.child(Constants.DB_REF_USERS)
                .orderByChild(Constants.DB_REF_PRESTIGE_LVL)
                .limitToLast(10);

        FirebaseRecyclerOptions<UserObject> mOptions = new FirebaseRecyclerOptions.Builder<UserObject>().setQuery(query, UserObject.class).build();

        mRankingAdapter = new FirebaseRecyclerAdapter<UserObject, RankingViewHolder>(mOptions) {
            @Override
            protected void onBindViewHolder(RankingViewHolder holder, int position, UserObject model) {
                holder.setRanking(model,getApplicationContext());
            }

            @NonNull
            @Override
            public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_districtlist, parent, false);
                return new RankingViewHolder(view);
            }
        };
        mRankingRecyclerView.setAdapter(mRankingAdapter);
    }

    /**
     * Destroy listener for battery and memory management
     */
    @Override
    protected void onStop() {
        super.onStop();

        mRootReference.removeEventListener(mUserListener);
        if (mRankingAdapter != null) {
            mRankingAdapter.stopListening();
        }
    }
}
