package com.antandbuffalo.birthdayreminder;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antandbuffalo.birthdayreminder.fragments.Settings;
import com.antandbuffalo.birthdayreminder.fragments.Today;
import com.antandbuffalo.birthdayreminder.fragments.Upcoming;

import java.util.Locale;

/**
 * Created by i677567 on 28/8/15.
 */
public class SectionsAdapter extends FragmentPagerAdapter {
    public SectionsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 0) {
            return Today.newInstance();
        }
        else if(position == 1) {
            return Upcoming.newInstance();
        }
        else if(position == 2) {
            return Settings.newInstance();
        }
        else {
            return null;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "Today as String";
            case 1:
                return "Upcoming as string";
            case 2:
                return "Settings as string";
        }
        return null;
    }
}
