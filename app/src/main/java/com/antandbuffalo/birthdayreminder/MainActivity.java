package com.antandbuffalo.birthdayreminder;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.antandbuffalo.birthdayreminder.addnew.AddNew;
import com.antandbuffalo.birthdayreminder.database.DBHelper;
import com.antandbuffalo.birthdayreminder.database.DobDBHelper;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    TabsAdapter mTabsAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     * http://www.codelearn.org/android-tutorial/android-listview
     */
    ViewPager mViewPager;
    Button addNew;

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("sadfasf", "asdfsadfs");
        DBHelper.createInstance(this);
        //DobDBHelper.deleteAll();
        DobDBHelper.addDOB();
        //DobDBHelper.selectAll();
        //Util.writeToFile();
        //Util.readFromFile();

        addNew = (Button)findViewById(R.id.addNew);

        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.setDisplayShowTitleEnabled(false);  //to hide title
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

        addNew.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // TODO Auto-generated method stub
                Intent intent = new Intent(v.getContext(), AddNew.class);
                startActivity(intent);
                //finish();
            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        if(tab.getPosition() == 0 || tab.getPosition() == 1) {
            addNew.setVisibility(View.VISIBLE);
        }
        else if(tab.getPosition() == 2) {
            addNew.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
