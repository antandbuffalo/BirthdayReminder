package com.antandbuffalo.birthdayreminder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.antandbuffalo.birthdayreminder.addnew.AddNew;
import com.antandbuffalo.birthdayreminder.common.Storage;
import com.antandbuffalo.birthdayreminder.database.DBHelper;
import com.antandbuffalo.birthdayreminder.database.OptionsDBHelper;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;
import com.antandbuffalo.birthdayreminder.restore.RestoreBackup;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.util.Collections;


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
    int currentTabPosition;
    private AdView mAdView;
    public GoogleSignInClient googleSignInClient;

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
        loadAd();
        //DobDBHelper.deleteAll();
        //DobDBHelper.addDOB();
        //DobDBHelper.selectAll(); 
        //Util.writeToFile();
        //Util.readFromFile();
        //OptionsDBHelper.insertDefaultValues();
        //System.out.println(DateOfBirthDBHelper.selectToday());

//        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        nMgr.cancelAll();

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
        Boolean isSecondTime = settings.getBoolean(Constants.PREFERENCE_IS_SECONDTIME, false);
        if(!isSecondTime) {
            if(Util.isBackupFileFound()) {
                Intent intent = new Intent(this, RestoreBackup.class);
                startActivityForResult(intent, Constants.ADD_NEW_MEMBER);
                //loadBackupFile();
            }
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.PREFERENCE_IS_SECONDTIME, true);
            editor.commit();
        }
        OptionsDBHelper.populatePage();
        Util.createEmptyFolder();

        DriveSignIn();
    }

    public void setRepeatingAlarm() {
        Log.i("MAIN", "Setting repeating alarm in main activity");
        SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_NAME, 0);
        int hour = settings.getInt(Constants.PREFERENCE_NOTIFICATION_TIME_HOURS, 0);
        int minute = settings.getInt(Constants.PREFERENCE_NOTIFICATION_TIME_MINUTES, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int frequency = Storage.getNotificationFrequency(settings);
        Util.setRepeatingAlarm(this, alarmManager, hour, minute, frequency);

//        Intent intent = new Intent(this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123,
//                intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        Calendar calendar = Calendar.getInstance();
//        // 12:00 AM
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//
//        //Send notification twice a day
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);

        //Send notification every 5 seconds
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (60   * 1000), pendingIntent);

        //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), (5 * 1000), pendingIntent);
    }

    public void loadAd() {
        mAdView = this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 999) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                        @Override
                        public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                            Log.i("SUCC", "login success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("FAIL", e.getLocalizedMessage());
                        }
                    });
        }


        System.out.println("In main activity " + requestCode);
        switch (requestCode) {
            case Constants.ADD_NEW_MEMBER: {
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
                break;
            }
            case Constants.DELETE_MEMBER: {
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
                break;
            }
            case Constants.REFRESH_SETTINGS: {
                if(resultCode == RESULT_OK) {
                    int index = mViewPager.getCurrentItem();
                    TabsAdapter adapter = (TabsAdapter) mViewPager.getAdapter();
                    MyFragment fragment = (MyFragment) adapter.getFragment(index);
                    fragment.refreshData();
                }
                break;
            }

        }
    }

    public void selectTab() {
        int pixelsToMove = Util.convertDPtoPixel(65, getResources());
        //getting the fragment reference
        //http://stackoverflow.com/questions/18609261/getting-the-current-fragment-instance-in-the-viewpager
        mViewPager.setCurrentItem(currentTabPosition);
        if (currentTabPosition == 0) {
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
        } else if (currentTabPosition == 1) {
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
        } else if (currentTabPosition == 2) {
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

        if (DataHolder.getInstance().refreshTracker.get(currentTabPosition)) {
            MyFragment fragment = (MyFragment) mTabsAdapter.getFragment(currentTabPosition);
            if(fragment != null) {
                fragment.refreshData();
            }
            DataHolder.getInstance().refreshTracker.set(currentTabPosition, false);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        currentTabPosition = tab.getPosition();
        selectTab();
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

    @Override
    public void onBackPressed() {
        if(currentTabPosition == 1 || currentTabPosition == 2) {
            currentTabPosition = 0;
            selectTab();
        }
        else {
            super.onBackPressed();
        }
        //Log.i("BACK", mTabsAdapter.toString());
    }

    public void DriveSignIn() {
        // https://stackoverflow.com/questions/54052220/how-to-access-the-application-data-on-google-drive-on-android-with-its-rest-api

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken("405608185466-1ertijbnjfc99cgdvbmplv7r0c840vu6.apps.googleusercontent.com")x
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        startActivityForResult(googleSignInClient.getSignInIntent(), 999);

    }

//    public void createFileToDrive() {
//        File fileMetadata = new File();
//        fileMetadata.setName("config.json");
//        fileMetadata.setParents(Collections.singletonList("appDataFolder"));
//        java.io.File filePath = new java.io.File("files/config.json");
//        FileContent mediaContent = new FileContent("application/json", filePath);
//        File file = driveService.files().create(fileMetadata, mediaContent)
//                .setFields("id")
//                .execute();
//        System.out.println("File ID: " + file.getId());
//    }
}