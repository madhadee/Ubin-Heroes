package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Entity.Card;
import com.mygdx.game.Resource.Resource;
import com.mygdx.game.Resource.Utils;
import com.mygdx.game.screens.ApplicationIntent;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.LoadingScreen;
import com.mygdx.game.screens.SpashScreen;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.ArrayList;

public class UBinHeroes extends Game implements ApplicationListener {
    public static final int V_WIDTH = 480;
    public static final int V_HEIGHT = 720;

    Resource res;
    Utils util;

    public OrthographicCamera camera;
    public FitViewport viewport;

    public SpriteBatch batch;
    public BitmapFont font24;
    public AssetManager assets;
    public LoadingScreen loadingScreen;
    public SpashScreen spashScreen;
    public GameScreen gameScreen;
    public ApplicationIntent actionResolver;

    public int playerLvl = 0;
    public String playername = "player";
    public ArrayList<Card> deck = new ArrayList<Card>();
    public ArrayList<Integer> deckID = new ArrayList<Integer>();

    public UBinHeroes(){

    }

    public UBinHeroes(ApplicationIntent actionResolver, int playerLvl, ArrayList<Integer> deckID, String playername){
        super();
        res = new Resource();
        util = new Utils();
        this.actionResolver = actionResolver;//intent to bring back to app
        this.playerLvl = playerLvl;//get players lvl
        this.deckID = deckID;
        this.playername = playername;
    }

    @Override
    public void create() {
        Gdx.input.setCatchBackKey(true);

        //init assetManager, camera, viewport
        assets = new AssetManager();
        camera = new OrthographicCamera();
        viewport = new FitViewport(V_WIDTH,V_HEIGHT,camera);
        viewport.apply();
        camera.position.set(V_WIDTH/2,V_HEIGHT/2,0);
        camera.update();

        //init SpriteBatch to render assets
        batch = new SpriteBatch();
        font24 = new BitmapFont();

        //init font
        initFonts();

        //generate selected cards
        this.deck = generateDeck(deckID);


        //init screens
        loadingScreen = new LoadingScreen(this,res);
        spashScreen = new SpashScreen(this,res);
        gameScreen = new GameScreen(this,res);

        this.setScreen(loadingScreen);
    }

    @Override
    public void render() {
        super.render();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
        batch.dispose();
        font24.dispose();
        assets.dispose();
        loadingScreen.dispose();
        spashScreen.dispose();
        gameScreen.dispose();
    }

    //Game Fonts
    private void initFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(res.font));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = 20;
        params.color = Color.BLACK;
        font24 = generator.generateFont(params);
    }

    //load card assets
    private ArrayList<Card> generateDeck(ArrayList<Integer> deckID){
        Texture imgTexture;
        Image cardimg = new Image();
        Card card;
        ArrayList<Card> deck = new ArrayList<Card>();
        for (int i = 0; i < deckID.size(); i++){
            switch(deckID.get(i)){
                case 1://Plastic Bow
                    imgTexture = new Texture(Gdx.files.internal(res.plastic_weapon_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(1,util.plastic_weapon, util.plastic_weapon, 2, 3,0,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;

                case 2://Plastic Shield
                    imgTexture = new Texture(Gdx.files.internal(res.plastic_shield_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(2,util.plastic_shield, util.plastic_shield, 2, 2,2,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;

                case 3://Plastic Spell
                    imgTexture = new Texture(Gdx.files.internal(res.plastic_spell_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(3,util.plastic_spell, util.plastic_spell, 2, 3,0,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;

                case 4://Metal Sword
                    imgTexture = new Texture(Gdx.files.internal(res.metal_weapon_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(4,util.metal_weapon, util.metal_weapon, 1, 4,0,1, cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;

                case 5://Metal Shield
                    imgTexture = new Texture(Gdx.files.internal(res.metal_shield_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(5,util.metal_shield, util.metal_shield, 1, 2,3,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;

                case 6://Metal Spell
                    imgTexture = new Texture(Gdx.files.internal(res.metal_spell_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(6,util.metal_spell, util.metal_spell, 1, 2,0,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;

                case 7://Paper Sphere
                    imgTexture = new Texture(Gdx.files.internal(res.paper_weapon_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(7,util.paper_weapon, util.paper_weapon, 3, 2,0,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;

                case 8://Paper Shield
                    imgTexture = new Texture(Gdx.files.internal(res.paper_shield_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(8,util.paper_shield, util.paper_shield, 3, 1,1,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;

                case 9://Paper Spell
                    imgTexture = new Texture(Gdx.files.internal(res.paper_spell_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(9,util.paper_spell, util.paper_spell, 3, 3,0,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;
                case 10:
                    imgTexture = new Texture(Gdx.files.internal(res.basic_sword_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(10,util.basic_sword, util.basic_sword, 1, 1,0,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;

                case 11:
                    imgTexture = new Texture(Gdx.files.internal(res.basic_bow_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(11,util.basic_bow, util.basic_bow, 2, 1,0,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;

                case 12:
                    imgTexture = new Texture(Gdx.files.internal(res.basic_spear_image));
                    cardimg.setDrawable(new TextureRegionDrawable(new TextureRegion(imgTexture)));
                    card = new Card(12,util.basic_spear, util.basic_spear, 3, 1,0,1,cardimg);
                    card.setCardTexuture(imgTexture);
                    deck.add(card);
                    break;
            }
        }
        return deck;
    }
}
