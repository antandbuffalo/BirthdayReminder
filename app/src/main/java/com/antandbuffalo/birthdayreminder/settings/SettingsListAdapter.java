package com.antandbuffalo.birthdayreminder.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.antandbuffalo.birthdayreminder.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by i677567 on 23/9/15.
 */
public class SettingsListAdapter extends BaseAdapter {
    List<SettingsModel> listData;

    SettingsListAdapter() {
        listData = getDataForListView();
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public SettingsModel getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.settings_listitem, parent, false);
        }

        TextView title = (TextView)convertView.findViewById(R.id.title);
        TextView subTitle = (TextView)convertView.findViewById(R.id.subTitle);

        title.setText(listData.get(position).getTitle());
        subTitle.setText(listData.get(position).getSubTitle());

        return convertView;
    }

    public List<SettingsModel> getDataForListView() {
        List data = new ArrayList();
        SettingsModel datum;
        datum = SettingsModel.newInstance();
        datum.setKey("backup");
        datum.setTitle("Take a Backup");
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey("load");
        datum.setTitle("Load from latest Backup File");
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey("deleteall");
        datum.setTitle("Delete All Data");
        data.add(datum);

        return data;
    }
    public void refreshData() {
        listData = getDataForListView();
        this.notifyDataSetChanged();
    }
}
