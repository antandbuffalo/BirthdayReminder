package com.antandbuffalo.birthdayreminder.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DataHolder;
import com.antandbuffalo.birthdayreminder.MainActivity;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.database.OptionsDBHelper;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;

import java.util.Date;
import java.util.List;

/**
 * Created by i677567 on 28/8/15.
 */
public class Settings extends MyFragment {
    SettingsListAdapter settingsListAdapter;
    public static Settings newInstance() {
        Settings fragment = new Settings();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.settings, container, false);

        final ListView settingsList = (ListView)rootView.findViewById(R.id.settingsList);

        settingsListAdapter = new SettingsListAdapter();
        //http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        settingsList.setAdapter(settingsListAdapter);

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SettingsModel option = settingsListAdapter.listData.get(position);
                if (option.getKey().equalsIgnoreCase(Constants.SETTINGS_WRITE_FILE)) {
                    Toast.makeText(inflater.getContext(), Util.writeToFile(), Toast.LENGTH_SHORT).show();
                    option.setSubTitle("Last backup was");
                    option.setUpdatedOn(new Date());
                    OptionsDBHelper.updateOption(option);
                    settingsListAdapter.refreshData();
                }
                else if (option.getKey().equalsIgnoreCase(Constants.SETTINGS_READ_FILE)) {
                    Toast.makeText(inflater.getContext(), Util.readFromFile(Constants.FILE_NAME), Toast.LENGTH_SHORT).show();
                    option.setSubTitle("Data was loaded");
                    option.setUpdatedOn(new Date());
                    OptionsDBHelper.updateOption(option);
                    settingsListAdapter.refreshData();
                    for (int i = 0; i < DataHolder.getInstance().refreshTracker.size(); i++) {
                        DataHolder.getInstance().refreshTracker.set(i, true);
                    }
                }
                else if (option.getKey().equalsIgnoreCase(Constants.SETTINGS_DELETE_ALL)) {
                    //put confirmation here
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to delete all?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
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
                }

            }
        });

        return rootView;
    }
}
