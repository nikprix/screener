package com.mykolabs.screener.controllers;

import com.mykolabs.screener.beans.ProgramData;
import com.mykolabs.screener.beans.SeleniumDataFactory;
import com.mykolabs.screener.util.FolderManager;
import com.mykolabs.screener.util.PropertiesManager;
import com.mykolabs.screener.util.WebDriverUtil;
import java.awt.Toolkit;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nikprixmar
 */
public class ScreenshotTaker {

    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private final static String DOMAIN_PROTOCOL = "http://";
    private final static String WWW = "www";

    private ProgramData programData;
    private String browser;
    private String screenshotsFolderPath = "";

    /**
     * Static factory method returns an object of this class.
     *
     * @param browser
     * @param programData
     * @return
     */
    public static ScreenshotTaker getInstance(String browser, ProgramData programData) {
        return new ScreenshotTaker(browser, programData);
    }

    /**
     * Initializing fields.
     *
     * @param browser
     * @param programData
     */
    private ScreenshotTaker(String browser, ProgramData programData) {
        this.browser = browser;
        this.programData = programData;
    }

    /**
     * Takes screenshot of loaded page.
     */
    public void takeScreenshots() {

        // getting Driver instance, which can be either Firefox or Chrome
        // depending from selected Radio button
        WebDriver driver = SeleniumDataFactory.getInstance(browser).getDriver();

        // maximizing window, should work for FF and Chrome
        driver.manage().window().maximize();

        // maximize Chrome on Mac
        if (driver instanceof ChromeDriver && WebDriverUtil.OSDetector().contains("Mac")) {
            WebDriverUtil.maximizeScreen(driver);
        }

        // creating folder for screenshots:
        screenshotsFolderPath = FolderManager.createScreensDir();

        // looping through ALL pages IDS
        programData.getProgramPagesIds().forEach(pageId
                -> {

            log.info("Loading URL: " + this.createPageUrl(pageId));

            // need to pause Thread completely BEFORE page load
            WebDriverUtil.setTimeout(2500);

            driver.get(this.createPageUrl(pageId));

            // need to pause Thread completely AFTER page load
            WebDriverUtil.setTimeout(2500);

        }
        );

//        List<String> pageIds = programData.getProgramPagesIds();
//        for (String pageId : pageIds) {
//
//            log.info("Loading URL: " + this.createPageUrl(pageId));
//
//            WebDriverUtil.setTimeout(2500);
//            driver.get(this.createPageUrl(pageId));
//            WebDriverUtil.setTimeout(2500);
//        }
        // Closing browser and removing Webdriver's instance
        SeleniumDataFactory.getInstance(browser).removeDriver();

    }

    /**
     * Creates program URL.
     *
     * @return
     */
    private String createPageUrl(String pageId) {
        Properties props = PropertiesManager.getProperties();
        // Creating request URL
        return new StringBuilder()
                .append(DOMAIN_PROTOCOL)
                .append(WWW)
                .append(".")
                .append(programData.getDomainURL())
                .append("/")
                .append(programData.getDomainPath())
                .append("/")
                .append("index.html")
                .append("?")
                .append("collection=")
                .append(programData.getCollectionId())
                .append("&")
                .append("presentationid=")
                .append(programData.getPresentationId())
                .append("#")
                .append(pageId)
                .toString();
    }

}
