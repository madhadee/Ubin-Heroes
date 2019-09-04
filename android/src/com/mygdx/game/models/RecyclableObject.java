package com.mygdx.game.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.mygdx.game.R;

import java.util.Date;

@IgnoreExtraProperties
public class RecyclableObject {
    private String type;
    private Date timeStamp;
    private int verified = 0;

    public RecyclableObject() {
        // Default constructor required for calls to DataSnapshot.getValue(RecyclableObject.class)
    }

    public RecyclableObject(String type, Date timeStamp, int verified) {
        this.type = type;
        this.timeStamp = timeStamp;
        this.verified = verified;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    @Exclude
    public String getVerification() {
        switch (verified) {
            case 0:
                return "Not Verified";
            case 1:
                return "Verified";
            default:
                break;
        }
        return "-";
    }

    @Exclude
    public int getImage() {
        switch (type) {
            case "plastic":
                return R.drawable.ic_plastic;
            case "metal":
                return R.drawable.ic_metal;
            case "paper":
                return R.drawable.ic_paper;
            default:
                break;
        }
        return 0;
    }
}
