package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.mygdx.game.Entity.Card;
import com.mygdx.game.screens.BattleHandler;
import com.mygdx.game.screens.GameScreen;

public class MyActor extends Actor {
    Sprite sprite;
    public com.mygdx.game.Entity.Card card;
    public float xPos;
    public float yPos;

    public MyActor(Texture texture, float width, float height, final Card card, final float xPos, final float yPos){
        this.card = card;
        this.xPos = xPos;
        this.yPos = yPos;

        sprite = new Sprite(texture);
        sprite.setSize(width,height);
        setBounds(sprite.getX(),sprite.getY(),sprite.getWidth(),sprite.getHeight());
        setTouchable(Touchable.enabled);
    }

    @Override
    protected void positionChanged() {
        sprite.setPosition(getX(),getY());
        super.positionChanged();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}