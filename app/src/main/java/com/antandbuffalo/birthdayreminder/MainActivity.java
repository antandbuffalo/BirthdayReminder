package com.antandbuffalo.birthdayreminder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.addnew.AddNew;
import com.antandbuffalo.birthdayreminder.database.DBHelper;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;
import com.antandbuffalo.birthdayreminder.notification.AlarmReceiver;

import java.util.Calendar;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    TabsAdapter mTabsAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     * http://www.codelearn.org/android-tutorial/android-listview
     */
    ViewPager mViewPager;
    Button addNew;
    Animation animFadeOut, animFadeIn;
    RelativeLayout mainContainer;
    AlarmManager alarmManager;

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initValues();
        //DobDBHelper.deleteAll();
        //DobDBHelper.addDOB();
        //DobDBHelper.selectAll(); 
        //Util.writeToFile();
        //Util.readFromFile();
        //OptionsDBHelper.insertDefaultValues();
        //System.out.println(DateOfBirthDBHelper.selectToday());

        mainContainer = (RelativeLayout) findViewById(R.id.mainContainer);
        addNew = (Button) findViewById(R.id.addNew);
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

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        setRepeatingAlarm();

        SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_NAME, 0);
        Boolean isSecondTime = settings.getBoolean("isSecondTime", false);
        if(!isSecondTime) {
            if(Util.isBackupFileFound()) {
                loadBackupFile();
            }
        }
        Util.createEmptyFolder();
    }

    public void loadBackupFile() {
        SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirmation");
        alertDialogBuilder.setMessage("Backup file found. Do you want to load data from backup file?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Util.readFromFile(Constants.FILE_NAME);
                for (int i = 0; i < DataHolder.getInstance().refreshTracker.size(); i++) {
                    DataHolder.getInstance().refreshTracker.set(i, true);
                }
                Toast toast = Toast.makeText(getApplicationContext(), Constants.NOTIFICATION_SUCCESS_DATA_LOAD, Toast.LENGTH_SHORT);
                toast.show();
                editor.putBoolean("isSecondTime", true);
                editor.commit();

                MyFragment fragment = (MyFragment) mTabsAdapter.getFragment(0);
                if(fragment != null) {
                    fragment.refreshData();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editor.putBoolean("isSecondTime", true);
                editor.commit();
            }
        });
        alertDialogBuilder.show();
    }

    public void setRepeatingAlarm() {
        System.out.println("Setting alarm");
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();
        // 9 AM
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //Send notification twice a day
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HALF_DAY, pendingIntent);

        //Send notification every 5 seconds
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (5 * 1000), pendingIntent);

        //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), (5 * 1000), pendingIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("In main activity");
        if (requestCode == Constants.ADD_NEW_MEMBER) {
            if (resultCode == RESULT_OK) {
                if (data.getStringExtra(Constants.IS_USER_ADDED).equalsIgnoreCase(Constants.FLAG_SUCCESS)) {
                    int index = mViewPager.getCurrentItem();
                    TabsAdapter adapter = (TabsAdapter) mViewPager.getAdapter();
                    MyFragment fragment = (MyFragment) adapter.getFragment(index);
                    fragment.refreshData();
                    //mTabsAdapter.notifyDataSetChanged();
                    for (int i = 0; i < DataHolder.getInstance().refreshTracker.size(); i++) {
                        if (i != index) {
                            DataHolder.getInstance().refreshTracker.set(i, true);
                        }
                    }
                }
            }
        }
        else if(requestCode == Constants.DELETE_MEMBER) {
            System.out.println("after delete activity");
            if(resultCode == RESULT_OK) {
                int index = mViewPager.getCurrentItem();
                TabsAdapter adapter = (TabsAdapter) mViewPager.getAdapter();
                MyFragment fragment = (MyFragment) adapter.getFragment(index);
                fragment.refreshData();
                //mTabsAdapter.notifyDataSetChanged();
                for (int i = 0; i < DataHolder.getInstance().refreshTracker.size(); i++) {
                    if (i != index) {
                        DataHolder.getInstance().refreshTracker.set(i, true);
                    }
                }
            }
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.

        int pixelsToMove = Util.convertDPtoPixel(65, getResources());

        //getting the fragment reference
        //http://stackoverflow.com/questions/18609261/getting-the-current-fragment-instance-in-the-viewpager
        mViewPager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 0) {
            if (addNew.getVisibility() == View.GONE) {
                addNew.setVisibility(View.VISIBLE);
                addNew.animate().translationXBy(-pixelsToMove).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //adding one listener everywhere. otherwise it is automatically executing the third listener, which hides the button after the animation
                    }
                });
            }
        } else if (tab.getPosition() == 1) {
            if (addNew.getVisibility() == View.GONE) {
                addNew.setVisibility(View.VISIBLE);
                addNew.animate().translationXBy(-pixelsToMove).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //adding one listener everywhere. otherwise it is automatically executing the third listener, which hides the button after the animation
                    }
                });
            }
        } else if (tab.getPosition() == 2) {
            if (addNew.getVisibility() == View.VISIBLE) {
                addNew.animate().translationXBy(pixelsToMove).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        addNew.setVisibility(View.GONE);
                    }
                });
            }
        }

//        System.out.println("width -- " + addNew.getWidth());
//        System.out.println(Util.convertDPtoPixel(50, getResources()));
//        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)addNew.getLayoutParams();
//        System.out.println(lp.rightMargin);
//        System.out.println(Util.convertDPtoPixel(15, getResources()));

        if (DataHolder.getInstance().refreshTracker.get(tab.getPosition())) {
            MyFragment fragment = (MyFragment) mTabsAdapter.getFragment((tab.getPosition()));
            if(fragment != null) {
                fragment.refreshData();
            }
            DataHolder.getInstance().refreshTracker.set(tab.getPosition(), false);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public void initValues() {
        DBHelper.createInstance(this);
        DataHolder.getInstance().setAppContext(getApplicationContext());
    }
}