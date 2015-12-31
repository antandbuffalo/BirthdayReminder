package com.antandbuffalo.birthdayreminder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;
import com.antandbuffalo.birthdayreminder.settings.Settings;
import com.antandbuffalo.birthdayreminder.today.Today;
import com.antandbuffalo.birthdayreminder.upcoming.Upcoming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by i677567 on 16/9/15.
 */
//https://github.com/astuetz/PagerSlidingTabStrip
//http://blog.alwold.com/2013/08/28/styling-tabs-in-the-android-action-bar/
public class TabsAdapter extends FragmentPagerAdapter {
    List<String> titles = new ArrayList<String>(3);
    Map<Integer, MyFragment> mPageReferenceMap = new HashMap<Integer, MyFragment>();
    public  TabsAdapter(FragmentManager fm) {
        super(fm);
        titles.add("Today");
        titles.add("Upcoming");
        titles.add("Settings");
    }
    @Override
    public Fragment getItem(int position) {
        MyFragment myFragment = null;
        switch (position) {
            case 0:
                myFragment = Today.newInstance();
                break;
            case 1:
                myFragment = Upcoming.newInstance();
                break;
            case 2:
                myFragment = Settings.newInstance();
                break;
        }
        return myFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MyFragment myFragment = null;
        myFragment = (MyFragment)super.instantiateItem(container, position);
        mPageReferenceMap.put(position, myFragment);
        return myFragment;
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        if(position == 0) {
            title = titles.get(position);   // + " (" + DateOfBirthDBHelper.getCountTodayAndBelated() + ")";
        }
        else {
            title = titles.get(position);
        }
        return title;
    }

    public Fragment getFragment(int key) {
        MyFragment fragment = mPageReferenceMap.get(key);
        return fragment;
    }
}