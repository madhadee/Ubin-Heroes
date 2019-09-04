package com.mygdx.game.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.R;
import com.mygdx.game.adapters.CardListAdapter;
import com.mygdx.game.models.CardObject;
import com.mygdx.game.utils.Constants;

import java.util.ArrayList;

public class InventoryCraftFragment extends Fragment{

    //Variables
    private RecyclerView mRecyclerView;
    private CardListAdapter mAdapter;
    private ArrayList<CardObject> mRecyclableList = new ArrayList<>();
    private FirebaseAuth mAuth;

    //Reference pointing to https://ubin-heroes.firebaseio.com/
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();

    private OnFragmentInteractionListener mListener;

    public InventoryCraftFragment() {
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
        populateCards();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_inventory_craft, container, false);
        mAdapter = new CardListAdapter(inflater.getContext(), mRecyclableList);
        mRecyclerView = rootView.findViewById(R.id.rvCraftCards);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 3));
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Populate user cards from Firebase eg. quantity into the recyclable list
     */
    public void populateCards(){

        final FirebaseUser userFirebase = mAuth.getCurrentUser();

        mRootReference.child(Constants.DB_REF_CARD_OBJECT).child(userFirebase.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               //Clear the RecyclerView render to start fresh
               mRecyclableList.clear();

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {

                    //Set values into each card
                    CardObject cardObject = new CardObject();
                    cardObject.setCardID(userSnapshot.getValue(CardObject.class).getCardID());
                    cardObject.setQuantity(userSnapshot.getValue(CardObject.class).getQuantity());

                    //Add to recycler list
                    mRecyclableList.add(cardObject);
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
}