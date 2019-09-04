package com.mygdx.game;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.activities.DefeatActivity;
import com.mygdx.game.activities.MainActivity;
import com.mygdx.game.activities.VictoryActivity;
import com.mygdx.game.screens.ApplicationIntent;

import java.util.ArrayList;

public class AndroidLauncher extends AndroidApplication implements ApplicationIntent {
    private int prestige;
    private int prestigeLvl;
    private String musername;
    private ArrayList<Integer> deck = new ArrayList<>();

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        musername = getIntent().getStringExtra("player");
        prestigeLvl = getIntent().getIntExtra("prestigeLvl",1);
		deck = getIntent().getIntegerArrayListExtra("Deck");

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		UBinHeroes uBinHeroes = new UBinHeroes(this,prestigeLvl,deck,musername);
		initialize(uBinHeroes, config);
	}

	@Override
	public void androidActivity(int win) {
		if (win == 1){
			Intent intent;
			intent = new Intent(getApplicationContext(), VictoryActivity.class);
			intent.putExtra("prestigeGained", prestige);
			intent.putExtra("Deck",deck);
            this.startActivity(intent);
		} else if (win == 0) {
			Intent intent;
			intent = new Intent(getApplicationContext(), DefeatActivity.class);
			intent.putExtra("prestigeGained", prestige);
			intent.putExtra("Deck",deck);
            this.startActivity(intent);
		} else {
            Intent intent;
            intent = new Intent(getApplicationContext(), MainActivity.class);
            this.startActivity(intent);
        }
	}

	@Override
	public void setPrestige(int prestige) {
        this.prestige = prestige;
	}

	@Override
	public double getPrestige() {
		return prestige;
	}
}
