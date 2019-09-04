package com.mygdx.game.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mygdx.game.R;
import com.mygdx.game.models.UserObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankingViewHolder extends RecyclerView.ViewHolder {
    private CircleImageView mDistrictProfilePic;
    private TextView mUsername;
    private TextView mPrestigeLvl;
    private TextView mRecycleCount;
    private TextView mWinCount;

    public RankingViewHolder(View itemView) {
        super(itemView);

        this.mDistrictProfilePic = itemView.findViewById(R.id.civRankingProfilePics);
        this.mUsername = itemView.findViewById(R.id.tvDistrictUser);
        this.mPrestigeLvl = itemView.findViewById(R.id.tvPrestige);
        this.mRecycleCount = itemView.findViewById(R.id.tvRecycleCount);
        this.mWinCount = itemView.findViewById(R.id.tvDistrictWins);
    }

    public void setRanking(UserObject userObject,Context context){
        String dUsername = userObject.getUsername();
        int dPrestigeLvl = userObject.getPrestigeLvl();
        int dRecycleCount = userObject.getRecyclingCount();
        int dWinCount = userObject.getWinCount();
        int dRankCount = userObject.getDistrictRank();

        String allUrl = userObject.getProfilePicUrl();
        Glide.with(context).load(allUrl)
                .apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).into(mDistrictProfilePic);
        mUsername.setText(dUsername);
        mPrestigeLvl.setText("Prestige Level " + Integer.valueOf(dPrestigeLvl).toString());
        mRecycleCount.setText(Integer.valueOf(dRecycleCount).toString());
        mWinCount.setText(Integer.valueOf(dWinCount).toString());
    }

    public void setmDistrictProfilePic(CircleImageView mDistrictProfilePic) {
        this.mDistrictProfilePic = mDistrictProfilePic;
    }

    public void setmUsername(TextView mUsername) {
        this.mUsername = mUsername;
    }

    public void setmPrestigeLvl(TextView mPrestigeLvl) {
        this.mPrestigeLvl = mPrestigeLvl;
    }

    public void setmRecycleCount(TextView mRecycleCount) {
        this.mRecycleCount = mRecycleCount;
    }

    public void setmWinCount(TextView mWinCount) {
        this.mWinCount = mWinCount;
    }
}