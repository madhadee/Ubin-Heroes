package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entity.Enemy;
import com.mygdx.game.Entity.Player;
import com.mygdx.game.Event.BattleEvent;
import com.mygdx.game.Resource.Resource;
import com.mygdx.game.Resource.Utils;

import java.util.Random;

public class BattleEventHandler {

    public GameScreen gameScreen;
    public Player player;
    public Enemy monster;
    public BattleHandler battleHandler;
    public DialogScreen specialDialog;
    public Resource res;
    public Utils util;

    //ui stuffs
    private Stage stage;
    private float stateTime = 0;

    // the ui for displaying text
    private Image ui;

    // Label for text animation
    private Label textLabel;

    // invisible Label for clicking the window
    private Label clickLabel;

    //cycle
    private boolean beginCycle = false;
    private boolean endCycle = false;
    public BattleEvent prevEvent = BattleEvent.NONE;
    public BattleEvent nextEvent = BattleEvent.NONE;

    // text animation
    private String currentText = "";
    private String[] currentDialog = new String[0];
    private int dialogIndex = 0;
    private String[] anim;
    private String resultingText = "";
    private int animIndex = 0;

    public Sound textSound;
    public Sound bow;
    public Sound sword;
    public Sound spear;
    public Sound spell;
    public Sound shield;
    public Sound critical;
    public Sound heal;
    public Sound armor;
    public Sound monster_dying;
    public Sound monster_laugh;


    Random r;

    public BattleEventHandler(GameScreen gameScreen, Player player, BattleHandler battleHandler) {
        this.gameScreen = gameScreen;
        this.player = player;
        res = new Resource();
        util = new Utils();
        this.stage = this.gameScreen.stage;
        this.battleHandler = battleHandler;
        this.monster = battleHandler.battleScene.monster;
        specialDialog = new DialogScreen();

        //random init
        r = new Random();

        //sfx
        textSound = Gdx.audio.newSound(Gdx.files.internal(res.text_sound));
        bow = Gdx.audio.newSound(Gdx.files.internal(res.bow_sound));
        sword = Gdx.audio.newSound(Gdx.files.internal(res.sword_sound));
        spear = Gdx.audio.newSound(Gdx.files.internal(res.spear_sound));
        spell = Gdx.audio.newSound(Gdx.files.internal(res.spell_sound));
        shield = Gdx.audio.newSound(Gdx.files.internal(res.shield_sound));
        critical = Gdx.audio.newSound(Gdx.files.internal(res.critical_sound));
        heal = Gdx.audio.newSound(Gdx.files.internal(res.heal_sound));
        armor = Gdx.audio.newSound(Gdx.files.internal(res.armor_sound));
        monster_dying = Gdx.audio.newSound(Gdx.files.internal(res.dying_sound));
        monster_laugh = Gdx.audio.newSound(Gdx.files.internal(res.monster_laugh_sound));

        // create main UI
        ui = new Image(new Texture(Gdx.files.internal(res.dialog_box)));
        ui.setSize(480,100);
        ui.setPosition(0,0);
        ui.setTouchable(Touchable.disabled);

        stage = this.gameScreen.stage;
        stage.addActor(ui);
        Label.LabelStyle font = new Label.LabelStyle(gameScreen.app.font24, new Color(0, 0, 0, 1));

        textLabel = new Label("", font);
        textLabel.setWrap(true);
        textLabel.setTouchable(Touchable.disabled);
        textLabel.setFontScale(1.7f / 2);
        textLabel.setPosition(50, 50);
        textLabel.setSize(400, 52 / 2);
        textLabel.setAlignment(Align.topLeft);
        this.gameScreen.stage.addActor(textLabel);

        clickLabel = new Label("", font);
        clickLabel.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        clickLabel.setPosition(50, 50);

        clickLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (dialogIndex + 1 == currentDialog.length && endCycle) {
                    // the text animation has run through every element of the text array
                    textSound.play(util.sfx_vol);
                    endDialog();
                    handleBattleEvent(nextEvent);
                } else if (endCycle && dialogIndex < currentDialog.length) {
                    // after a cycle of text animation ends, clicking the UI goes to the next cycle
                    textSound.play(util.sfx_vol);
                    dialogIndex++;
                    reset();
                    currentText = currentDialog[dialogIndex];
                    anim = currentText.split("");
                    beginCycle = true;
                } else if (beginCycle && !endCycle) {
                    // clicking on the box during a text animation completes it early
                    resultingText = currentText;
                    textLabel.setText(resultingText);
                    beginCycle = false;
                    endCycle = true;
                }
            }
        });
        stage.addActor(clickLabel);
    }

    public void startDialog(String[] dialog, BattleEvent prev, BattleEvent next) {
        ui.setVisible(true);
        textLabel.setVisible(true);
        textLabel.setText("");
        clickLabel.setVisible(true);
        clickLabel.setTouchable(Touchable.enabled);

        currentDialog = dialog;
        currentText = currentDialog[0];
        anim = currentText.split("");

        prevEvent = prev;
        nextEvent = next;
        beginCycle = true;
    }

    public void endDialog() {
        reset();
        clickLabel.setTouchable(Touchable.disabled);
        dialogIndex = 0;
        currentDialog = new String[0];
    }

    public void reset() {
        stateTime = 0;
        currentText = "";
        textLabel.setText("");
        resultingText = "";
        animIndex = 0;
        anim = new String[0];
        beginCycle = false;
        endCycle = false;
    }

    public void update(float dt) {
        if (beginCycle) {
            stateTime += dt;

            if (animIndex >= anim.length) endCycle = true;

            // a new character is appended to the animation every TEXT_SPEED delta time
            if (stateTime > 0.03f && animIndex < anim.length && !endCycle) {
                resultingText += anim[animIndex];
                textLabel.setText(resultingText);
                animIndex++;
                stateTime = 0;
            }
        }
    }

    public void render(float dt) {
        stage.act(dt);
        stage.draw();
    }

    public void handleBattleEvent(BattleEvent event) {
        switch(event) {
            case PLAYER_TURN:
                battleHandler.cardui.myturn = true;
                textLabel.setText("Select a card...");

                //randomize next hand
                battleHandler.cardui.createMoveUI();
                break;

            case ENEMY_TURN:
                battleHandler.cardui.PAN = true;
                monsterTurn();
                break;

            case END_BATTLE:
                gameScreen.currentEvent = BattleEvent.END_BATTLE;
                break;
        }
    }

    private boolean applyPlayerDamage() {
        //randomize attack
        int dmg = monster.monsterDamage();
        int dmgtoplayer = dmg;
        String severity;
        String dialog[];

        //randomize attack type
        int type = r.nextInt(2) + 1;
        String typeAttack = "";

        if (type==1) {
            typeAttack = "Metal Damage, clumps of trash piled on you!";
            dmg = dmg * 2;
        } else if(type==2) {
            typeAttack = "Plastic Damage, sticky substance attached to you!";
            dmg = dmg + 2;
        } else if (type == 3) {
            typeAttack = "Paper Damage, it pierced through you!";
            dmg = dmg + 1;
        }

        severity = damageSeverity(dmg);

        //deduct from shield
        dmgtoplayer = shieldCalculator(dmg, true,type);

        //apply player damage
        battleHandler.battleScene.player.hit(dmgtoplayer);

        //reflect over to battlescene
        battleHandler.battleScene.playerHealthBar.setHP(battleHandler.battleScene.player.getHP());

        if (player.isAlive()) {
            //startDialog
            dialog = new String[]{
                    monster.getName() + ": " + specialDialog.mAttackingDialog[r.nextInt(specialDialog.mAttackingDialog.length)] + ".",
                    monster.getName() + " attacks you" + ".",
                    typeAttack ,
                    "Dealt " + + dmg + " " + severity + "." + "\n" +
                            "You lose " + dmgtoplayer + " HP.",
            };
            battleHandler.battleEventHandler.startDialog(dialog,BattleEvent.ENEMY_TURN,BattleEvent.PLAYER_TURN);
        } else {
            monster_laugh.play(util.sfx_vol);
            //player dead
            dialog = new String[]{
                    monster.getName() + ": " + specialDialog.mPlayerDefeat[r.nextInt(specialDialog.mPlayerDefeat.length)] + ".",
                    "Dealt " + dmg + " " + severity + "." + "\n" +
                            "You lose " + dmgtoplayer + " HP.",
                    monster.getName() + " dealt the final blow...",
            "You got knocked out!"
            };
            battleHandler.battleScene.player.setAlive(false);
            //game over dialog
            battleHandler.battleEventHandler.startDialog(dialog,BattleEvent.ENEMY_TURN,BattleEvent.END_BATTLE);
        }
        return false;
    }

    public String damageSeverity(int dmg){
        if (dmg > 4) {
            critical.play(1f);
            return "significant damage ";
        } else {
            sword.play(1f);
            return "weak damage ";
        }
    }

    public int shieldCalculator(int dmg, boolean isplayer,int type){
        //deduct from shield
        if (isplayer) {
            if (type == 3) {
                //ignore shield
                return dmg;
            } else {
                if (battleHandler.battleScene.player.getShield() != 0) {
                    dmg = battleHandler.battleScene.player.getShield() - dmg;
                    if (dmg < 0) {
                        dmg = dmg * -1;
                        battleHandler.battleScene.player.setShield(0);
                    } else {
                        battleHandler.battleScene.player.setShield(dmg);
                        dmg = 0;
                    }
                }
            }
        } else {
            if (battleHandler.battleScene.monster.getShield() != 0) {
                if (type == 3){
                    //ignore shield
                    return dmg;
                } else {
                    dmg = battleHandler.battleScene.monster.getShield() - dmg;
                    if (dmg < 0) {
                        dmg = dmg * -1;
                        battleHandler.battleScene.monster.setShield(0);
                    } else {
                        battleHandler.battleScene.monster.setShield(dmg);
                        dmg = 0;
                    }
                }
            }
        }
        return dmg;
    }

    public void applyMonsterShield(){
        monster.generateShield();
        int shieldGained = monster.getShield();
        String dialog[];

        //startDialog
        dialog = new String[]{
                monster.getName() + " cast Armor Up on himself.",
                "Gained " + + shieldGained + " Armor" + "\n" +
                        monster.getName() + specialDialog.mResponseDialog[r.nextInt(specialDialog.mResponseDialog.length)]+ "."
        };
        battleHandler.battleEventHandler.startDialog(dialog,BattleEvent.ENEMY_TURN,BattleEvent.PLAYER_TURN);
    }

    public void healMonster() {
        int healed = battleHandler.battleScene.monster.heal();
        battleHandler.battleScene.monsterHealthBar.setHP(battleHandler.battleScene.monster.getHP());
        String dialog[];
        //startDialog
        dialog = new String[]{
                monster.getName() + " cast Heal on himself.",
                "Restored " + + healed + " HP" + "\n" +
                        monster.getName() + specialDialog.mResponseDialog[r.nextInt(specialDialog.mResponseDialog.length)]  + "."
        };
        battleHandler.battleEventHandler.startDialog(dialog,BattleEvent.ENEMY_TURN,BattleEvent.PLAYER_TURN);
    }

    public void monsterTurn() {
        boolean criticalmode = battleHandler.battleScene.monster.criticalMode();
        int range;

        if (criticalmode) {
            //attack, shield, heal
            range = 3;
        } else {
            //attack, shield
            range = 2;
        }

        int turn = r.nextInt(range);
        //turn = 2;
        battleHandler.battleScene.monster.setMove(turn);
        System.out.println("Critical Mode : " + criticalmode);
        System.out.println("Move used : " + turn);

        switch (turn) {
            case 0:
                //attack
                applyPlayerDamage();//0
                break;
            case 1:
                applyMonsterShield();//1
                battleHandler.battleEventHandler.armor.play(util.sfx_vol);
                break;
            case 2:
                healMonster();
                battleHandler.battleEventHandler.heal.play(util.sfx_vol);
                break;
            case 3:
                healMonster();
                battleHandler.battleEventHandler.heal.play(util.sfx_vol);
        }
        battleHandler.battleScene.player.setAttacked(true);
    }
}
