package com.mygdx.game.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.AndroidLauncher;
import com.mygdx.game.R;
import com.mygdx.game.adapters.BattleListAdapter;
import com.mygdx.game.models.CardObject;
import com.mygdx.game.utils.Constants;

import java.util.ArrayList;

public class BattlePreparationActivity extends BaseActivity implements View.OnClickListener{
    //Declare listener variable
    private ValueEventListener mUserListener;

    private BattleListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<CardObject> mRecyclableList = new ArrayList<>();

    private int prestigeLvl;
    private String musername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_preparation);

        prestigeLvl = getIntent().getIntExtra("prestigeLvl",1);
        musername = getIntent().getStringExtra("player");

        //Initialize UI
        initUI();

        //recycler Adapter
        mAdapter = new BattleListAdapter(BattlePreparationActivity.this, mRecyclableList);
        mRecyclerView = findViewById(R.id.rvRecyclableItems);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Retrieve user's data from snapshot
        populateCards();
    }

    @Override
    public void onClick (View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnHome:
                startActivity(new Intent(BattlePreparationActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnRanking:
                startActivity(new Intent(BattlePreparationActivity.this, RankingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnScan:
                startActivity(new Intent(BattlePreparationActivity.this, ScanOptionsActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnInventory:
                startActivity(new Intent(BattlePreparationActivity.this, InventoryActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnProfile:
                startActivity(new Intent(BattlePreparationActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnFight:
                passPlayerInfo();
        }
    }

    /**
     * Pass cards selected, player level and player name to android launcher
     */
    private void passPlayerInfo(){
        if (mAdapter.selectionList.size() >= 3 && mAdapter.selectionList.size() <= 5) {
            Intent intent = new Intent(BattlePreparationActivity.this, AndroidLauncher.class);
            intent.putExtra("prestigeLvl", prestigeLvl);
            intent.putExtra("Deck", mAdapter.selectionList);
            intent.putExtra("player", musername);
            this.startActivity(intent);
        } else if (mAdapter.getItemCount() < 3) {
            //pop up Dialog
            Dialog dialogMessage = new Dialog(this);
            dialogMessage.setContentView(R.layout.dialog_craft_error);
            TextView tvTitle = dialogMessage.findViewById(R.id.dialogTitle);
            TextView tvDescription = dialogMessage.findViewById(R.id.dialogError);
            Button btnStart = dialogMessage.findViewById(R.id.btnOkay);

            tvTitle.setText(R.string.title_warning);
            tvDescription.setText(R.string.msg_warning);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //generate basic card
                    mAdapter.selectionList = new ArrayList<>();

                    mAdapter.selectionList.add(10);
                    mAdapter.selectionList.add(11);
                    mAdapter.selectionList.add(12);

                    dialogMessage.dismiss();

                    Intent intent = new Intent(BattlePreparationActivity.this, AndroidLauncher.class);
                    intent.putExtra("prestigeLvl", prestigeLvl);
                    intent.putExtra("Deck", mAdapter.selectionList);
                    intent.putExtra("player",musername);
                    v.getContext().startActivity(intent);
                }
            });
            dialogMessage.show();

        } else {
            Toast.makeText(this, R.string.msg_not_enough_cards, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     *Populating of recyclerview with cards retreived from Firebase
     */
    private void populateCards(){
        Log.d(Constants.TAG_PREP_ACTIVITY, "Retrieving cards");
        mUserListener = mRootReference.child(Constants.DB_REF_CARD_OBJECT).child(mUserFirebase.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Clear the RecyclerView render to start fresh
                mRecyclableList.clear();
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    //check if its the 3 basic card id : 1, 4, 7
                    if (userSnapshot.getValue(CardObject.class).getQuantity() != 0) {
                        //Set values into each card
                        CardObject cardObject = new CardObject();
                        cardObject.setCardID(userSnapshot.getValue(CardObject.class).getCardID());
                        cardObject.setQuantity(userSnapshot.getValue(CardObject.class).getQuantity());
                        cardObject.setSelected(false);

                        //Add to recycler list
                        mRecyclableList.add(cardObject);
                        Log.d(Constants.TAG_PREP_ACTIVITY, "Adding cards");
                    }
                }
                //Tell adapter change has happened
                mAdapter.notifyDataSetChanged();
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
        //Button listeners
        findViewById(R.id.btnHome).setOnClickListener(this);
        findViewById(R.id.btnRanking).setOnClickListener(this);
        findViewById(R.id.btnScan).setOnClickListener(this);
        findViewById(R.id.btnInventory).setOnClickListener(this);
        findViewById(R.id.btnProfile).setOnClickListener(this);
        findViewById(R.id.btnFight).setOnClickListener(this);
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
