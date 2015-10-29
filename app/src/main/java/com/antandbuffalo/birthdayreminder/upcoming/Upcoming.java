package com.antandbuffalo.birthdayreminder.upcoming;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.today.TodayListAdapter;

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

        //ArrayAdapter<String> codeLearnArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, codeLearnChapters);
        UpcomingListAdapter upcomingListAdapter = new UpcomingListAdapter();
        //http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        ListView upcomingListView = (ListView)rootView.findViewById(R.id.upcomingListView);
        upcomingListView.setAdapter(upcomingListAdapter);

        return rootView;
    }
}
