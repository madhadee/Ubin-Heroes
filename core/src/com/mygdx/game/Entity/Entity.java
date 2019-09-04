package com.mygdx.game.Entity;

public class Entity {
    float maxHP;
    float HP;
    int shield;
    boolean alive;
    boolean attacked;
    float level;

    public void setStats(float level) {
        if (level > 1  && level <= 5){
            HP = 10 *(1 + level / 10);
            maxHP = 10 * (1 + level / 10);
        }
        else if(level > 5) {
            HP = 15 *(1 + level/10);
            maxHP = 15 * (1 + level/10);
            System.out.println("Scaled by " + (1 + level/10));
            System.out.println("HP Jumped to " + maxHP);
        }
        else {
            this.HP = 10;
            this.maxHP = 10;
            this.level = 1;
        }
    }

    public void hit (int mdmg) {
        this.HP = this.HP - mdmg;
        if (this.HP <= 0) {
            this.alive = false;
            this.HP = 0;
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAttacked() {
        return attacked;
    }

    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    public float getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(float maxHP) {
        this.maxHP = maxHP;
    }

    public float getHP() {
        return HP;
    }

    public void setHP(float HP) {
        this.HP = HP;
    }




}
