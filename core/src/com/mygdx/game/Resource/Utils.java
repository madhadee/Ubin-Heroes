package com.mygdx.game.Resource;

public class Utils {
    //game dimension
    public final int V_WIDTH = 480;
    public final int V_HEIGHT = 720;

    //Monster Name
    public String monster_one = "BrittleBasher";
    public String monster_two = "ArcticBasher";
    public String monster_three = "ScorchBasher";

    public String boss_one = "Poly Orc";
    public String boss_two = "Steel Ogre";
    public String boss_three = "Leather Goblin";

    //card dimension
    public final int cardWidth = 120;
    public final int cardHeight = 160;

    public final String metal_weapon = "Metal Sword";
    public final String metal_shield = "Metal Shield";
    public final String metal_spell = "Metal Spell";

    public final String plastic_weapon = "Plastic Bow";
    public final String plastic_shield = "Plastic Shield";
    public final String plastic_spell = "Plastic Spell";

    public final String paper_weapon = "Paper Spear";
    public final String paper_shield = "Paper Shield";
    public final String paper_spell = "Paper Spell";

    public final String basic_sword = "Basic Sword";
    public final String basic_bow = "Basic Bow";
    public final String basic_spear = "Basic Spear";

    //volume
    public float sfx_vol = 1f;

    public double typeAttackStack (int type, int dmg) {
        double damage = dmg;
        //same type move, check type
        if (type == 1) {
            //metal
            damage = dmg * 2;
        } else if (type == 2) {
            damage = dmg + 2;
        } else {
            //paper
            damage = dmg + 1;
        }
        return damage;
    }


}
