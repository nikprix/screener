package com.mykolabs.screener.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager creation of folders for saving screenshots.
 *
 * @author nikprixmar
 */
public class FolderManager {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(FolderManager.class);

    /**
     * Returns user's home directory path. Should be crossplatform according to:
     * http://stackoverflow.com/a/586345
     *
     * @return
     */
    private static String getUserDesktopDirPath() {
        // getting user's Desktop's path
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        log.info("Desktop path: " + desktop.getPath());
        return desktop.getPath();
    }

    public static String createScreensDir() {
        String PATH = getUserDesktopDirPath();
        String directoryName;

        switch (WebDriverUtil.OSDetector()) {
            case "Mac":
                directoryName = PATH.concat("/mDADI_Screens");
                break;
            case "Windows": directoryName = PATH.concat("\\mDADI_Screens");
                break;
            default: directoryName = PATH.concat("_mDADI_Screens");
        }

        log.info("New folder path: " + String.valueOf(directoryName));
        
        File directory = new File(String.valueOf(directoryName));
        // no need to check if this directory exists
        directory.mkdir();

        return String.valueOf(directoryName);
    }

}
