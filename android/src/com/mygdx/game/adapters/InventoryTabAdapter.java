package com.mygdx.game.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.mygdx.game.fragments.*;

public class InventoryTabAdapter extends FragmentStatePagerAdapter {
    int mNoOfTabs;

    public InventoryTabAdapter(FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                InventoryItemFragment inventoryItemFragment = new InventoryItemFragment();
                return inventoryItemFragment;
            case 1:
                InventoryCraftFragment inventoryCraftFragment = new InventoryCraftFragment();
                return  inventoryCraftFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}