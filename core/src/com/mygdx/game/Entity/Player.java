package com.mygdx.game.Entity;

import java.util.ArrayList;

public class Player extends Entity{
    int prevMove;
    boolean stackedAttack = false;
    String player_name;

    public Player(String playerName) {
        this.alive = true;
        this.attacked = false;
        this.prevMove = 0;
        this.player_name = playerName;
    }


    public float getLevel(){
        return this.level;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public void setLevel(float level){
        this.level = level;
        setStats(level);
    }

    public boolean isStackedAttack() {
        return stackedAttack;
    }

    public void setStackedAttack(boolean stackedAttack) {
        this.stackedAttack = stackedAttack;
    }

    public int getPrevMove(){
        return prevMove;
    }

    public void setPrevMove(int id) {
        this.prevMove = id;
    }

}
