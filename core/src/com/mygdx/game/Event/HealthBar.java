package com.mygdx.game.Event;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class HealthBar extends Actor {

    private Stage stage;
    private ShapeRenderer shapeRenderer;

    private float healthbarWidth = 200;
    private float healthbarHeight = 20;

    private float xPos;
    private float yPos;

    private float maxHP;
    private float HP;

    private Color damageColor = new Color(1, 0, 0, 1);

    public HealthBar(Stage stage, ShapeRenderer shapeRenderer, float maxHP, float x, float y){
        this.stage = stage;
        this.maxHP = maxHP;
        this.HP = maxHP;
        this.xPos = x;
        this.yPos = y;
        this.shapeRenderer = shapeRenderer;
    }

    public void update(float dt){

    }

    public void render(float dt){
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // create black bg
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(xPos, yPos, healthbarWidth, healthbarHeight);

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(xPos, yPos + (healthbarHeight / 2), healthbarWidth, healthbarHeight / 2);

        // bottom rect
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(xPos, yPos, healthbarWidth, healthbarHeight / 2);

        // render hp animation
        // entity damaged
        shapeRenderer.setColor(damageColor);
        shapeRenderer.rect(xPos, yPos, healthbarWidth * HP/maxHP, healthbarHeight);

        shapeRenderer.end();
    }

    public void setHP(float hp){
        this.HP = hp;
    }
}
