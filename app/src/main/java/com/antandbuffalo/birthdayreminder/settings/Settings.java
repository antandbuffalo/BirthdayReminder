package com.antandbuffalo.birthdayreminder.settings;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DataHolder;
import com.antandbuffalo.birthdayreminder.MainActivity;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.about.About;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.database.OptionsDBHelper;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;
import com.antandbuffalo.birthdayreminder.modifytoday.ModifyToday;
import com.antandbuffalo.birthdayreminder.notificationfrequency.NotificationFrequency;
import com.antandbuffalo.birthdayreminder.notificationsettings.NotificationSettings;
import com.antandbuffalo.birthdayreminder.notificationtime.NotificationTime;
import com.antandbuffalo.birthdayreminder.wishtemplate.WishTemplate;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by i677567 on 28/8/15.
 */
public class Settings extends MyFragment {
    SettingsListAdapter settingsListAdapter;
    private SettingsViewModel settingsViewModel;
    LayoutInflater layoutInflater;
    SettingsModel selectedOption;
    ProgressBar progressSpinner;
    Settings mySettings = this;

    public static Settings newInstance() {
        Settings fragment = new Settings();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutInflater = inflater;
        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);

        View rootView = inflater.inflate(R.layout.settings, container, false);

        final ListView settingsList = (ListView)rootView.findViewById(R.id.settingsList);

        progressSpinner = (ProgressBar)rootView.findViewById(R.id.progressBar);
        progressSpinner.setVisibility(View.GONE);
        progressSpinner.setBackgroundColor(this.getResources().getColor(R.color.spinner));

        settingsListAdapter = new SettingsListAdapter();
        //http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        settingsList.setAdapter(settingsListAdapter);

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedOption = settingsListAdapter.listData.get(position);
                if (selectedOption.getKey().equalsIgnoreCase(Constants.SETTINGS_WRITE_FILE)) {
                    if(getStoragePermission(Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE)) {
                        createBackup(true);
                    }
                } else if (selectedOption.getKey().equalsIgnoreCase(Constants.SETTINGS_READ_FILE)) {
                    if(getStoragePermission(Constants.MY_PERMISSIONS_READ_EXTERNAL_STORAGE)) {
                        restoreBackup(true);
                    }
                } else if (selectedOption.getKey().equalsIgnoreCase(Constants.SETTINGS_DELETE_ALL)) {
                    //put confirmation here
                    new AlertDialog.Builder(getActivity())
                            //.setIcon(android.R.drawable.ic_dialog_alert)
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to delete all?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(inflater.getContext(), DateOfBirthDBHelper.deleteAll(), Toast.LENGTH_SHORT).show();
                                    for (int i = 0; i < DataHolder.getInstance().refreshTracker.size(); i++) {
                                        DataHolder.getInstance().refreshTracker.set(i, true);
                                    }
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else if (selectedOption.getKey().equalsIgnoreCase(Constants.SETTINGS_MODIFY_TODAY)) {

                    Intent intent = new Intent(view.getContext(), ModifyToday.class);
                    getActivity().startActivityForResult(intent, Constants.REFRESH_SETTINGS);
                } else if (selectedOption.getKey().equalsIgnoreCase(Constants.SETTINGS_ABOUT)) {
                    Intent intent = new Intent(view.getContext(), About.class);
                    startActivity(intent);
                } else if (selectedOption.getKey().equalsIgnoreCase(Constants.SETTINGS_NOTIFICATION)) {
                    Intent intent = new Intent(view.getContext(), NotificationSettings.class);
                    getActivity().startActivityForResult(intent, Constants.REFRESH_SETTINGS);
                } else if (selectedOption.getKey().equalsIgnoreCase(Constants.SETTINGS_WISH_TEMPLATE)) {
                    Intent intent = new Intent(view.getContext(), WishTemplate.class);
                    getActivity().startActivityForResult(intent, Constants.REFRESH_SETTINGS);
                }
                else if (selectedOption.getKey().equalsIgnoreCase(Constants.SETTINGS_NOTIFICATION_TIME)) {
                    Intent intent = new Intent(view.getContext(), NotificationTime.class);
                    getActivity().startActivityForResult(intent, Constants.REFRESH_SETTINGS);
                }
                else if (selectedOption.getKey().equalsIgnoreCase(Constants.SETTINGS_NOTIFICATION_FREQUENCY)) {
                    Intent intent = new Intent(view.getContext(), NotificationFrequency.class);
                    getActivity().startActivityForResult(intent, Constants.REFRESH_SETTINGS);
                }
            }
        });

        return rootView;
    }

    public void showSpinner() {
        Log.i("show spinner", "show spinner");
        progressSpinner.setVisibility(View.VISIBLE);
    }

    public void hideSpinner() {
        Log.i("hide spinner", "hide spinner");
        progressSpinner.setVisibility(View.GONE);
    }

    public void showToast(String message) {
        Toast.makeText(layoutInflater.getContext(), message, Toast.LENGTH_LONG).show();
    }

    public void createBackup(Boolean isGranted) {
        if(isGranted) {
            new UISpinner(mySettings).execute("backup");
        }
        else {
            Toast.makeText(layoutInflater.getContext(), "Please provide storage access to save the backup file", Toast.LENGTH_LONG).show();
        }
    }

    public void disableUserInteraction() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUserInteraction() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    public void restoreBackup(Boolean isGranted) {
        if(isGranted) {
            new UISpinner(mySettings).execute("restore");
        }
        else {
            Toast.makeText(layoutInflater.getContext(), "Please provide storage access to read the backup file", Toast.LENGTH_LONG).show();
        }
    }


    public Boolean getStoragePermission(int permissionType) {
        switch (permissionType) {
            case Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                    return false;
                } else {
                    // Permission has already been granted
                    return true;
                }
            }
            case Constants.MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);

                    return false;
                } else {
                    // Permission has already been granted
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    createBackup(true);

                } else {
                    // permission denied, boo! Disable the
                    createBackup(false);
                }
                return;
            }
            case Constants.MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    restoreBackup(true);

                } else {
                    // permission denied, boo! Disable the
                    restoreBackup(false);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    public void refreshData() {
        settingsListAdapter.notifyDataSetChanged();
    }

    public class UISpinner extends AsyncTask<String, String, String> {

        Settings container;
        public UISpinner(Settings f) {
            this.container = f;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if(params.length > 0) {
                    String type = params[0];
                    switch (type) {
                        case "backup": {
                            Util.writeToFile();
                            break;
                        }
                        case "restore": {
                            Util.readFromFile(Constants.FILE_NAME);
                            break;
                        }
                    }
                }

                // Emulate a long running process
                // In this case we are pretending to fetch the URL content
                //Thread.sleep(3000); // This takes 3 seconds

                // If you are implementing actual fetch API, the call would be something like this,
                // API.fetchURL(params[0]);
            }catch(Exception ex) {}
            return params[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            container.showSpinner();
            container.disableUserInteraction();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("ASYNC", result);
            // The activity can be null if it is thrown out by Android while task is running!
            if(container != null && container.getActivity() != null) {
                if(result.equalsIgnoreCase("backup")) {
                    container.showToast(Constants.SETTINGS_MSG.get(result));
                    Util.updateBackupTime(selectedOption);
                    container.refreshData();
                }
                else if(result.equalsIgnoreCase("restore")) {
                    container.showToast(Constants.NOTIFICATION_SUCCESS_DATA_LOAD);
                    Util.updateRestoreTime(selectedOption);
                    for (int i = 0; i < DataHolder.getInstance().refreshTracker.size(); i++) {
                        DataHolder.getInstance().refreshTracker.set(i, true);
                    }
                    container.refreshData();
                }
                container.hideSpinner();
                container.enableUserInteraction();
                this.container = null;
            }
        }
    }
}
