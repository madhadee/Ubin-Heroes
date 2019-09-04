package com.mygdx.game.Entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Card {
    int cardID;
    String name;
    String desc;
    int type;
    int dmg;
    int shield;
    int lvl;
    Image cardImg;
    Texture cardTexuture;

    public Card(int cardID, String name, String desc, int type, int dmg,int shield, int lvl, Image cardImg) {
        this.cardID = cardID;
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.dmg = dmg;
        this.shield = shield;
        this.lvl = lvl;
        this.cardImg = cardImg;
    }

    public int getCardID() {
        return cardID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDmg() {
        return dmg;
    }

    public int getLvl() {
        return lvl;
    }

    public int getShield() {
        return shield;
    }

    public Image getCardImg() {
        return cardImg;
    }

    public void setCardImg(Image cardImg) {
        this.cardImg = cardImg;
    }

    public Texture getCardTexuture() {
        return cardTexuture;
    }

    public void setCardTexuture(Texture cardTexuture) {
        this.cardTexuture = cardTexuture;
    }
}
