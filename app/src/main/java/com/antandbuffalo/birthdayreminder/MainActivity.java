package com.antandbuffalo.birthdayreminder;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.ViewPager;

import com.antandbuffalo.birthdayreminder.fragments.Settings;
import com.antandbuffalo.birthdayreminder.fragments.Today;
import com.antandbuffalo.birthdayreminder.fragments.Upcoming;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    TabsAdapter mTabsAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mTabsAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mTabsAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab().setText(mTabsAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

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
}
