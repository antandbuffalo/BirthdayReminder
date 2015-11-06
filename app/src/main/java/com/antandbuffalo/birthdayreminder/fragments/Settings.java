package com.antandbuffalo.birthdayreminder.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.addnew.AddNew;
import com.antandbuffalo.birthdayreminder.database.DobDBHelper;

/**
 * Created by i677567 on 28/8/15.
 */
public class Settings extends Fragment {
    public static Settings newInstance() {
        Settings fragment = new Settings();
        return fragment;
    }

    public Settings() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.settings, container, false);

        Button addNew = (Button)rootView.findViewById(R.id.readFile);
        addNew.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Util.readFromFile();
                Toast.makeText(inflater.getContext(), "Read file success. Please exit app and open", Toast.LENGTH_SHORT).show();
            }
        });

        Button deleteAll = (Button)rootView.findViewById(R.id.deleteAll);
        deleteAll.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                DobDBHelper.deleteAll();
                Toast.makeText(inflater.getContext(), "Delete All success. Please exit app and open", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
