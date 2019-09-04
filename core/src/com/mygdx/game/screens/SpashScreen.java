package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Resource.Resource;
import com.mygdx.game.UBinHeroes;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class SpashScreen implements Screen {
    private final UBinHeroes app;
    private Stage stage;
    private Image leftDoorImg, rightDoorImg, fightTitleImg, backgroundImg;
    public OrthographicCamera camera;
    Texture leftDoor, rightDoor, fightTitle, background;
    public Music splashSound;
    Resource res;

    public SpashScreen(final UBinHeroes app,Resource resource){
        this.app = app;
        this.camera = app.camera;
        this.stage = new Stage(new FitViewport(app.V_WIDTH,app.V_HEIGHT,app.camera));
        res = resource;

        //play music
        splashSound = Gdx.audio.newMusic(Gdx.files.internal(res.splash_sound));
        splashSound.setVolume(0.1f);
        splashSound.setLooping(true);
        splashSound.play();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        //Splash Screen animations
        Runnable transitionRunnable =  new Runnable() {
            @Override
            public void run() {
                app.setScreen(app.gameScreen);
            }
        };

        background =  new Texture(Gdx.files.internal(res.castle));
        backgroundImg = new Image(background);
        backgroundImg.setFillParent(true);

        leftDoor = new Texture(Gdx.files.internal(res.doorLeft));
        leftDoorImg = new Image(leftDoor);
        leftDoorImg.setX(-200);
        leftDoorImg.setY(stage.getHeight()/4);
        leftDoorImg.setSize(stage.getWidth()/2, stage.getHeight()/2);

        rightDoor = new Texture(Gdx.files.internal(res.doorRight));
        rightDoorImg = new Image(rightDoor);
        rightDoorImg.setX(stage.getWidth() + 200);
        rightDoorImg.setY(stage.getHeight()/4);
        rightDoorImg.setSize(stage.getWidth()/2, stage.getHeight()/2);

        fightTitle = new Texture(Gdx.files.internal(res.fight));
        fightTitleImg = new Image(fightTitle);
        fightTitleImg.setX(0);
        fightTitleImg.setY(0);
        fightTitleImg.setSize(stage.getWidth(), stage.getHeight());

        leftDoorImg.addAction(sequence(alpha(1),
                moveTo(0, stage.getHeight()/4, 0.5f),
                delay(1.5f),fadeOut(0.75f), run(transitionRunnable)));

        rightDoorImg.addAction(sequence(alpha(1),
                moveTo(stage.getWidth()/2, stage.getHeight()/4, 0.5f),
                delay(1.5f),fadeOut(0.75f), run(transitionRunnable)));

        fightTitleImg.addAction(sequence(alpha(1),
                delay(1.5f),fadeOut(1), run(transitionRunnable)));

        stage.addActor(backgroundImg);
        stage.addActor(leftDoorImg);
        stage.addActor(rightDoorImg);
        stage.addActor(fightTitleImg);
    }

    public void update(float delta) {
        stage.act(delta);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        stage.draw();
        app.batch.begin();
        app.batch.end();
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
        splashSound.dispose();
        stage.dispose();
    }
}
