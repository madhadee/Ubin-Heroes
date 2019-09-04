package com.mygdx.game.activities;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.mygdx.game.R;

/**
 * This activity is responsible for displaying the onBoarding steps at very first install or after all data is cleared
 *
 */
public class SlidersActivity extends AppIntro {
    /**
     *SlidersActivity extends the Library AppIntro that creates the fragments on first launch
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // adding new fragments to the slider
        addSlide(AppIntroFragment.newInstance(pageOne()));
        addSlide(AppIntroFragment.newInstance(pageTwo()));
        addSlide(AppIntroFragment.newInstance(pageThree()));
        addSlide(AppIntroFragment.newInstance(pageFour()));
        addSlide(AppIntroFragment.newInstance(pageFive()));
        addSlide(AppIntroFragment.newInstance(pageSix()));
        askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},  6);
        setSwipeLock(false);
        showSkipButton(false);
        setColorTransitionsEnabled(true);
    }

    /** First page
     * Each page is defined and customized here, by setting params of the library, the library takes care of the rest.
     * @return returns screen for page one
     */
    private SliderPage pageOne(){
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Welcome");
        sliderPage.setDescription("Hi there! Welcome to Ubin Heroes!");
        sliderPage.setImageDrawable(R.drawable.ic_login_logo);
        sliderPage.setBgColor(getResources().getColor(R.color.yellowText));

        return sliderPage;
    }

    /** Second page
     * Each page is defined and customized here, by setting params of the library, the library takes care of the rest.
     * @return returns screen for page two
     */
    private SliderPage pageTwo(){
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Scan Recyclable Items");
        sliderPage.setDescription("Pick up recyclable items and scan them to store them in your inventory.");
        sliderPage.setImageDrawable(R.drawable.bg_item);
        sliderPage.setBgColor(getResources().getColor(R.color.colorPrimaryDark));

        return sliderPage;
    }

    /** Third page
     * Each page is defined and customized here, by setting params of the library, the library takes care of the rest.
     * @return returns screen for page third
     */
    private SliderPage pageThree(){
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Scan QR Code");
        sliderPage.setDescription("Throw the items into recycling bins and scan the QR code to convert the items to materials.");
        sliderPage.setImageDrawable(R.drawable.bg_bin);
        sliderPage.setBgColor(getResources().getColor(R.color.blueText));

        return sliderPage;
    }

    /** Fourth page
     * Each page is defined and customized here, by setting params of the library, the library takes care of the rest.
     * @return returns screen for page four
     */
    private SliderPage pageFour(){
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Craft Cards");
        sliderPage.setDescription("Use the materials earned to craft cards.");
        sliderPage.setImageDrawable(R.drawable.card_metal_weapon);
        sliderPage.setBgColor(getResources().getColor(R.color.redText));

        return sliderPage;
    }

    /** Fifth page
     * Each page is defined and customized here, by setting params of the library, the library takes care of the rest.
     * @return returns screen for page five
     */
    private SliderPage pageFive(){
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Battle");
        sliderPage.setDescription("Bring the cards to battle to defeat monsters!");
        sliderPage.setImageDrawable(R.drawable.ic_battle);
        sliderPage.setBgColor(getResources().getColor(R.color.colorAccentDark));

        return sliderPage;
    }

    /** Sixth page
     * Each page is defined and customized here, by setting params of the library, the library takes care of the rest.
     * @return returns screen for page six
     */
    private SliderPage pageSix(){
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Be A True Ubin Hero");
        sliderPage.setDescription("Compete with friends to see who can recycle and defeat the most monsters and claim the title of Ubin Hero");
        sliderPage.setImageDrawable(R.drawable.ic_white_logo);
        sliderPage.setBgColor(getResources().getColor(R.color.colorPrimaryDark));

        return sliderPage;
    }

    /**
     * After the user has swiped to the lats page, launch the loginActivity class
     * @param currentFragment
     */
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
