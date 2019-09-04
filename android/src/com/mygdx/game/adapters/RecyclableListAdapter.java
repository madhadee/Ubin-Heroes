package com.mygdx.game.adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mygdx.game.R;
import com.mygdx.game.models.RecyclableObject;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.NotificationWorker;

import java.util.ArrayList;
import java.util.List;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class RecyclableListAdapter extends RecyclerView.Adapter<RecyclableListAdapter.RecyclableViewHolder> {
    private final List<RecyclableObject> mRecyclableList;
    private final LayoutInflater mInflater;

    class RecyclableViewHolder extends RecyclerView.ViewHolder {
        public final TextView dRecyclableType;
        public final TextView dRecyclableDate;
        public final TextView dRecyclableVerified;
        public final TextView dRecyclableExpires;
        public final ImageView dRecyclableImage;

        final RecyclableListAdapter dAdapter;

        public RecyclableViewHolder(View itemView, RecyclableListAdapter adapter) {
            super(itemView);
            dRecyclableType = itemView.findViewById(R.id.tvRecyclableType);
            dRecyclableDate = itemView.findViewById(R.id.tvRecyclableTime);
            dRecyclableVerified = itemView.findViewById(R.id.tvRecyclableVerified);
            dRecyclableExpires = itemView.findViewById(R.id.tvRecyclableExpires);
            dRecyclableImage = itemView.findViewById(R.id.ivRecyclableImage);

            this.dAdapter = adapter;
        }
    }

    public RecyclableListAdapter(Context context, ArrayList<RecyclableObject> recyclableList) {
        this.mRecyclableList = recyclableList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Do stuff here
        View mItemView = mInflater.inflate(R.layout.recyclablelist_item, parent, false);
        return new RecyclableViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(RecyclableViewHolder holder, int position) {
        if (mRecyclableList != null) {
            RecyclableObject current = mRecyclableList.get(position);

            long expiryTime = current.getTimeStamp().getTime() + 3600000;
            long difference = expiryTime - System.currentTimeMillis();

            if(current.getVerified() == 0) {
                holder.dRecyclableExpires.setVisibility(View.VISIBLE);
                new CountDownTimer(difference, 1000) {

                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000) % 60;
                        int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                        int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);

                        holder.dRecyclableExpires.setText("Expires: " + hours + " Hours, " + minutes + " Minutes, " + seconds + " Seconds");
                    }

                    public void onFinish() {
                        holder.dRecyclableExpires.setText("Expired!");
                        Data myData = new Data.Builder()
                                .putString(Constants.KEY_Y_ARG, String.valueOf(current.getTimeStamp().getTime()))
                                .build();
                        OneTimeWorkRequest deleteRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                                .addTag("DeleteJob2")
                                .setInputData(myData)
                                .build();
                        WorkManager.getInstance().enqueue(deleteRequest);
                    }
                }.start();
            } else {
                Data myData = new Data.Builder()
                        .putString(Constants.KEY_Y_ARG, String.valueOf(current.getTimeStamp().getTime()))
                        .build();
                OneTimeWorkRequest deleteRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .addTag("DeleteJob2")
                        .setInputData(myData)
                        .build();
                WorkManager.getInstance().enqueue(deleteRequest);
            }
            holder.dRecyclableType.setText(current.getType());
            holder.dRecyclableDate.setText("Scanned on: " + current.getTimeStamp().toString());
            holder.dRecyclableVerified.setText(String.valueOf(current.getVerification()));
            holder.dRecyclableImage.setImageResource(current.getImage());
        } else {
            // Covers the case of data not being ready yet.
            holder.dRecyclableType.setText("No users in district");
        }
    }

    @Override
    public int getItemCount() {
        return mRecyclableList.size();
    }
}