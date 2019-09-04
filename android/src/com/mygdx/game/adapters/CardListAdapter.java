package com.mygdx.game.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.mygdx.game.models.CardObject;
import com.mygdx.game.models.UseableList;
import com.mygdx.game.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardViewHolder>{

    private final List<CardObject> mCardList;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private Dialog mDialog;
    private Dialog cDialog;
    private Dialog mSuccessDialog;
    private Dialog mErrorDialog;
    private TextView polyCost;
    private TextView logamCost;
    private TextView kayuCost;
    private TextView craftButton;
    private Button btnOkay;
    private ImageView bigCardImage;

    private FirebaseAuth mAuth;

    //Reference pointing to https://ubin-heroes.firebaseio.com/
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();

    class CardViewHolder extends RecyclerView.ViewHolder{
        final CardListAdapter dAdapter;
        public final ImageView dCardImage;
        public final TextView dCardQuantity;

        private CardViewHolder(View itemView, CardListAdapter adapter) {
            super(itemView);
            dCardImage = itemView.findViewById(R.id.ivCardImage);
            dCardQuantity = itemView.findViewById(R.id.tvCardQuantity);
            this.dAdapter = adapter;
        }
    }

    public CardListAdapter(Context context, ArrayList<CardObject> cardList) {
        this.mCardList = cardList;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();

        final View mItemView = mInflater.inflate(R.layout.item_craftlist, parent, false);
        final CardViewHolder cardViewHolder = new CardViewHolder(mItemView,this);

        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_craft);

        cDialog = new Dialog(mContext);
        cDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cDialog.setContentView(R.layout.dialog_card);

        polyCost = mDialog.findViewById(R.id.tvPolyCost);
        logamCost = mDialog.findViewById(R.id.tvLogamCost);
        kayuCost = mDialog.findViewById(R.id.tvKayuCost);

        craftButton = mDialog.findViewById(R.id.dialogCraftCreate);
        bigCardImage = cDialog.findViewById(R.id.ivCardBig);

        //Card on click function
        cardViewHolder.dCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int poly = mCardList.get(cardViewHolder.getAdapterPosition()).getCost().get(0);
                int logam = mCardList.get(cardViewHolder.getAdapterPosition()).getCost().get(1);
                int kayu = mCardList.get(cardViewHolder.getAdapterPosition()).getCost().get(2);
                int selectedCard = cardViewHolder.getAdapterPosition() + 1;

                polyCost.setText(String.valueOf(poly));
                logamCost.setText(String.valueOf(logam));
                kayuCost.setText(String.valueOf(kayu));

                mDialog.show();

                craftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        craftAction(poly, logam, kayu, selectedCard);
                        mDialog.cancel();
                    }
                });
            }
        });

        cardViewHolder.dCardImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                bigCardImage.setImageResource(mCardList.get(cardViewHolder.getAdapterPosition()).getImage());

                cDialog.show();

                bigCardImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cDialog.cancel();
                    }
                });

                return false;
            }
        });

        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        if (mCardList != null) {
            CardObject current = mCardList.get(position);
            holder.dCardImage.setImageResource(current.getImage());
            holder.dCardQuantity.setText(String.valueOf(current.getQuantity()));
        } else {
            holder.dCardImage.setImageResource(R.drawable.card_blank_weapon);
        }
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    /**
     * Implement crafting of card based on cost and current user usable materials
     * @param polyCost Plastic materials
     * @param logamCost Metal materials
     * @param kayuCost Paper materials
     * @param selectedCard Card to be crafted
     */
    public void craftAction(int polyCost, int logamCost, int kayuCost, int selectedCard){

        final FirebaseUser userFirebase = mAuth.getCurrentUser();
        DatabaseReference useableList = mRootReference.child(Constants.DB_REF_USEABLE_LIST).child(userFirebase.getUid());

       useableList.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int userPoly = dataSnapshot.getValue(UseableList.class).getPlastic();
                int userLogam = dataSnapshot.getValue(UseableList.class).getMetal();
                int userKayu = dataSnapshot.getValue(UseableList.class).getPaper();

                if (userPoly >= polyCost && userLogam >= logamCost && userKayu >= kayuCost){

                    //Increment value of card selected
                    DatabaseReference cardRef = mRootReference.child(Constants.DB_REF_CARD_OBJECT).child(userFirebase.getUid()).child(String.valueOf(selectedCard)).child("quantity");
                    cardRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            Integer quantity = mutableData.getValue(Integer.class);
                            mutableData.setValue(quantity + 1);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                        }
                    });

                    //Increment value of card selected (Plastic)
                    DatabaseReference plasticRef = mRootReference.child(Constants.DB_REF_USEABLE_LIST).child(userFirebase.getUid()).child(Constants.DB_REF_PLASTIC);
                    plasticRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            Integer plastic = mutableData.getValue(Integer.class);
                            mutableData.setValue(plastic - polyCost);
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
                            mutableData.setValue(metal - logamCost);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                        }
                    });

                    //Increment value of card selected (Paper)
                    DatabaseReference paperRef = mRootReference.child(Constants.DB_REF_USEABLE_LIST).child(userFirebase.getUid()).child(Constants.DB_REF_PAPER);
                    paperRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            Integer paper = mutableData.getValue(Integer.class);
                            mutableData.setValue(paper - kayuCost);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            mSuccessDialog = new Dialog(mContext);
                            mSuccessDialog.setContentView(R.layout.dialog_craft_success);
                            mSuccessDialog.show();

                            btnOkay = mSuccessDialog.findViewById(R.id.btnOkay);

                            btnOkay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mSuccessDialog.cancel();
                                }
                            });
                        }
                    });

                } else {

                    mErrorDialog = new Dialog(mContext);
                    mErrorDialog.setContentView(R.layout.dialog_craft_error);
                    mErrorDialog.show();

                    btnOkay = mErrorDialog.findViewById(R.id.btnOkay);

                    btnOkay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mErrorDialog.cancel();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}