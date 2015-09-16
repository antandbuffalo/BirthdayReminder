package com.antandbuffalo.birthdayreminder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.antandbuffalo.birthdayreminder.fragments.Settings;
import com.antandbuffalo.birthdayreminder.fragments.Today;
import com.antandbuffalo.birthdayreminder.fragments.Upcoming;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i677567 on 16/9/15.
 */
//https://github.com/astuetz/PagerSlidingTabStrip
//http://blog.alwold.com/2013/08/28/styling-tabs-in-the-android-action-bar/
public class TabsAdapter extends FragmentPagerAdapter {
    List<String> titles = new ArrayList<String>(3);
    public  TabsAdapter(FragmentManager fm) {
        super(fm);
        titles.add("Today");
        titles.add("Upcoming");
        titles.add("Settings");
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Today();
            case 1:
                return new Upcoming();
            case 2:
                return new Settings();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}