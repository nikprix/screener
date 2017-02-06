package com.mykolabs.screener.controllers;

import com.mykolabs.screener.beans.ProgramData;
import com.mykolabs.screener.beans.SeleniumData;
import com.mykolabs.screener.util.PropertiesManager;
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
    public void takeScreenshots() {

        // getting Driver instance, which can be either Firefox or Chrome
        // depending from selected Radio button
        WebDriver driver = seleniumData.getDriver();

        // maximizing window, should work for FF and Chrome
        driver.manage().window().maximize();

        // maximize Chrome on Mac
        if (driver instanceof ChromeDriver && OSDetector().contains("mac")) {
            maximizeScreen(driver);
        }

        // looping through ALL pages IDS
//        programData.getProgramPagesIds().forEach(pageId
//                -> {
//            // load page
//            log.info("Loading URL: " + this.createPageUrl(pageId));
//            driver.get(this.createPageUrl(pageId));
//        }
//        );
        List<String> pageIds = programData.getProgramPagesIds();
        for (String pageId : pageIds) {
            
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(ScreenshotTaker.class.getName()).log(Level.SEVERE, null, ex);
            }

            log.info("Loading URL: " + this.createPageUrl(pageId));
            driver.get(this.createPageUrl(pageId));

        }

        // closing browser's window
        driver.quit();

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

    /**
     * Maximizes Chrome.
     *
     * @param driver
     */
    private void maximizeScreen(WebDriver driver) {
        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point position = new Point(0, 0);
        driver.manage().window().setPosition(position);
        Dimension maximizedScreenSize
                = new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight());
        driver.manage().window().setSize(maximizedScreenSize);
    }

    /**
     * Detects OS of the user.
     *
     * @return
     */
    private String OSDetector() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "Windows";
        } else if (os.contains("nux") || os.contains("nix")) {
            return "Linux";
        } else if (os.contains("mac")) {
            return "Mac";
        } else if (os.contains("sunos")) {
            return "Solaris";
        } else {
            return "Other";
        }
    }

}
