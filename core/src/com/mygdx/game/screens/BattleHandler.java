package com.mygdx.game.screens;

import com.mygdx.game.Entity.Enemy;
import com.mygdx.game.Entity.Player;
import com.mygdx.game.Event.BattleEvent;
import com.mygdx.game.Event.BattleState;

import java.util.Random;

public class BattleHandler {

    public GameScreen gameScreen;
    public BattleEventHandler battleEventHandler;
    public BattleScene battleScene;
    public DialogScreen specialDialog;
    public cardUI cardui;
    public Random r;

    public BattleState currentState;

    public BattleHandler(GameScreen gameScreen, Player p, Enemy monster){
        this.gameScreen = gameScreen;
        r = new Random();
        cardui = new cardUI(gameScreen);
        battleScene = new BattleScene(gameScreen,p,this, monster);//GameScreen gameScreen, Player player, Battle battle,
        battleEventHandler = new BattleEventHandler(gameScreen, p,this);
        specialDialog = new DialogScreen();
        engage(gameScreen.monster);
    }

    public void update(float dt) {
        if (currentState == BattleState.DIALOG) battleEventHandler.update(dt);
        battleScene.update(dt);
        battleScene.render(dt);
    }

    public void render(float dt) {
        battleScene.render(dt);
        battleEventHandler.update(dt);

        //Load new hand
        if (currentState == BattleState.MOVE) cardui.render(dt);
        if (currentState == BattleState.DIALOG) battleEventHandler.render(dt);
    }

    public void engage(Enemy enemy) {
        currentState = BattleState.DIALOG;
        String[] intro;

        //randomize between player turn and monster turn
        int event = new Random().nextInt(10 - 0);

        if (event > 5) {
            //monster turn
            intro = new String[]{
                    "You encountered " + enemy.getName() + "!",
                    enemy.getName() + ": " + specialDialog.mIntroDialog[r.nextInt(specialDialog.mIntroDialog.length)] + " " + enemy.getName(),
                    "You hesitated for a brief moment.",
                    enemy.getName() +  " took this moment to strike!",
                    "Preparing hand...",
                    "Battle start!"
            };

            battleEventHandler.startDialog(intro, BattleEvent.NONE, BattleEvent.ENEMY_TURN);
        } else {
            //player turn
            intro = new String[]{
                    "You encountered " + enemy.getName() + "!",
                    enemy.getName() + ": " + specialDialog.mIntroDialog[r.nextInt(specialDialog.mIntroDialog.length)] + " " + enemy.getName(),
                    "You happened to be faster than your opponent.",
                    "Preparing hand...",
                    "Battle start!"
            };
            battleEventHandler.startDialog(intro, BattleEvent.NONE, BattleEvent.PLAYER_TURN);
        }
    }
}
