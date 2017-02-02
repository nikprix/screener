package com.mykolabs.screener.controllers;

import com.mykolabs.screener.beans.ProgramData;
import com.mykolabs.screener.beans.SeleniumData;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author nikprixmar
 */
public class ScreenshotTaker {

    private SeleniumData seleniumData;
    private ProgramData programData;

    /**
     * Static factory method returns an object of this class.
     *
     * @param seleniumData
     * @param programData
     * @return
     */
    public static ScreenshotTaker getInstance(SeleniumData seleniumData, ProgramData programData) {
        return new ScreenshotTaker(seleniumData, programData);
    }

    /**
     * Initializing fields.
     *
     * @param seleniumData
     * @param programData
     */
    private ScreenshotTaker(SeleniumData seleniumData, ProgramData programData) {
        this.seleniumData = seleniumData;
        this.programData = programData;
    }

    /**
     * Takes screenshot of loaded page.
     */
    public void takeScreenshot() {

        // getting Driver instance, which can be either Firefox or Chrome
        WebDriver driver = seleniumData.getDriver();

    }

}
