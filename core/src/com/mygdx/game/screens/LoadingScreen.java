package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.Resource.Resource;
import com.mygdx.game.UBinHeroes;

public class LoadingScreen implements Screen {

    private final UBinHeroes app;
    private ShapeRenderer shapeRenderer;
    public OrthographicCamera camera;
    Resource res;

    private float progress;
    public LoadingScreen(final UBinHeroes app, Resource resource){
        this.app = app;
        this.camera = app.camera;
        this.shapeRenderer = new ShapeRenderer();
        res = resource;
    }

    public void update(float delta){
        progress = MathUtils.lerp(progress, app.assets.getProgress(), .1f);
        if(app.assets.update() && progress <= app.assets.getProgress() - 0.001f){
            app.setScreen(app.spashScreen);
        }
    }

    @Override
    public void show() {
        this.progress = 0f;
        queueAssets();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(app.V_WIDTH/2,app.V_HEIGHT/2,app.V_WIDTH - 64,16);

        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(app.V_WIDTH/2,app.V_HEIGHT/2,progress * (app.V_WIDTH - 64),16);
        shapeRenderer.end();

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

    }

    private void queueAssets(){
        app.assets.load("ui/uiskin.atlas", TextureAtlas.class);
    }
}
