package com.antandbuffalo.birthdayreminder.common;

import java.io.File;

public interface DriveServiceListener {
    public void loggedIn();
    public void fileDownloaded(File file);
    public void cancelled();
    public void handleError(Exception exception);
}
