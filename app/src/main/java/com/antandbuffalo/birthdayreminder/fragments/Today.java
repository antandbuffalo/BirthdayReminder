package com.antandbuffalo.birthdayreminder.fragments;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antandbuffalo.birthdayreminder.R;

/**
 * Created by i677567 on 28/8/15.
 */
public class Today extends Fragment {
    public static Today newInstance() {
        Today fragment = new Today();
        return fragment;
    }

    public Today() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.today, container, false);
        return rootView;
    }
}
