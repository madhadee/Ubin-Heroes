package com.mygdx.game.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.mygdx.game.R;
import com.mygdx.game.utils.Constants;

public class ScanOptionsActivity extends BaseActivity implements View.OnClickListener {
    // permissions needed.
    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_options);

        //Initialize UI
        initUI();

        //check if user has perimissions if not request the user
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, Constants.PERMISSION_ALL);
        }
    }

    /**
     *
     * @param context this activity
     * @param permissions which permissions requested
     * @return return if the permission is true or false
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onClick (View v){
        int id = v.getId();
        switch (id){
            case R.id.btnHome:
                startActivity(new Intent(ScanOptionsActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnRanking:
                startActivity(new Intent(ScanOptionsActivity.this, RankingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnScanItems:
                startActivity(new Intent(ScanOptionsActivity.this, ScanItemsActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnScanBin:
                startActivity(new Intent(ScanOptionsActivity.this, ScanBinActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnInventory:
                startActivity(new Intent(ScanOptionsActivity.this, InventoryActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnProfile:
                startActivity(new Intent(ScanOptionsActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btnDivider:
                startActivity(new Intent(ScanOptionsActivity.this, MapActivity.class));
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
        findViewById(R.id.btnInventory).setOnClickListener(this);
        findViewById(R.id.btnProfile).setOnClickListener(this);
        findViewById(R.id.btnScanItems).setOnClickListener(this);
        findViewById(R.id.btnScanBin).setOnClickListener(this);
        findViewById(R.id.btnDivider).setOnClickListener(this);
    }
}
