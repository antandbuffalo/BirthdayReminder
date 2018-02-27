package com.antandbuffalo.birthdayreminder.settings;

import android.Manifest;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.antandbuffalo.birthdayreminder.Constants;

/**
 * Created by Jeyabalaji on 24/2/18.
 */

public class SettingsViewModel extends AndroidViewModel {
    public SettingsViewModel(@NonNull Application application) {
        super(application);
    }
}
