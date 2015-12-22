package com.antandbuffalo.birthdayreminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i677567 on 22/12/15.
 */
public class DataHolder {
    private static DataHolder dataHolder = null;
    public List<Boolean> refreshTracker = null;

    private DataHolder() {
        this.refreshTracker = new ArrayList();
        this.refreshTracker.add(false); //for today
        this.refreshTracker.add(false); //for upcoming
        this.refreshTracker.add(false); //for settings
    }
    public static DataHolder getInstance() {
        if(dataHolder == null) {
            dataHolder = new DataHolder();
        }
        return dataHolder;
    }
}
