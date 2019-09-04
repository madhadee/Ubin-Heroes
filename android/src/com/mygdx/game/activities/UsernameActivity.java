package com.mygdx.game.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.models.CardObject;
import com.mygdx.game.models.UseableList;
import com.mygdx.game.models.UserObject;
import com.mygdx.game.utils.Constants;

import java.util.concurrent.ExecutionException;

public class UsernameActivity extends AppCompatActivity implements View.OnClickListener {
    //Variables
    private EditText mUsername;
    private EditText mDistrict;
    private TextView mWelcome;

    //Firebase Database declarations
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    //Declare authenticator to be used by child Activities
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUserFirebase;
    private DatabaseReference mUseableRef;
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        //Initialize authenticator
        mAuth = FirebaseAuth.getInstance();
        //Get instance of current user logged in
        mUserFirebase = mAuth.getCurrentUser();
        //Get instance of Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Reference to Firebase
        myRef = mFirebaseDatabase.getReference();

        //Initialize UI
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        String welcome = getString(R.string.title_registration_intro, mUserFirebase.getDisplayName());
        mWelcome.setText(welcome);
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        String usernameInput = mUsername.getText().toString();
        String districtInput = mDistrict.getText().toString();
        switch (id) {
            case R.id.btnConfirm:
                if (!usernameInput.isEmpty() && !districtInput.isEmpty()) {
                    if (isPostalCodeValid(Integer.valueOf(districtInput))) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                //This is to grey out button to prevent user from submitting multiple request
                                Button button;
                                button = findViewById(R.id.btnConfirm);
                                button.setAlpha(.5f);
                                button.setClickable(false);
                                String location = retrieveDistrict(districtInput);
                                initDatabase(usernameInput, location);
                                startActivity(new Intent(UsernameActivity.this, MainActivity.class));
                            }
                        };
                        thread.start();
                    }
                }
                else {
                    Toast.makeText(this, "Please fill in everything!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Populate and create an instance of a DB contents of the user
     * @param username of login user
     * @param district of the user
     */
    private void initDatabase(String username, String district){
        populateUsers(username,district);
        populateMaterials();
        populateCards();
    }

    /**
     * Populate into users
     * @param username of login user
     * @param district of the user
     */
    public void populateUsers(String username, String district){
        UserObject userObject = new UserObject(username, district);
        String url = mUserFirebase.getPhotoUrl().toString();

        myRef.child(Constants.DB_REF_USERS).child(mUserFirebase.getUid()).setValue(userObject);
        myRef.child(Constants.DB_REF_USERS).child(mUserFirebase.getUid()).child(Constants.DB_REF_PROFILE_PIC).setValue(url);
    }

    /**
     * Populate an instance of materials of the current user
     */
    public void populateMaterials(){
        UseableList useableList = new UseableList();
        myRef.child(Constants.DB_REF_USEABLE_LIST).child(mUserFirebase.getUid()).setValue(useableList);
    }

    /**
     * Populate an instance of cards of the current user
     */
    public void populateCards(){
        CardObject cardObject;
        for (int i =1; i <10; i++){
            cardObject = new CardObject(i);
            myRef.child(Constants.DB_REF_CARD_OBJECT).child(mUserFirebase.getUid()).child(String.valueOf(cardObject.getCardID())).setValue(cardObject);
        }
    }

    /**
     * Check if postal code is valid
     * @param districtInput postal code
     * @return status
     */
    private boolean isPostalCodeValid(int districtInput){
        int numOfDigits = String.valueOf(districtInput).length();

        if (numOfDigits == 6){
            return true;
        } else {
            Toast.makeText(this, "Please enter a valid postal code!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Return the district allocated to the user
     * @param postal_code
     * @return district
     */
    public String retrieveDistrict(String postal_code) {
        String postal_area = postal_code.substring(0,2);
        mUseableRef = mRootReference.child("district");
        Task<String> userDistrict = getDistrict(postal_area);
        String result = "";
        try {
            Tasks.await(userDistrict);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (userDistrict.isSuccessful()) {
            result = userDistrict.getResult();
        }
        Log.d("TEST", "retrieveDistrict: "+ result);
        return result;
    }

    /**
     * Get district from Firebase to see if exist
     * @param postal_area Postal code
     * @return
     */
    public Task<String> getDistrict(String postal_area){
        final TaskCompletionSource<String> tcs = new TaskCompletionSource();
        mUseableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean foundDistrictFlag = false;
                for (DataSnapshot district : dataSnapshot.getChildren()) {
                    if (district.getValue().toString().contains(postal_area)){
                        tcs.setResult(district.getKey());
                        foundDistrictFlag = true;
                        break;
                    }
                }
                if (!foundDistrictFlag)
                    tcs.setResult("No District Found");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tcs.setException(databaseError.toException());
            }
        });
        return tcs.getTask();
    }

    /**
     * Initialise UI elements from XML
     */
    public void initUI(){
        //Button listeners
        mUsername = findViewById(R.id.etUsername);
        mDistrict = findViewById(R.id.etPostal);
        mWelcome = findViewById(R.id.tvIntro);
        findViewById(R.id.btnConfirm).setOnClickListener(this);
    }
}