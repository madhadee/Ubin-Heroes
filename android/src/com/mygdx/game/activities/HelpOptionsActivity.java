package com.mygdx.game.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mygdx.game.R;

public class HelpOptionsActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_options);

        //Initialize UI
        initUI();
    }

    @Override
    public void onClick (View v){
        int id = v.getId();
        switch (id){
            case R.id.btnHome:
                startActivity(new Intent(HelpOptionsActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnRanking:
                startActivity(new Intent(HelpOptionsActivity.this, RankingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnGameTutorial:
                startActivity(new Intent(HelpOptionsActivity.this, GameHelpActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnRecyclingFacts:
                startActivity(new Intent(HelpOptionsActivity.this, RecyclingFactsActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnScan:
                startActivity(new Intent(HelpOptionsActivity.this, ScanOptionsActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnInventory:
                startActivity(new Intent(HelpOptionsActivity.this, InventoryActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnProfile:
                startActivity(new Intent(HelpOptionsActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                break;
        }
    }

    /**
     * Initialise UI elements from XML
     */
    public void initUI(){
        //Button listeners
        findViewById(R.id.btnHome).setOnClickListener(this);
        findViewById(R.id.btnRanking).setOnClickListener(this);
        findViewById(R.id.btnScan).setOnClickListener(this);
        findViewById(R.id.btnInventory).setOnClickListener(this);
        findViewById(R.id.btnProfile).setOnClickListener(this);
        findViewById(R.id.btnGameTutorial).setOnClickListener(this);
        findViewById(R.id.btnRecyclingFacts).setOnClickListener(this);
    }
}
