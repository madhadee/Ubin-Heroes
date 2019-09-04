package com.mygdx.game.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.adapters.InventoryTabAdapter;
import com.mygdx.game.fragments.InventoryCraftFragment;
import com.mygdx.game.fragments.InventoryItemFragment;
import com.mygdx.game.models.UseableList;
import com.mygdx.game.utils.Constants;

public class InventoryActivity extends BaseActivity implements View.OnClickListener, InventoryItemFragment.OnFragmentInteractionListener, InventoryCraftFragment.OnFragmentInteractionListener{
    private InventoryTabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView mPlasticCount, mMetalCount, mPaperCount;

    //Declare listener variable
    private ValueEventListener mUserListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //Initialize UI
        initUI();
        initTabs();
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
                startActivity(new Intent(InventoryActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnRanking:
                startActivity(new Intent(InventoryActivity.this, RankingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnScan:
                startActivity(new Intent(InventoryActivity.this, ScanOptionsActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnProfile:
                startActivity(new Intent(InventoryActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                break;
        }
    }

    /**
     * Load in user usable list of materials (plastic, metal, paper) from Firebase
     */
    private void loadUserData(){
        //Store listener to be able to remove at onStop
        mUserListener = mRootReference.child(Constants.DB_REF_USEABLE_LIST).child(mUserFirebase.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Instance of UseableList class
                UseableList useableList = new UseableList();

                useableList.setPlastic(dataSnapshot.getValue(UseableList.class).getPlastic());
                useableList.setMetal(dataSnapshot.getValue(UseableList.class).getMetal());
                useableList.setPaper(dataSnapshot.getValue(UseableList.class).getPaper());

                populateMaterialCount(useableList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * @param useableList
     *
     * Populate user info from snapshot to UI
     */
    private void populateMaterialCount(UseableList useableList){
        mPlasticCount.setText(String.valueOf(useableList.getPlastic()));
        mMetalCount.setText(String.valueOf(useableList.getMetal()));
        mPaperCount.setText(String.valueOf(useableList.getPaper()));
    }

    /**
     * Initialise UI elements from XML
     */
    private void initUI(){
        //Button listeners
        findViewById(R.id.btnHome).setOnClickListener(this);
        findViewById(R.id.btnRanking).setOnClickListener(this);
        findViewById(R.id.btnScan).setOnClickListener(this);
        findViewById(R.id.btnProfile).setOnClickListener(this);

        //Material Count
        mPlasticCount = findViewById(R.id.plasticCount);
        mMetalCount = findViewById(R.id.metalCount);
        mPaperCount = findViewById(R.id.paperCount);

        //Tab declarations
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tablayout);
    }

    /**
     * Initialise tabs on tab layout (Items and Craft)
     */
    private void initTabs(){
        tabLayout.addTab(tabLayout.newTab().setText(Constants.ITEMS_TAB));
        tabLayout.addTab(tabLayout.newTab().setText(Constants.CRAFT_TAB));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new InventoryTabAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Remove listener for memory management
     */
    @Override
    protected void onStop() {
        super.onStop();
        mRootReference.removeEventListener(mUserListener);
    }
}
