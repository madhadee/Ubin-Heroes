package com.mygdx.game.Event;

import com.mygdx.game.Entity.Player;
import com.mygdx.game.Resource.Utils;
import com.mygdx.game.screens.DialogScreen;
import com.mygdx.game.screens.GameScreen;

import java.util.Random;

public class Battle {
    public GameScreen gameScreen;
    private Player player;
    public DialogScreen specialDialog;
    Random r;
    Utils util;

    public Battle(GameScreen gameScreen, Player player) {
        this.gameScreen = gameScreen;
        this.player = player;
        specialDialog = new DialogScreen();
        r = new Random();
        util = new Utils();
    }

    public void playerAttack(String name, int type, int dmg, int id, int shieldpnt) {
        int prevmove = 0;
        boolean typeStacked = false;
        boolean specialStackAttack = gameScreen.battleHandler.battleScene.player.isStackedAttack();
        String typeMessage = "You strike " + gameScreen.battleHandler.battleScene.monster.getName();

        if (gameScreen.battleHandler.battleScene.player.getPrevMove() != 0) {
            prevmove = gameScreen.battleHandler.battleScene.player.getPrevMove();
        }

        gameScreen.battleHandler.battleScene.monster.setAttacked(true);
        int shield = gameScreen.battleHandler.battleScene.player.getShield();

        if (prevmove != 0 && prevmove == type && !specialStackAttack) {
            dmg = (int) util.typeAttackStack(type,dmg);
            typeStacked = true;
            gameScreen.battleHandler.battleScene.player.setStackedAttack(true);
        } else {
            gameScreen.battleHandler.battleScene.player.setStackedAttack(false);
        }

        dmg = gameScreen.battleHandler.battleEventHandler.shieldCalculator(dmg, false,type);
        String[] intro;

        gameScreen.battleHandler.battleScene.monster.hit(dmg);
        gameScreen.battleHandler.battleScene.monsterHealthBar.setHP(gameScreen.battleHandler.battleScene.monster.getHP());
        shield = shield + shieldpnt;

        if(shield >= 10){
            shield = 10;
        }

        gameScreen.battleHandler.battleScene.player.setShield(shield);
        gameScreen.battleHandler.battleScene.player.setPrevMove(type);

        if (typeStacked) {
            if (type == 1) {//Metal
                typeMessage = "You wind up your next attack.. Metal Smash!";
            }
            else if (type == 2) {//Plastic
                typeMessage = "You strategically stike its weak points.. Plastic Strike!";
            }
            else {//paper
                typeMessage = "You focus on your next attack.. Paper Piercer!";
            }


        }

        if (gameScreen.battleHandler.battleScene.monster.isAlive()) {
            intro = new String[]{
                    typeMessage,
                "Player used " + name + ". Dealt " + dmg, "You have " + player.getShield() + " Armor",
                    gameScreen.battleHandler.battleScene.monster.getName() + ": " + specialDialog.mAttackedDialog[r.nextInt(specialDialog.mResponseDialog.length)] + ".",
                    gameScreen.battleHandler.battleScene.monster.getName() + " " +
                            specialDialog.mResponseDialog[r.nextInt(specialDialog.mResponseDialog.length)]
            };
            gameScreen.battleHandler.battleEventHandler.startDialog(intro,BattleEvent.PLAYER_TURN,BattleEvent.ENEMY_TURN);
            gameScreen.battleHandler.battleScene.player.setAttacked(true);
            gameScreen.battleHandler.battleScene.checkMove(id);
        } else {
            gameScreen.battleHandler.battleEventHandler.monster_dying.play(util.sfx_vol);
            intro = new String[]{
                    "Player used " + name + ". Dealt " + dmg,
                    "Player dealt the final blow..",
                    gameScreen.battleHandler.battleScene.monster.getName() + " : " + specialDialog.mDefeatedDialog[r.nextInt(specialDialog.mDefeatedDialog.length)],
                    gameScreen.battleHandler.battleScene.monster.getName() + " is defeated.."
            };
            gameScreen.battleHandler.battleEventHandler.startDialog(intro,BattleEvent.PLAYER_TURN,BattleEvent.END_BATTLE);
            gameScreen.battleHandler.battleScene.player.setAttacked(true);
        }
    }

}
