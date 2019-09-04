package com.mygdx.game.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.adapters.RecyclableListAdapter;
import com.mygdx.game.models.RecyclableObject;
import com.mygdx.game.utils.Constants;

import java.util.ArrayList;

public class InventoryItemFragment extends Fragment implements View.OnClickListener {

    //Variables
    private RecyclerView mRecyclerView;
    private RecyclableListAdapter mAdapter;
    private ArrayList<RecyclableObject> mRecyclableList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference mUseableRef;

    //Reference pointing to https://ubin-heroes.firebaseio.com/
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();

    private OnFragmentInteractionListener mListener;
    private ValueEventListener mPopulateListener;

    public InventoryItemFragment() {
        // Required empty public constructor
    }

    /**
     * Constructor of fragment
     * @param param1
     * @param param2
     * @return fragment
     */
    public static InventoryCraftFragment newInstance(String param1, String param2) {
        InventoryCraftFragment fragment = new InventoryCraftFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        populateRecyclable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_inventory_item, container, false);
        mRecyclerView = rootView.findViewById(R.id.rvRecyclableItems);
        mAdapter = new RecyclableListAdapter(inflater.getContext(), mRecyclableList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        return rootView;
    }

    /**
     * Button input to check for clicks on a fragment
     * @param uri
     */
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * Populate the recyclable items scanned into the recycler view
     * Add countdown for a item expiry and will delete itself after an hgour
     */
    public void populateRecyclable(){
        final FirebaseUser userFirebase = mAuth.getCurrentUser();

        mUseableRef = mRootReference.child(Constants.DB_REF_RECYCLABLE_OBJECT).child(userFirebase.getUid());
        //This is to sort the inventory recycler view to be sorted based on unverified items followed by verified
        mPopulateListener = mUseableRef.orderByChild("verified").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Clear the RecyclerView render to start fresh
                mRecyclableList.clear();

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    //Set values into each card
                    RecyclableObject recyclableObject = new RecyclableObject();
                    recyclableObject.setType(userSnapshot.getValue(RecyclableObject.class).getType());
                    recyclableObject.setVerified(userSnapshot.getValue(RecyclableObject.class).getVerified());
                    recyclableObject.setTimeStamp(userSnapshot.getValue(RecyclableObject.class).getTimeStamp());

                    //Add to recycler list
                    mRecyclableList.add(recyclableObject);
                }
                //Tell adapter change has happened
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Remove listener for memory management
     */
    @Override
    public void onStop() {
        super.onStop();
        mUseableRef.removeEventListener(mPopulateListener);
    }
}