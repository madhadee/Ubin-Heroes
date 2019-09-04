package com.mygdx.game.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mygdx.game.R;
import com.mygdx.game.models.CardObject;

import java.util.ArrayList;
import java.util.List;

public class BattleListAdapter extends RecyclerView.Adapter<BattleListAdapter.CardViewHolder>{

    private final List<CardObject> mCardList;
    private final LayoutInflater mInflater;
    private final Context mContext;

    private int counter = 0;
    private int maxItem;
    public ArrayList<Integer> selectionList = new ArrayList<>();

    private FirebaseAuth mAuth;

    //Reference pointing to https://ubin-heroes.firebaseio.com/
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();

    class CardViewHolder extends RecyclerView.ViewHolder{
        final BattleListAdapter dAdapter;
        public final ImageView dCardImage;
        public final TextView dCardQuantity;

        private CardViewHolder(View itemView, BattleListAdapter adapter) {
            super(itemView);
            dCardImage = itemView.findViewById(R.id.ivCardImage);
            dCardQuantity = itemView.findViewById(R.id.tvCardQuantity);
            this.dAdapter = adapter;
        }
    }

    public BattleListAdapter(Context context, ArrayList<CardObject> cardList) {
        this.mCardList = cardList;
        this.mContext = context;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();

        final View mItemView = mInflater.inflate(R.layout.item_craftlist, parent, false);
        final CardViewHolder cardViewHolder = new CardViewHolder(mItemView,this);

        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        if (mCardList != null) {
            System.out.println("Card list size : " + mCardList.size());
            this.maxItem = mCardList.size();
            CardObject current = mCardList.get(position);
            holder.dCardImage.setImageResource(current.getImage());
            holder.dCardQuantity.setText(String.valueOf(current.getQuantity()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCardList.get(holder.getAdapterPosition()).setSelected(!mCardList.get(holder.getAdapterPosition()).isSelected());

                    boolean selected = mCardList.get(holder.getAdapterPosition()).isSelected();
                    int value = mCardList.get(holder.getAdapterPosition()).getCardID();
                    if (counter < maxItem && counter <5) {
                        //can select
                        //check if true or false
                        //if true, set to select and increment
                        //else set to not selected and decrement
                        if (selected) {
                            selectionList.add(Integer.valueOf(value));
                            counter++;
                            holder.itemView.setBackgroundColor(selected ? Color.CYAN : Color.TRANSPARENT);
                        }
                        if(!selected) {
                            //check if it exist in list
                            if(selectionList.contains(value)){
                                //exist in list then we remove it
                                selectionList.remove(Integer.valueOf(value));
                                counter--;
                                holder.itemView.setBackgroundColor(selected ? Color.CYAN : Color.TRANSPARENT);
                            }

                        }

                    } else {
                        //cannot select
                        //check if true
                        if (!selected) {
                            if(selectionList.contains(value)){
                                //exist in list then we remove it
                                selectionList.remove(Integer.valueOf(value));
                                counter--;
                                holder.itemView.setBackgroundColor(selected ? Color.CYAN : Color.TRANSPARENT);
                            }
                        }
                    }

                    System.out.println("Counter " + counter);
                }
            });

        } else {
            holder.dCardImage.setImageResource(R.drawable.card_blank_weapon);
        }
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }
}