package com.antandbuffalo.birthdayreminder;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.antandbuffalo.birthdayreminder.addnew.AddNew;
import com.antandbuffalo.birthdayreminder.database.DBHelper;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;
import com.antandbuffalo.birthdayreminder.today.Today;
import com.antandbuffalo.birthdayreminder.upcoming.Upcoming;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    TabsAdapter mTabsAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     * http://www.codelearn.org/android-tutorial/android-listview
     */
    ViewPager mViewPager;
    Button addNew;
    Animation animFadeOut, animFadeIn;

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper.createInstance(this);
        //DobDBHelper.deleteAll();
        //DobDBHelper.addDOB();
        //DobDBHelper.selectAll();
        //Util.writeToFile();
        //Util.readFromFile();

        addNew = (Button)findViewById(R.id.addNew);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        addNew.setAnimation(animFadeOut);
        addNew.setBackgroundResource(R.drawable.rounded_button);
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.setDisplayShowTitleEnabled(false);  //to hide title
//        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable());
        // Set up the ViewPager with the sections adapter.+
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
                startActivityForResult(intent, Constants.ADD_NEW_MEMBER);
                //finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(data.getStringExtra(Constants.IS_USER_ADDED));
        System.out.println(Constants.FLAG_SUCCESS);
        if (requestCode == Constants.ADD_NEW_MEMBER) {
            if(resultCode == RESULT_OK){
                if(data.getStringExtra(Constants.IS_USER_ADDED).equalsIgnoreCase(Constants.FLAG_SUCCESS)) {
                    int index = mViewPager.getCurrentItem();
                    TabsAdapter adapter = (TabsAdapter)mViewPager.getAdapter();
                    MyFragment fragment = (MyFragment)adapter.getFragment(index);
                    fragment.updateData();
                }
            }
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.

        //getting the fragment reference
        //http://stackoverflow.com/questions/18609261/getting-the-current-fragment-instance-in-the-viewpager
        mViewPager.setCurrentItem(tab.getPosition());
        if(tab.getPosition() == 0) {
            addNew.setVisibility(View.VISIBLE);
        }
        else if(tab.getPosition() == 1) {
            addNew.setVisibility(View.VISIBLE);
            //Upcoming upcoming = (Upcoming)mTabsAdapter.getFragment(tab.getPosition());
            //upcoming.updateData();
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
