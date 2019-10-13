package com.antandbuffalo.birthdayreminder.today;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;
import com.antandbuffalo.birthdayreminder.sharewish.ShareWish;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by i677567 on 28/8/15.
 */
public class  Today extends MyFragment {
    TodayListAdapter todayListAdapter;

    public static Today newInstance() {
        Today fragment = new Today();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.today, container, false);

        MobileAds.initialize(getActivity(), Constants.ADMOB_APP_ID);

        //ArrayAdapter<String> codeLearnArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, codeLearnChapters);
        todayListAdapter = new TodayListAdapter();
        //http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        ListView todayList = (ListView)rootView.findViewById(R.id.listView1);
        todayList.setAdapter(todayListAdapter);

        todayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DateOfBirth dateOfBirth = todayListAdapter.getItem(position);
                Intent intent = new Intent(view.getContext(), ShareWish.class);
                intent.putExtra("currentDOB", dateOfBirth);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void refreshData() {
        todayListAdapter.refreshData();
    }
}
