package com.mygdx.game.Entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.UBinHeroes;

import java.util.Random;

public class Enemy extends Actor {
    String name;
    int id;

    //Monster Stats
    float dmg;
    int shield;
    float maxHP;
    float HP;
    float level;
    boolean isBoss;
    boolean alive;
    boolean attacked;
    int move;
    Random r;

    //enemy animation
    private Animation<TextureRegion> animation;
    private float showTime;

    public Enemy(String name) {
        this.name = name;
        this.alive = true;
        this.attacked = false;
        this.isBoss = false;
        r = new Random();
    }

    float myDelta;

    @Override
    public void act(float delta){
        super.act(delta);
        myDelta = delta;
        showTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);
        batch.draw(animation.getKeyFrame(showTime), UBinHeroes.V_WIDTH / 2 - 150, UBinHeroes.V_HEIGHT / 2 + 100, 300, 300);
    }

    public int heal(){
        Random r = new Random();
        int healed =  r.nextInt(((int)dmg - 1) + 1) + 1;
        HP = HP + healed;
        if (HP >= maxHP) {
            HP = maxHP;
        }
        return healed;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    public void setShowTime(float showTime) {
        this.showTime = showTime;
    }

    public float getMaxHP() {
        return maxHP;
    }

    public float getHP() {
        return HP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDmg() {
        return dmg;
    }

    public void setDmg(int dmg) {
        this.dmg = dmg;
    }

    public int getID(){
        return id;
    }

    public void setLevel(float level) { this.level = level; }

    public float getLevel(){
        return this.level;
    }

    public void setStats(float level) {
        //scale monster/boss based on player level
        if (isBoss) {
            dmg = 5 *(1 + (level+5)/10);
            HP = 10 *(1 + (level+5)/10);
            maxHP = 10 *(1 + (level+5)/10);
            this.level = level;
        }
        else {
            if (level > 1  && level <=5) {
                dmg = 2 ;
                HP = 10 *(1 + level/10);
                maxHP = 10 * (1 + level/10);
                this.level = level;
            }
            else if (level >5) {
                dmg = 4;
                HP = 15 *(1 + level/10);
                maxHP = 15 * (1 + level/10);
                this.level = level;
            }
            else {
                this.dmg = 3;
                this.HP = 10;
                this.maxHP = 10;
                this.level = 1;
            }
        }
    }

    public void hit (int mdmg) {
        this.HP = this.HP - mdmg;
        if (this.HP <= 0) {
            this.alive = false;
            this.HP = 0;
        }
    }

    public int monsterDamage() {
        return r.nextInt(((int)dmg - 1) + 1) + 1;
    }

    public void generateShield() {
        shield =  r.nextInt(5);
        if (shield == 0) {
            shield += 1;
        }
    }

    public boolean getBoss() {
        return isBoss;
    }

    public void setBoss(boolean boss) {
        isBoss = boss;
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

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    public void setID(int id) {
        this.id = id;
    }

    public boolean criticalMode(){
        if (HP/maxHP < 0.3f) {
            //if hp is less than 30% critical mode
            return true;
        } else {
            return false;
        }
    }
}
