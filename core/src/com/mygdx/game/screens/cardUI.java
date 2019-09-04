package com.mygdx.game.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.mygdx.game.Entity.Card;
import com.mygdx.game.MyActor;
import com.mygdx.game.Resource.Utils;

import java.util.ArrayList;

public class  cardUI {

    private Stage stage;

    //init
    ArrayList<Card> playerMoves;
    ArrayList<Card> playerDeck;
    ArrayList<MyActor> cardActor;

    MyActor actor1;
    MyActor actor2;
    MyActor actor3;

    private GameScreen gameScreen;
    Utils util;
    boolean PAN = false;
    boolean myturn = false;

    public cardUI(GameScreen gameScreen){
        this.gameScreen = gameScreen;
        this.stage = gameScreen.stage;

        util = new Utils();
        playerMoves = new ArrayList<Card>();
        playerDeck = gameScreen.deck;
    }

    public void render(float dt) {}

    public void createMoveUI() {
        // Create card actors
        playerMoves = new ArrayList<Card>();
        cardActor = new ArrayList<MyActor>();

        //randomizes players cards
        for (int i = 0; i < 3; i++) {//retrieve random card arraylist position
            int index = gameScreen.randomGenerator.nextInt(playerDeck.size());
            playerMoves.add(playerDeck.get(index));
        }

        if (actor1 != null && actor2 != null && actor3 != null) {
            actor1.remove();
            actor2.remove();
            actor3.remove();
        }

        //set randomized cards to put into stage as actors
        actor1 = new MyActor(playerMoves.get(0).getCardTexuture(), util.cardWidth, util.cardHeight , playerMoves.get(0),util.V_WIDTH / 2 - 180,util.V_HEIGHT / 2 - 150);
        actor1.setPosition(util.V_WIDTH / 2 - 180, util.V_HEIGHT / 2 - 150);

        actor2 = new MyActor(playerMoves.get(1).getCardTexuture(), util.cardWidth, util.cardHeight , playerMoves.get(1),util.V_WIDTH / 2 - 50,util.V_HEIGHT / 2 - 150);
        actor2.setPosition(util.V_WIDTH / 2 - 50, util.V_HEIGHT / 2 - 150);

        actor3 = new MyActor(playerMoves.get(2).getCardTexuture(), util.cardWidth, util.cardHeight , playerMoves.get(2),util.V_WIDTH / 2 + 80,util.V_HEIGHT / 2 - 150);
        actor3.setPosition(util.V_WIDTH / 2 + 80, util.V_HEIGHT / 2 - 150);

        //store card actor into arrayList
        cardActor.add(actor1);
        cardActor.add(actor2);
        cardActor.add(actor3);

        //set cards onto stage
        for(int i =0;i<cardActor.size();i++){
            this.gameScreen.stage.addActor(cardActor.get(i));
        }
        handleMoveEvents();
    }

    //handle card selected events
    private void handleMoveEvents() {
        PAN = true;
        for (int i = 0; i < cardActor.size(); i++) {
            final int index = i;

            cardActor.get(i).addListener(new ActorGestureListener(){
                public boolean longPress(Actor actor, float x, float y) {
                    final Dialog dialog = new Dialog("Card Details", gameScreen.skin, "dialog") {
                        public void result(Object obj) {
                            System.out.println("result " + obj);
                        }
                    };

                    dialog.text("Name " + cardActor.get(index).card.getName()
                            + "\n" + "Description : " + cardActor.get(index).card.getDesc()
                            + "\n" + "Damage : " + cardActor.get(index).card.getDmg()
                            + "\n" + "Card Level : " + cardActor.get(index).card.getLvl()
                            + "\n" + "Type : " + cardActor.get(index).card.getType());
                    dialog.button("Close", true); //sends "true" as the result

                    dialog.show(gameScreen.stage);

                    PAN = true;
                    return true;
                }

                public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                    event.getListenerActor().moveBy(deltaX, deltaY);
                    if (event.getListenerActor().getY() > 350 ) {
                        event.getListenerActor().moveBy(deltaX, deltaY);
                        PAN = false;
                    } else {
                        PAN = true;
                    }
            }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (!PAN && myturn) {
                        event.getListenerActor().setPosition(cardActor.get(index).xPos, cardActor.get(index).yPos);
                        String cardName = cardActor.get(index).card.getName();
                        int cardType = cardActor.get(index).card.getType();
                        int dmg = cardActor.get(index).card.getDmg();
                        int id = cardActor.get(index).card.getCardID();
                        int shield = cardActor.get(index).card.getShield();
                        gameScreen.battleHandler.battleScene.battle.playerAttack(cardName, cardType, dmg, id, shield);
                        PAN = true;
                        myturn = false;
                    } else {
                        event.getListenerActor().setPosition(cardActor.get(index).xPos, cardActor.get(index).yPos);
                    }
                }
            });
        }
    }
}


