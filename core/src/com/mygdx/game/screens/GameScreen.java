package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Entity.Card;
import com.mygdx.game.Entity.Enemy;
import com.mygdx.game.Entity.Player;
import com.mygdx.game.Event.BattleEvent;
import com.mygdx.game.Resource.Resource;
import com.mygdx.game.Resource.Utils;
import com.mygdx.game.UBinHeroes;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen implements Screen, InputProcessor {

    public final UBinHeroes app;
    public Stage stage;
    public Skin skin;
    Resource res;

    public InputMultiplexer multiplexer;
    public BattleHandler battleHandler;
    public Utils util;

    public Random randomGenerator;

    public Texture backgroundScene;

    Texture bg1;
    Texture bg2;
    Texture bg3;

    public BattleEvent currentEvent;

    public Enemy monster;
    public Enemy stoneGolem;
    public Enemy iceGolem;
    public Enemy lavaGolem;

    public Enemy orc;
    public Enemy ogre;
    public Enemy goblin;

    public Enemy troll_one;
    public Enemy troll_twp;
    public Enemy troll_three;

    public boolean bossmode = false;

    public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public ArrayList<Enemy> bosses = new ArrayList<Enemy>();

    Player player;
    public OrthographicCamera camera;
    public ShapeRenderer shapeRenderer;

    //Deck
    ArrayList<Card> deck;
    ArrayList<Texture> bg = new ArrayList<Texture>();

    public GameScreen(final UBinHeroes app,Resource resource) {
        //init Utils, Resource
        util = new Utils();
        res = resource;

        //reference to UBinHeroes class
        //gameScreen makes use of ubinheroes spritebatch, camera and stage to render game assets
        this.app = app;
        this.camera = app.camera;
        this.stage = new Stage(new FitViewport(util.V_WIDTH, util.V_HEIGHT, camera));
        this.shapeRenderer = new ShapeRenderer();
        this.shapeRenderer.setProjectionMatrix(app.camera.combined);

        //init random generator to randomize events
        randomGenerator = new Random();

        Gdx.input.setInputProcessor(stage);

        Gdx.input.setCatchBackKey(true);

        //set Background Screens
        bg1 = new Texture(Gdx.files.internal(res.scene_one));
        bg2 = new Texture(Gdx.files.internal(res.scene_two));
        bg3 = new Texture(Gdx.files.internal(res.scene_three));

        bg.add(bg1);
        bg.add(bg2);
        bg.add(bg3);

        //randomize background
        backgroundScene = bg.get(randomGenerator.nextInt(bg.size()));

        //Instantiate Monster
        generateMonster();
        generateBoss();

        //20% chance monster becomes a boss instead
        int bosschance = randomGenerator.nextInt(10);

        //randomize monster encounter Boss/Monster
        if (bosschance>7) {
            bossmode = true;
            int id = randomGenerator.nextInt(bosses.size());
            monster = bosses.get(id);
            monster.setID(id + 1);
            monster.setLevel(app.playerLvl);
            monster.setStats(app.playerLvl);
            monster.setBoss(true);
        }
        else {
            int id = randomGenerator.nextInt(enemies.size());
            monster = enemies.get(id);
            monster.setID(id + 1);
            monster.setLevel(app.playerLvl);
            monster.setStats(app.playerLvl);
            monster.setBoss(false);
        }

        //Instantiate Player
        deck = app.deck;

        player = new Player(app.playername);
        player.setLevel(app.playerLvl);


        // input multiplexer
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        battleHandler = new BattleHandler(this, player, monster);
    }

    @Override
    public void show() {
        //init Skin
        Gdx.input.setInputProcessor(multiplexer);
        skin = new Skin();
        this.skin.addRegions(app.assets.get(res.skin, TextureAtlas.class));
        this.skin.add("default-font", app.font24);
        this.skin.load(Gdx.files.internal(res.skin_json));
    }

    public void update(float delta) {
        stage.act(delta);
    }

    @Override
    public void render(float delta) {
        app.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        battleHandler.render(delta);

        //Checks if player wins
        if (currentEvent == BattleEvent.END_BATTLE && battleHandler.battleScene.player.isAlive()){
            //intent
            int prestige = (int) calculatePrestigePoints();
            app.actionResolver.setPrestige(prestige);
            app.actionResolver.androidActivity(1);
        }
        //Checks if player lose
        if (currentEvent == BattleEvent.END_BATTLE && battleHandler.battleScene.player.isAlive() == false){
            //bring to gameover screen
            app.actionResolver.setPrestige(0);
            app.actionResolver.androidActivity(0);
        }

        //Checks if player exits game
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            // Do something
            final Dialog dialog = new Dialog("Exit Game", skin, "dialog") {
                public void result(Object obj) {
                    boolean exit = (Boolean) obj;
                    if (exit) {
                        app.actionResolver.androidActivity(2);
                        Gdx.app.exit();
                    }
                }
            };
            dialog.text("Do you want to exit this game");
            dialog.button("Yes", true); //sends "true" as the result
            dialog.button("No", false);
            dialog.show(stage);

        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public SpriteBatch getBatch(){
        return app.batch;
    }


    public void generateMonster(){

            stoneGolem = new Enemy(util.monster_one);
            iceGolem = new Enemy(util.monster_two);
            lavaGolem = new Enemy (util.monster_three);

            enemies.add(stoneGolem);
            enemies.add(iceGolem);
            enemies.add(lavaGolem);

    }

    public void generateBoss(){
        orc = new Enemy(util.boss_one);
        ogre = new Enemy(util.boss_two);
        goblin = new Enemy( util.boss_three);

        bosses.add(orc);
        bosses.add(ogre);
        bosses.add(goblin);
    }


    public double calculatePrestigePoints(){
        //generate points
        double exponent = 1.5;
        double prestigeEarned = 10 * Math.pow(app.playerLvl,exponent);
        if (bossmode) {
            prestigeEarned = (30 * Math.pow(app.playerLvl,exponent));
        }
        return prestigeEarned;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            //back button handling
            final Dialog dialog = new Dialog("Exit Game", skin) {
                public void result(boolean obj) {
                    if (obj){
                        Gdx.app.exit();
                    }
                }
            };
            dialog.text("Do you want to exit this game");
            dialog.button("Yes", true); //sends "true" as the result
            dialog.button("No", false);
            dialog.show(stage);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}