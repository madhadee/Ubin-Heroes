package com.mygdx.game.models;

public class UserObject {
    private String username;
    private String district;
    private String prestigeTitle = "Karang Guni";
    private String profilePicUrl;
    private int prestigeLvl = 1;
    private int prestigePoints;
    private int recyclingCount;
    private int winCount;
    private int districtRank;

    public UserObject(){
        // Default constructor required for calls to DataSnapshot.getValue(UserObject.class)
    }

    public UserObject(String username, String district){
        this.username = username;
        this.district = district;
    }

    public UserObject(String username, int prestigeLvl, int recyclingCount, int winCount, int districtRank) {
        this.username = username;
        this.prestigeLvl = prestigeLvl;
        this.recyclingCount = recyclingCount;
        this.winCount = winCount;
        this.districtRank = districtRank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPrestigeTitle() { return prestigeTitle; }

    public void setPrestigeTitle(String prestigeTitle) { this.prestigeTitle = prestigeTitle; }

    public String getProfilePicUrl() { return profilePicUrl; }

    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }

    public int getPrestigeLvl() { return prestigeLvl; }

    public void setPrestigeLvl(int prestigeLvl) { this.prestigeLvl = prestigeLvl; }

    public int getPrestigePoints() { return prestigePoints; }

    public void setPrestigePoints(int prestigePoints) { this.prestigePoints = prestigePoints; }

    public int getRecyclingCount() { return recyclingCount; }

    public void setRecyclingCount(int recyclingCount) { this.recyclingCount = recyclingCount; }

    public int getWinCount() { return winCount; }

    public void setWinCount(int winCount) { this.winCount = winCount; }

    public int getDistrictRank() { return districtRank; }

    public void setDistrictRank(int districtRank) { this.districtRank = districtRank; }
}
