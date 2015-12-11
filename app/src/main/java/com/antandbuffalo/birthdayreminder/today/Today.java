package com.antandbuffalo.birthdayreminder.today;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;

/**
 * Created by i677567 on 28/8/15.
 */
public class Today extends MyFragment {
    TodayListAdapter todayListAdapter;

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

        //ArrayAdapter<String> codeLearnArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, codeLearnChapters);
        todayListAdapter = new TodayListAdapter();
        //http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        ListView codeLearnLessons = (ListView)rootView.findViewById(R.id.listView1);
        codeLearnLessons.setAdapter(todayListAdapter);
        return rootView;
    }

    @Override
    public void updateData() {
        System.out.println("MyFragment =============== Child");
        todayListAdapter.updateData();
    }
}
