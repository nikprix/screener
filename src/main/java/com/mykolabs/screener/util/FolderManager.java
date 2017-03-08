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
    public static String getUserDesktopDirPath() {
        // getting user's Desktop's path
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        log.info("Desktop path: " + desktop.getPath());
        return desktop.getPath();
    }

    /**
     * Creates parent folder for per-Program folders with screenshots.
     *
     * @param parentPath
     * @param folderName
     * @return
     */
    public static String createScreensDir(String parentPath, String folderName) {

        // getting full path for creating new folder.
        // includes folder's name.
        String fullPath = createFullDirPath(parentPath, folderName);

        log.info("New folder path: " + String.valueOf(fullPath));

        File directory = new File(String.valueOf(fullPath));
        // no need to check if this directory exists
        directory.mkdir();

        return String.valueOf(fullPath);
    }

    /**
     * Creates full directory path by concatenating parent path and folder's
     * name. Detects user's OS to correctly specify path.
     *
     * @param parentPath
     * @param folderName
     * @return
     */
    private static String createFullDirPath(String parentPath, String folderName) {
        String directoryName;

        // important to define user's OS to have path specified correctly
        switch (WebDriverUtil.OSDetector()) {
            case "Mac":
                directoryName = parentPath.concat("/" + folderName);
                break;
            case "Windows":
                directoryName = parentPath.concat("\\" + folderName);
                break;
            // here, in case of the issues with 2 above cases we just concatenate
            // desired folder name to provided path. Note, there is no slash at the
            // beginning of the folder name for purpose no to breake parent path.
            default:
                directoryName = parentPath.concat("_" + folderName);
        }
        return directoryName;
    }

}
