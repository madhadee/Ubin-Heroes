package com.mygdx.game.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.mygdx.game.R;

import java.util.Arrays;
import java.util.List;

@IgnoreExtraProperties
public class CardObject {

    private int cardID;
    private String type;
    private int quantity;
    private int damage;
    private boolean isSelected;


    public CardObject(){
        // Default constructor required for calls to DataSnapshot
    }

    public CardObject(int cardID){
        this.cardID = cardID;
        this.setCardType();
    }

    public CardObject(String type, int quantity, int damage) {
        this.type = type;
        this.quantity = quantity;
        this.damage = damage;
    }

    public int getCardID() {
        return cardID;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Exclude
    public String setCardType() {
        if (cardID == 1 | cardID == 2 | cardID == 3){
            this.type = "plastic";
        }
        else if (cardID == 4 | cardID == 5| cardID == 6 ){
            this.type = "metal";
        }
        else{
            this.type= "paper";
        }
        return null;
    }

    @Exclude
    public int getImage() {
        switch (cardID) {
            case 1:
                return R.drawable.card_plastic_weapon;
            case 2:
                return R.drawable.card_plastic_shield;
            case 3:
                return R.drawable.card_plastic_spell;
            case 4:
                return R.drawable.card_metal_weapon;
            case 5:
                return R.drawable.card_metal_shield;
            case 6:
                return R.drawable.card_metal_spell;
            case 7:
                return R.drawable.card_paper_weapon;
            case 8:
                return R.drawable.card_paper_shield;
            case 9:
                return R.drawable.card_paper_spell;
            default:
                break;
        }
        return 0;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Exclude
    public List<Integer> getCost() {
        List<Integer> cost;
        switch (cardID) {
            case 1:
                cost = Arrays.asList(1, 0, 0);
                return cost;
            case 2:
                cost = Arrays.asList(2, 1, 0);
                return cost;
            case 3:
                cost = Arrays.asList(2, 0, 1);
                return cost;
            case 4:
                cost = Arrays.asList(0, 1, 0);
                return cost;
            case 5:
                cost = Arrays.asList(0, 2, 1);
                return cost;
            case 6:
                cost = Arrays.asList(1, 2, 0);
                return cost;
            case 7:
                cost = Arrays.asList(0, 0, 1);
                return cost;
            case 8:
                cost = Arrays.asList(0, 1, 2);
                return cost;
            case 9:
                cost = Arrays.asList(1, 0, 2);
                return cost;
            default:
                break;
        }
        return null;
    }
}
