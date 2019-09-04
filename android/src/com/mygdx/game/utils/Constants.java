package com.mygdx.game.utils;

import android.util.SparseIntArray;
import android.view.Surface;

import com.mygdx.game.BuildConfig;

/**
 * Storage for all constants
 */

public final class Constants {
    //Tags
    public static final String TAG_BASE_ACTIVITY = "TAG_BASE_ACTIVITY";
    public static final String TAG_SPLASH_ACTIVITY = "TAG_SPLASH_ACTIVITY";
    public static final String TAG_LOGIN_ACTIVITY = "TAG_LOGIN_ACTIVITY";
    public static final String TAG_USERNAME_ACTIVITY = "TAG_USERNAME_ACTIVITY";
    public static final String TAG_MAIN_ACTIVITY = "TAG_MAIN_ACTIVITY";
    public static final String TAG_PREP_ACTIVITY = "TAG_PREP_ACTIVITY";
    public static final String TAG_RANKING_ACTIVITY = "TAG_RANKING_ACTIVITY";
    public static final String TAG_SCAN_OPTIONS_ACTIVITY = "TAG_SCAN_OPTIONS_ACTIVITY";
    public static final String TAG_SCAN_ITEMS_ACTIVITY = "TAG_SCAN_ITEMS_ACTIVITY";
    public static final String TAG_SCAN_ITEMS_RESULT_ACTIVITY = "TAG_SCAN_ITEMS_RESULT_ACTIVITY";
    public static final String TAG_SCAN_BIN_ACTIVITY = "TAG_SCAN_BIN_ACTIVITY";
    public static final String TAG_SCAN_BIN_RESULT_ACTIVITY = "TAG_SCAN_BIN_RESULT_ACTIVITY";
    public static final String TAG_INVENTORY_ACTIVITY = "TAG_INVENTORY_ACTIVITY";
    public static final String TAG_PROFILE_ACTIVITY = "TAG_PROFILE_ACTIVITY";

    //Firebase Database Path Reference
    public static final String DB_REF_ROOT = "https://ubin-heroes.firebaseio.com/";
    public static final String DB_REF_USERS = "users";
    public static final String DB_REF_INVENTORY = "inventory";
    public static final String DB_REF_DISTRICT = "district";
    public static final String DB_REF_DISTRICT_RANK = "districtRank";
    public static final String DB_REF_PRESTIGE_LVL = "prestigeLvl";
    public static final String DB_REF_PRESTIGE_POINTS = "prestigePoints";
    public static final String DB_REF_PRESTIGE_TITLE = "prestigeTitle";
    public static final String DB_REF_PROFILE_PIC = "profilePicUrl";
    public static final String DB_REF_RECYCLE_COUNT = "recyclingCount";
    public static final String DB_REF_USERNAME = "username";
    public static final String DB_REF_WIN_COUNT = "winCount";

    public static final String DB_REF_CARD_OBJECT = "cardObject";
    public static final String DB_REF_CRAFTING_OBJECT = "craftingObject";
    public static final String DB_REF_RECYCLABLE_OBJECT = "recyclableObject";
    public static final String DB_REF_EQUIPMENT = "equipment";
    public static final String DB_REF_METAL_SHIELD = "metalShield";
    public static final String DB_REF_PAPER_KNIFE = "paperKnife";
    public static final String DB_REF_PLASTIC_SWORD = "plasticSword";
    public static final String DB_REF_RECYCLABLE_ITEMS = "recyclableItems";
    public static final String DB_REF_METAL = "metal";
    public static final String DB_REF_PAPER = "paper";
    public static final String DB_REF_PLASTIC = "plastic";
    public static final String DB_REF_USEABLE_MATERIALS = "useableMaterials";
    public static final String DB_REF_USEABLE_LIST = "useableList";
    public static final String DB_REF_METAL_MATERIALS = "metalMaterial";
    public static final String DB_REF_PAPER_MATERIALS = "paperMaterial";
    public static final String DB_REF_PLASTIC_MATERIALS = "plasticMaterial";
    public static final String DB_REF_QR_BINS = "qrCode";
    public static final String DB_REF_SCAN_COUNT = "scanCount";

    //Splashscreen duration
    public static int SPLASH_TIME_OUT = 3000;

    //SplashActivity
    public static final String FIRST_LOGIN = "firstTime";
    public static final String SHARED_PREF_PATH = "com.mygdx.game.SplashActivity";

    //Request Code used to invoke sign in user interactions
    public static final int RC_SIGN_IN = 2105;

    //Google Cloud Platform API Key for Vision
    public static final String CLOUD_VISION_API_KEY = BuildConfig.API_KEY;

    //ScanItemsActivity
    public static final String FILE_NAME = "temp.jpg";
    public static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    public static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    public static final int MAX_LABEL_RESULTS = 10;
    public static final int MAX_DIMENSION = 1200;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final String WORKER_TAG = "TimerJob1";

    //ScanBinActivity
    public static final int PERMISSION_ALL = 1;

    //ScanBinResultActivity
    public static final String SCAN_ERROR = "You are not within the proximity of a recycling bin. Please try again!";
    public static final String SCAN_LONGITUDE = "longitude";
    public static final String SCAN_LATITUDE = "latitude";
    public static final String SCAN_QUANTITY = "x ";

    //MapActivity
    public static final int REQUEST_USER_LOCATION_CODE = 99;

    //InventoryActivity
    public static final String ITEMS_TAB = "Items";
    public static final String CRAFT_TAB = "Craft";

    //ProfileActivity
    public static final String LOGOUT_TITLE = "Logout";
    public static final String LOGOUT_MESSAGE = "Are you sure?";
    public static final String LOGOUT_YES = "Yes";
    public static final String LOGOUT_NO = "No";
    public static final String LOGOUT_REPLY = "Successfully logged out!";

    //Check state orientation of output image
    public static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static{
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    //Notification Worker Params
    public static final String KEY_X_ARG = "X";
    public static final String KEY_Y_ARG = "Y";
    public static final String KEY_RESULT = "Result";
}