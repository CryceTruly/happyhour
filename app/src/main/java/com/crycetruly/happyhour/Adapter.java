package com.crycetruly.happyhour;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Elia on 01/05/2018.
 */

public class Adapter  extends FragmentPagerAdapter {
    public Adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return new PreviousFragment();

            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My Offers";
            case 1:
                return "ADD";
            case 2:
                return "ACCOUNT";

            default:
                return super.getPageTitle(position);
        }


    }
}
