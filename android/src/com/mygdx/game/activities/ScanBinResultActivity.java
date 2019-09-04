package com.mygdx.game.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.models.RecyclableObject;
import com.mygdx.game.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.work.WorkManager;

public class ScanBinResultActivity extends AppCompatActivity implements View.OnClickListener{
    //Variables
    private TextView tvScanStatus;
    private TextView tvLocation;
    private TextView tvPolyCount;
    private TextView tvLogamCount;
    private TextView tvKayuCount;

    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mInvertoryRef;
    private DatabaseReference mUseableRef;
    private DatabaseReference binScanCountRef;

    //Declare authentication
    private FirebaseAuth mAuth;

    private int recyclablePlastic = 0;
    private int recyclableMetal = 0;
    private int recyclablePaper = 0;

    private int receivedPlastic = 0;
    private int receivedMetal = 0;
    private int receivedPaper = 0;

    //Only increment bin scan based on barcode id if items are transferred
    private String barcodeId;
    private int itemsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bin_result);

        //Initialize UI
        initUI();

        //Get result from previous activity
        getResult();
    }

    /**
     * Initialise UI elements from XML
     */
    private void initUI(){
        tvScanStatus = findViewById(R.id.tvScanPrompt);
        tvLocation = findViewById(R.id.tvScanLocation);
        tvPolyCount = findViewById(R.id.tvScanPolyCount);
        tvLogamCount = findViewById(R.id.tvScanLogamCount);
        tvKayuCount = findViewById(R.id.tvScanKayuCount);
    }

    /**
     * Carry out the whole process of verifying location and generate materials
     */
    private void getResult(){
        Intent intent2 = getIntent();
        Bundle bundle = intent2.getExtras();

        double longitude = bundle.getDouble(Constants.SCAN_LONGITUDE);
        double latitude = bundle.getDouble(Constants.SCAN_LATITUDE);
        barcodeId = bundle.getString("barcode_id");
        String barcodePostalCode = bundle.getString("barcode_postalCode");
        int barcode_total_scans = bundle.getInt("barcode_scan_number");

        //Get bin's address based on bin location to display
        String binAddress = getAddress(barcodePostalCode);
        Log.d("LocationAddress", binAddress);
        Log.d("Barcode Postal", "getResult: " + barcodePostalCode);
        Log.d("Barcode Scan Counts", "getResult: " + barcode_total_scans);

        //Get Scanned Recycle Bin Coordinates to verify with user coordinates
        ArrayList<Double> binPostalCoords = getPostalLongLat(barcodePostalCode);

        //push barcode postalcode to geolocation get long lat
        //If location of recycleBin is same as the scanned QRCode Bin then push items to db and cancelwork.
        //(SIT@NYP - 567739), (SIT Dover - 138683)
        if (withinRange(binPostalCoords.get(0),binPostalCoords.get(1),latitude,longitude)) {
            tvLocation.setText(binAddress);
            mAuth = FirebaseAuth.getInstance();
            cancelWork();
            generateMaterials();
            updateRecycleCount();
        } else {
            tvScanStatus.setText(Constants.SCAN_ERROR);
            tvLocation.setText(null);
        }
    }

    /**
     * Geodecoder, Find if user is within 150m range of recycle bin
     * @param binLatitude, binLongitude, userLatitude, userLongitude
     * @return ArrayList<Double> coordinates
     */
    private boolean withinRange(double binLatitude,double binLongitude, double userLatitude, double userLongitude){
        double theta = binLongitude - userLongitude;
        double dist = Math.sin(Math.toRadians(binLatitude)) * Math.sin(Math.toRadians(userLatitude)) + Math.cos(Math.toRadians(binLatitude)) * Math.cos(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist*1000;

        Log.d("TestDistance",binLatitude+" "+binLongitude);
        Log.d("TestDistance",userLatitude+" "+userLongitude);
        Log.d("TestDistance","Distance between user's location and QR location(in metres)" + String.valueOf(dist));

        //if user is within 150m range
        if (dist<150)
            return true;
        return false;
    }

    /**
     * Geodecoder, Get gps coordinates based on postal code
     * @param postalCode
     * @return ArrayList<Double> coordinates
     */
    private ArrayList<Double> getPostalLongLat(String postalCode){
        ArrayList<Double> coords = new ArrayList<>();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address>addresses = geocoder.getFromLocationName(postalCode, 1, 1.2276, 103.5970, 1.4754, 104.0254);
            if (addresses.size() > 0)
            {
                coords.add(addresses.get(0).getLatitude());
                coords.add(addresses.get(0).getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coords;
    }

    /**
     * Geodecoder, Get address from postalCode
     * @param postalCode
     * @return String address
     */
    private String getAddress(String postalCode) {
        ArrayList<String> addressL = new ArrayList<>();
        StringBuilder strReturnedAddress = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(postalCode, 1, 1.2276, 103.5970, 1.4754, 104.0254);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                Log.d("Returned Address", "getAddress: " + returnedAddress);

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strReturnedAddress.toString();
    }

    /**
     * For QR code notification
     */
    private void cancelWork(){
        WorkManager.getInstance().cancelAllWorkByTag("TimerJob1");
    }

    /**
     * Generate materials (plastic, metal, paper) from recyclable object and increment to usable list in Firebase
     */
    private void generateMaterials(){
        final FirebaseUser userFirebase = mAuth.getCurrentUser();
        mUseableRef = mRootReference.child(Constants.DB_REF_RECYCLABLE_OBJECT).child(userFirebase.getUid());
        //This is to check if the object is unverified. It will only pull items from firebase which are unverified.
        mUseableRef.orderByChild("verified").equalTo(0).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemsCount = (int) dataSnapshot.getChildrenCount();
                if(itemsCount>0)
                    incrementBinScanCount();
                Log.d("ScanBinResult", "Number of unverified items: "+ itemsCount);

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    RecyclableObject recyclableObject = new RecyclableObject();
                    recyclableObject.setType(userSnapshot.getValue(RecyclableObject.class).getType());
                    recyclableObject.setVerified(userSnapshot.getValue(RecyclableObject.class).getVerified());

                    if (recyclableObject.getType().equals(Constants.DB_REF_PLASTIC)){
                        recyclablePlastic +=1;
                    } else if (recyclableObject.getType().equals(Constants.DB_REF_METAL)){
                        recyclableMetal +=1;
                    } else if (recyclableObject.getType().equals(Constants.DB_REF_PAPER)){
                        recyclablePaper +=1;
                    }
                    userSnapshot.child("verified").getRef().setValue(1);
                }
                updateReceived(recyclablePlastic, recyclableMetal, recyclablePaper);
                updateUsableList(receivedPlastic, receivedMetal, receivedPaper);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Clear any count from previous. Clean slate
     */
    private void clear(){
        recyclablePlastic = 0;
        recyclableMetal = 0;
        recyclablePaper = 0;
        receivedPlastic = 0;
        receivedMetal = 0;
        receivedPaper = 0;
    }

    /**
     * Generate materials from received materials
     * @param recyclablePlastic Plastic received from scan trigger
     * @param recyclableMetal Metal received from scan trigger
     * @param recyclablePaper Paper received from scan trigger
     */
    private void updateReceived(int recyclablePlastic, int recyclableMetal, int recyclablePaper){
        receivedPlastic = materialGenerator(recyclablePlastic);
        receivedMetal = materialGenerator(recyclableMetal);
        receivedPaper = materialGenerator(recyclablePaper);
    }

    /**
     * From the received generated materials, a transaction is made with Firebase to update the usable list
     * @param receivedPlastic
     * @param receivedMetal
     * @param receivedPaper
     */
    private void updateUsableList(int receivedPlastic, int receivedMetal, int receivedPaper){
        final FirebaseUser userFirebase = mAuth.getCurrentUser();

        DatabaseReference usableList = mRootReference.child(Constants.DB_REF_USEABLE_LIST).child(userFirebase.getUid());

        tvPolyCount.setText(String.valueOf(receivedPlastic) + Constants.SCAN_QUANTITY + Constants.DB_REF_PLASTIC);
        tvLogamCount.setText(String.valueOf(receivedMetal) + Constants.SCAN_QUANTITY + Constants.DB_REF_METAL);
        tvKayuCount.setText(String.valueOf(receivedPaper) + Constants.SCAN_QUANTITY + Constants.DB_REF_PAPER);

        usableList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Increment value of card selected (Plastic)
                DatabaseReference plasticRef = mRootReference.child(Constants.DB_REF_USEABLE_LIST).child(userFirebase.getUid()).child(Constants.DB_REF_PLASTIC);
                plasticRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer plastic = mutableData.getValue(Integer.class);
                        if (plastic != null){
                            mutableData.setValue(plastic + receivedPlastic);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    }
                });

                //Increment value of card selected (Metal)
                DatabaseReference metalRef = mRootReference.child(Constants.DB_REF_USEABLE_LIST).child(userFirebase.getUid()).child(Constants.DB_REF_METAL);
                metalRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer metal = mutableData.getValue(Integer.class);
                        if (metal != null){
                            mutableData.setValue(metal + receivedMetal);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {}
                });

                //Increment value of card selected (Paper)
                DatabaseReference paperRef = mRootReference.child(Constants.DB_REF_USEABLE_LIST).child(userFirebase.getUid()).child(Constants.DB_REF_PAPER);
                paperRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer paper = mutableData.getValue(Integer.class);
                        if (paper != null){
                            mutableData.setValue(paper + receivedPaper);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        clear();
                        Log.d("Clear1", "Plastic:" + String.valueOf(recyclablePlastic) + "Metal" + String.valueOf(recyclableMetal) + "Paper" + String.valueOf(recyclablePaper));
                        Log.d("Clear2", "Plastic:" + String.valueOf(receivedPlastic) + "Metal" + String.valueOf(receivedMetal) + "Paper" + String.valueOf(receivedPaper));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    /**
     * At the same time, after a successful recycling session.
     * The recycling count for a user will be increment based on the amount of items scanned
     */
    private void updateRecycleCount() {
        final FirebaseUser userFirebase = mAuth.getCurrentUser();
        mInvertoryRef = mRootReference.child(Constants.DB_REF_USERS).child(userFirebase.getUid()).child(Constants.DB_REF_RECYCLE_COUNT);
        mInvertoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int recycledMaterials = Integer.parseInt(dataSnapshot.getValue().toString()) + recyclablePlastic + recyclableMetal + recyclablePaper;
                dataSnapshot.getRef().setValue(recycledMaterials);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    /**
     * At the same time, after a successful recycling session.
     * The recycling count for a Ubin at that location will be incremented
     */
    public void incrementBinScanCount(){
        binScanCountRef = mRootReference.child(Constants.DB_REF_QR_BINS).child(barcodeId).child(Constants.DB_REF_SCAN_COUNT);
        binScanCountRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer scanCount = mutableData.getValue(Integer.class);
                if (scanCount != null){
                    //increment scan count every successful count
                    mutableData.setValue(scanCount + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {}
        });
    }

    /**
     * Algorithm to decide the amount of materials generated
     * @param item The amount of items
     * @return amount of materials
     */
    public int materialGenerator(int item){
        if (item >0) {
            int materialGenerated = 0;
            for (int i =0;i<item;i++) {
                materialGenerated = materialGenerated + new Random().nextInt((3 - 1) + 1) + 1;
            }
            return materialGenerated;
        } else {
            return 0;
        }
    }

    @Override
    public void onClick (View v){
        int id = v.getId();
        switch (id){
            case R.id.btnScanBack:
                startActivity(new Intent(this, ScanOptionsActivity.class));
                overridePendingTransition(0, 0);
                break;
        }
    }
}