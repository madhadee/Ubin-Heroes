package com.mygdx.game.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UseableList {

    private int plastic;
    private int metal;
    private int paper;

    public UseableList(){
        // Default constructor required for calls to DataSnapshot.getValue(RecyclableObject.class)
    }

    public UseableList(int plastic, int metal, int paper) {
        this.plastic = plastic;
        this.metal = metal;
        this.paper = paper;
    }

    public int getPlastic() {
        return plastic;
    }

    public void setPlastic(int plastic) {
        this.plastic = plastic;
    }

    public int getMetal() {
        return metal;
    }

    public void setMetal(int metal) {
        this.metal = metal;
    }

    public int getPaper() {
        return paper;
    }

    public void setPaper(int paper) {
        this.paper = paper;
    }
}
