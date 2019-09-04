package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.Entity.Enemy;
import com.mygdx.game.Entity.Player;
import com.mygdx.game.Event.Battle;
import com.mygdx.game.Event.BattleEvent;
import com.mygdx.game.Event.HealthBar;
import com.mygdx.game.Resource.Resource;
import com.mygdx.game.Resource.Utils;

public class BattleScene {

    private GameScreen gameScreen;
    public Player player;
    public Battle battle;
    private BattleHandler battleHandler;
    private Utils util;
    private Resource res;

    //stage
    private Stage stage;
    public Enemy monster;

    //monster Texture Atlas
    private TextureAtlas monster_idle_texture;
    private TextureAtlas monster_attack_texture;
    private TextureAtlas monster_hurt_texture;
    private TextureAtlas player_attack_texture;
    private TextureAtlas player_shield_texture;
    private TextureAtlas heal_texture;

    public HealthBar monsterHealthBar;
    public HealthBar playerHealthBar;

    //monster battleAnimation
    private Animation<TextureRegion> monster_attack_animation;
    private Animation<TextureRegion> monster_animation;
    private Animation<TextureRegion> monster_hurt_animation;
    private Animation<TextureRegion> playerAttackAnimation;
    private Animation<TextureRegion> playerShieldAnimation;
    private Animation<TextureRegion> healAnimation;

    private float elapsedTime = 0f;
    private float animationTime = 0f;

    public BattleScene(GameScreen gameScreen, Player player, BattleHandler battleHandler, Enemy monster){
        this.gameScreen = gameScreen;
        this.battleHandler = battleHandler;
        this.stage = this.gameScreen.stage;
        util = new Utils();
        res = new Resource();
        this.player = player;
        this.monster = monster;

        //generate health bar
        monsterHealthBar = new HealthBar(this.stage,gameScreen.shapeRenderer,monster.getMaxHP(), util.V_WIDTH / 2 - 100, util.V_HEIGHT / 2 + 100);
        playerHealthBar = new HealthBar(this.stage,gameScreen.shapeRenderer,player.getMaxHP(), util.V_WIDTH / 2 -100, util.V_HEIGHT / 2 -230);

        //if monster is boss
        if (monster.getBoss()){
            loadBossAssets(monster.getID());
        }
        else {
            loadMonsterAssets(monster.getID());
        }

        //init effects animation
        player_attack_texture = new TextureAtlas(Gdx.files.internal(res.attack_effect));
        playerAttackAnimation = new Animation(1f/15f, player_attack_texture.getRegions());

        player_shield_texture = new TextureAtlas(Gdx.files.internal(res.armor_effect));
        playerShieldAnimation = new Animation(1f/15f, player_shield_texture.getRegions());

        heal_texture = new TextureAtlas(Gdx.files.internal(res.heal_effect));
        healAnimation = new Animation(1f/15f, heal_texture.getRegions());

        //set default animation -- IDLE
        monster.setAnimation(monster_animation);
        monster.getAnimation().setPlayMode(Animation.PlayMode.LOOP);

        //add actors to stage
        stage.addActor(monster);
        stage.addActor(monsterHealthBar);
        stage.addActor(playerHealthBar);

        battle = new Battle(this.gameScreen,player);
    }

    public void update(float dt) {

    }

    public void render(float dt) {
        elapsedTime += dt;

        gameScreen.app.batch.begin();
        gameScreen.app.batch.draw(gameScreen.backgroundScene, 0, 0, util.V_WIDTH, util.V_HEIGHT);
        gameScreen.shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        gameScreen.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //monster idle animation
        gameScreen.app.font24.draw(gameScreen.getBatch(), monster.getName(), util.V_WIDTH / 2 - 100, util.V_HEIGHT / 2 + 140);
        gameScreen.app.font24.draw(gameScreen.getBatch(), "Lv " + (int) monster.getLevel(), util.V_WIDTH / 2 + 70, util.V_HEIGHT / 2 + 140);
        gameScreen.app.font24.draw(gameScreen.getBatch(), "HP: " + (int) monster.getHP() + "/" + (int) monster.getMaxHP(), util.V_WIDTH / 2, util.V_HEIGHT / 2 + 90);
        gameScreen.app.font24.draw(gameScreen.getBatch(), "Shield: " + monster.getShield(), util.V_WIDTH / 2 -100, util.V_HEIGHT / 2 + 90);

        gameScreen.app.font24.draw(gameScreen.getBatch(), player.getPlayer_name(), util.V_WIDTH / 2 - 100, util.V_HEIGHT / 2 - 190);
        gameScreen.app.font24.draw(gameScreen.getBatch(), "Lv " + (int) player.getLevel(), util.V_WIDTH / 2 + 70, util.V_HEIGHT / 2 -190);
        gameScreen.app.font24.draw(gameScreen.getBatch(), "HP: " + (int) player.getHP() + "/" + (int) player.getMaxHP(), util.V_WIDTH / 2, util.V_HEIGHT / 2 -240);
        gameScreen.app.font24.draw(gameScreen.getBatch(), "Shield: " + player.getShield(), util.V_WIDTH / 2 -100, util.V_HEIGHT / 2 -240);

        if (player.isAttacked() && battleHandler.battleEventHandler.prevEvent == BattleEvent.ENEMY_TURN) {
            //attack animation
            animationTime +=dt;

            if (monster.getMove() == 0) {//attack
                gameScreen.getBatch().draw(playerAttackAnimation.getKeyFrame(animationTime,false),util.V_WIDTH / 2 - 150, util.V_HEIGHT / 2 - 300,300,300);
                monster.setAnimation(monster_attack_animation);
                monster.getAnimation().setPlayMode(Animation.PlayMode.LOOP);
            }

            if (monster.getMove() == 1) {//shield
                gameScreen.getBatch().draw(playerShieldAnimation.getKeyFrame(animationTime,false),util.V_WIDTH / 2 - 310, util.V_HEIGHT / 2 - 20,600,600);

            }

            if (monster.getMove() == 2) {//heal
                gameScreen.getBatch().draw(healAnimation.getKeyFrame(animationTime,false),util.V_WIDTH / 2 - 150, util.V_HEIGHT / 2 + 100,300,300);
            }

            if (animationTime > 1) {
                monster.setAnimation(monster_animation);
                monster.getAnimation().setPlayMode(Animation.PlayMode.LOOP);
                animationTime = 0;
                player.setAttacked(false);
            }
        }

        if(monster.isAttacked() && battleHandler.battleEventHandler.prevEvent == BattleEvent.PLAYER_TURN){
            //draw attack animation
            animationTime += dt;
            gameScreen.getBatch().draw(playerAttackAnimation.getKeyFrame(animationTime,false),util.V_WIDTH / 2 - 150, util.V_HEIGHT / 2 + 100,300,300);

            monster.setShowTime(animationTime);
            monster.setAnimation(monster_hurt_animation);
            monster.getAnimation().setPlayMode(Animation.PlayMode.NORMAL);

            if(animationTime > 0.4){
                monster.setAnimation(monster_animation);
                monster.getAnimation().setPlayMode(Animation.PlayMode.LOOP);
                monster.setAttacked(false);
                animationTime = 0;
            }
        }

        gameScreen.shapeRenderer.end();
        gameScreen.getBatch().end();
        stage.draw();
        stage.act();
        monsterHealthBar.render(dt);
        playerHealthBar.render(dt);
    }

    public void checkMove(int id) {//plays sfx based on card selected
        switch(id) {
            case 1://plastic bow
                battleHandler.battleEventHandler.bow.play(util.sfx_vol);
                break;
            case 2://plastic shield
                battleHandler.battleEventHandler.shield.play(util.sfx_vol);
                break;
            case 3://plastic spell
                battleHandler.battleEventHandler.spell.play(util.sfx_vol);
                break;
            case 4://metal sword
                battleHandler.battleEventHandler.sword.play(util.sfx_vol);
                break;
            case 5://metal shield
                battleHandler.battleEventHandler.shield.play(util.sfx_vol);
                break;
            case 6://metal spell
                battleHandler.battleEventHandler.spell.play(util.sfx_vol);
                break;
            case 7://paper spear
                battleHandler.battleEventHandler.spear.play(util.sfx_vol);
                break;
            case 8://paper shield
                battleHandler.battleEventHandler.shield.play(util.sfx_vol);
                break;
            case 9://paper spell
                battleHandler.battleEventHandler.spell.play(util.sfx_vol);
                break;
            case 10://basic sword
                battleHandler.battleEventHandler.sword.play(util.sfx_vol);
                break;
            case 11://basic bow
                battleHandler.battleEventHandler.bow.play(util.sfx_vol);
                break;
            case 12://basic spear
                battleHandler.battleEventHandler.spear.play(util.sfx_vol);
                break;
        }
    }

    public void loadMonsterAssets(int id) {
        switch(id) {
            case 1:
                //stone golem
                monster_idle_texture = new TextureAtlas(Gdx.files.internal(res.earth_golem_idle));
                monster_attack_texture = new TextureAtlas(Gdx.files.internal(res.earth_golem_attack));
                monster_hurt_texture = new TextureAtlas(Gdx.files.internal(res.earth_golem_hurt));

                monster_animation = new Animation(1f/15f, monster_idle_texture.getRegions());
                monster_attack_animation = new Animation(1f/15f, monster_attack_texture.getRegions());
                monster_hurt_animation = new Animation(1f/30f, monster_hurt_texture.getRegions());
                break;
            case 2:
                monster_idle_texture = new TextureAtlas(Gdx.files.internal(res.ice_golem_idle));
                monster_attack_texture = new TextureAtlas(Gdx.files.internal(res.ice_golem_attack));
                monster_hurt_texture = new TextureAtlas(Gdx.files.internal(res.ice_golem_hurt));

                monster_animation = new Animation(1f/15f, monster_idle_texture.getRegions());
                monster_attack_animation = new Animation(1f/15f, monster_attack_texture.getRegions());
                monster_hurt_animation = new Animation(1f/30f, monster_hurt_texture.getRegions());
                break;
            case 3:
                monster_idle_texture = new TextureAtlas(Gdx.files.internal(res.lava_golem_idle));
                monster_attack_texture = new TextureAtlas(Gdx.files.internal(res.lava_golem_attack));
                monster_hurt_texture = new TextureAtlas(Gdx.files.internal(res.lava_golem_hurt));

                monster_animation = new Animation(1f/15f, monster_idle_texture.getRegions());
                monster_attack_animation = new Animation(1f/15f, monster_attack_texture.getRegions());
                monster_hurt_animation = new Animation(1f/30f, monster_hurt_texture.getRegions());
                break;

        }
    }

    public void loadBossAssets(int id) {
        switch (id) {
            case 1:
                //stone golem
                monster_idle_texture = new TextureAtlas(Gdx.files.internal(res.poly_king_orc_idle));
                monster_attack_texture = new TextureAtlas(Gdx.files.internal(res.poly_king_orc_attack));
                monster_hurt_texture = new TextureAtlas(Gdx.files.internal(res.poly_king_orc_hurt));

                monster_animation = new Animation(1f/15f, monster_idle_texture.getRegions());
                monster_attack_animation = new Animation(1f/15f, monster_attack_texture.getRegions());
                monster_hurt_animation = new Animation(1f/30f, monster_hurt_texture.getRegions());
                break;
            case 2:
                monster_idle_texture = new TextureAtlas(Gdx.files.internal(res.steel_king_ogre_idle));
                monster_attack_texture = new TextureAtlas(Gdx.files.internal(res.steel_king_ogre_attack));
                monster_hurt_texture = new TextureAtlas(Gdx.files.internal(res.steel_king_ogre_hurt));

                monster_animation = new Animation(1f/15f, monster_idle_texture.getRegions());
                monster_attack_animation = new Animation(1f/15f, monster_attack_texture.getRegions());
                monster_hurt_animation = new Animation(1f/30f, monster_hurt_texture.getRegions());
                break;
            case 3:
                monster_idle_texture = new TextureAtlas(Gdx.files.internal(res.leather_king_goblin_idle));
                monster_attack_texture = new TextureAtlas(Gdx.files.internal(res.leather_king_goblin_attack));
                monster_hurt_texture = new TextureAtlas(Gdx.files.internal(res.leather_king_goblin_hurt));

                monster_animation = new Animation(1f/15f, monster_idle_texture.getRegions());
                monster_attack_animation = new Animation(1f/15f, monster_attack_texture.getRegions());
                monster_hurt_animation = new Animation(1f/30f, monster_hurt_texture.getRegions());
                break;
        }
    }
}
