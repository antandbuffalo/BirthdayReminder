package com.antandbuffalo.birthdayreminder.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antandbuffalo.birthdayreminder.R;

/**
 * Created by i677567 on 28/8/15.
 */
public class Upcoming extends Fragment {
    public static Upcoming newInstance() {
        Upcoming fragment = new Upcoming();
        return fragment;
    }

    public Upcoming() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.upcoming, container, false);
        return rootView;
    }
}
