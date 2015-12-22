package com.antandbuffalo.birthdayreminder.settings;

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

import com.antandbuffalo.birthdayreminder.DataHolder;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;

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
                SettingsModel settingsModel = settingsListAdapter.listData.get(position);
                if(settingsModel.getKey().equalsIgnoreCase("backup")) {

                }
                else if(settingsModel.getKey().equalsIgnoreCase("deleteall")) {
                    DateOfBirthDBHelper.deleteAll();
                    Toast.makeText(inflater.getContext(), "Delete All success. Please exit app and open", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < DataHolder.getInstance().refreshTracker.size(); i++) {
                        DataHolder.getInstance().refreshTracker.set(i, true);
                    }
                }
                else if(settingsModel.getKey().equalsIgnoreCase("load")) {
                    Util.readFromFile();
                    Toast.makeText(inflater.getContext(), "Read file success. Please exit app and open", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < DataHolder.getInstance().refreshTracker.size(); i++) {
                        DataHolder.getInstance().refreshTracker.set(i, true);
                    }
                }
            }
        });


/*        Button addNew = (Button)rootView.findViewById(R.id.readFile);
        addNew.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Util.readFromFile();
                Toast.makeText(inflater.getContext(), "Read file success. Please exit app and open", Toast.LENGTH_SHORT).show();
            }
        });

        Button deleteAll = (Button)rootView.findViewById(R.id.deleteAll);
        deleteAll.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                DateOfBirthDBHelper.deleteAll();
                Toast.makeText(inflater.getContext(), "Delete All success. Please exit app and open", Toast.LENGTH_SHORT).show();
            }
        });*/



        return rootView;
    }
}
