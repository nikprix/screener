package com.mykolabs.screener.beans;

import com.mykolabs.screener.util.FolderManager;
import com.mykolabs.screener.util.PropertiesManager;
import com.mykolabs.screener.util.WebDriverUtil;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.LoggerFactory;

/**
 * Creates Webdriver instance based on requested browser. Also, allows
 * initializing the WebDriver object as a Thread Local for Parallel Test
 * Execution. Good read:
 * http://seleniumautomationhelper.blogspot.ca/2014/02/initializing-webdriver-object-as-thread.html
 *
 * !Important: You have to initialize your Webdriver object local to the method
 * or block only. Do not initialize it as a field or global object.
 *
 * @author nikprixmar
 */
public class SeleniumDataFactory implements BrowserDriver {

    // Logger
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SeleniumDataFactory.class);

    private static String driverName;

    private static final String FIREFOX_FIRESHOT_ADDON = "full_web_page_screenshots-0.98.89-sm+fx+tb-windows.xpi";
    public static final String FF_PREFIX = "full_web_page_screenshots-0.98.89-sm+fx+tb-windows";
    public static final String FF_SUFFIX = ".xpi";

    private static final String CHROME_FIRESHOT_ADDON = "Take-Webpage-Screenshots-Entirely-FireShot_v0.98.91.crx";
    public static final String CHROME_PREFIX = "Take-Webpage-Screenshots-Entirely-FireShot_v0.98.91";
    public static final String CHROME_SUFFIX = ".crx";

    private static SeleniumDataFactory instance = new SeleniumDataFactory();

    private static ProgramData programData;
    private static Map<String, String> folderPaths;

    /**
     * Default constructor. Does nothing.. Does not allow to initialize this
     * class from outside.
     */
    private SeleniumDataFactory() {
    }

    /**
     * Returns factory instance.
     *
     * @param driverName
     * @param programData
     * @return
     */
    public static SeleniumDataFactory getInstance(String driverName, ProgramData programData) {
        // initializing some fields
        SeleniumDataFactory.driverName = driverName;
        SeleniumDataFactory.programData = programData;
        SeleniumDataFactory.folderPaths = new HashMap<>();
        // returning new instance
        return instance;
    }

    /**
     * Thread local driver object for webdriver.
     */
    ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>() {
        @Override
        protected WebDriver initialValue() {

            // creating folder's structure on host's machine
            createFolders();

            switch (driverName) {
                case "firefox":
                    // no need to download Firefox binary since we are using 
                    // Maven dependency: https://github.com/bonigarcia/webdrivermanager
                    // full topic here: http://stackoverflow.com/questions/7450416/selenium-2-chrome-driver
                    FirefoxDriverManager.getInstance().setup("0.14.0");

                    // Since Fireshot API can be used on Windows only,
                    // checking user's OS and adding profile for Win users only
                    if (WebDriverUtil.OSDetector().equals("Windows")) {

                        // get curernt Firefox version
                        String currentFFVersion = getCurrentFirefoxVersion();
                        return new FirefoxDriver(createFFprofileWithAddon(currentFFVersion));
                    } else {
                        return new FirefoxDriver();
                    }

                case "chrome":
                    // no need to download Chrome binary since we are using 
                    // Maven dependency: https://github.com/bonigarcia/webdrivermanager
                    // full topic here: http://stackoverflow.com/questions/7450416/selenium-2-chrome-driver
                    ChromeDriverManager.getInstance().setup();

                    // ! Chrome sometimes does not start in full screen
                    // this fix will make it start by default
                    ChromeOptions options = new ChromeOptions();
                    //options.addArguments("--kiosk"); // this option needs more testing
                    options.addArguments("--start-maximized");
                    // set custom download folder
                    DesiredCapabilities cap = setDownloadFolderChrome(options);

                    // Since Fireshot API can be used on Windows only,
                    // checking user's OS and adding profile for Win users only
                    if (WebDriverUtil.OSDetector().equals("Windows")) {
                        try {
                            // add Fireshot extension
                            options.addExtensions(stream2file(PropertiesManager.class.getClassLoader()
                                    .getResourceAsStream(CHROME_FIRESHOT_ADDON), driverName));

                            return new ChromeDriver(cap);
                        } catch (IOException ex) {
                            log.info("There was a problem with adding Fireshot to Chrome: " + ex);
                        }
                    } else {
                        // on Mac, just run Crome with default options for full screen
                        return new ChromeDriver(cap);
                    }

                default:
                    // having default case to avoid exceptions
                    FirefoxDriverManager.getInstance().setup();
                    return new FirefoxDriver();
            }
        }
    };

    /**
     * Gets the driver object and launches the browser.
     *
     * @return
     */
    @Override
    public WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Quits the driver and closes the browser.
     */
    public void removeDriver() {
        driver.get().quit();
        driver.remove();
    }

    /**
     * Returns folderPaths, created during driver's initialization.
     *
     * @return
     */
    public Map<String, String> getFolderPaths() {
        return folderPaths;
    }

    /**
     * Creates folders, required for saving screenshots
     */
    private void createFolders() {
        // creating parent folder for screenshots:
        String screenshotsFolderPath = FolderManager.createScreensDir(
                FolderManager.getUserDesktopDirPath(), "mDADI_Screens");

        folderPaths.put("screenshotsFolderPath", screenshotsFolderPath);

        // creating specific Program's screenshots folder
        String programScreenshotsFolderPath = FolderManager.createScreensDir(screenshotsFolderPath,
                programData.getCollectionId() + "_" + programData.getPresentationId());

        folderPaths.put("programScreenshotsFolderPath", programScreenshotsFolderPath);

        // Since Fireshot can save PDFs, creating specific Program's screenshots folder for PDFs
        String programScreenshotsFolderPathPDF = FolderManager.createScreensDir(programScreenshotsFolderPath,
                programData.getCollectionId() + "_" + programData.getPresentationId() + "_" + "pdf");

        folderPaths.put("programScreenshotsFolderPathPDF", programScreenshotsFolderPathPDF);
    }

    /**
     * Creates new FF profile with addons. ref:
     * http://www.abodeqa.com/2013/08/24/adding-add-on-in-firefox-and-chrome-using-webdriver/
     * http://stackoverflow.com/a/8345807
     * http://toolsqa.com/selenium-webdriver/how-to-add-extensions-firebug-firepath-to-firefoxdriver/
     *
     * @return
     */
    private FirefoxProfile createFFprofileWithAddon(String currentFFVersion) {
        // Create new browser profile
        FirefoxProfile firefoxprofile = new FirefoxProfile();

        try {
            // add extension to profile
            firefoxprofile.addExtension(stream2file(PropertiesManager.class.getClassLoader().getResourceAsStream(FIREFOX_FIRESHOT_ADDON), driverName));
            log.info("Firefox extension was added");
        } catch (IOException ex) {
            log.info("There was a problem with adding Fireshot to Firefox: " + ex);
        }

        // this Preference fixes addon compatibility issues in FF
        firefoxprofile.setPreference("extensions.checkCompatibility." + currentFFVersion + ".0", false);
        log.info("Set 'extensions.checkCompatibility." + currentFFVersion + ".0" + " to 'false'");
        return firefoxprofile;
    }

    /**
     * Returns user's Firefox version.
     *
     * @return current version of user's Firefox browser
     */
    private static String getCurrentFirefoxVersion() {
        log.info("Getting Firefox version by separate FF instanse");

        WebDriver tempDriver = new FirefoxDriver();
        WebDriverUtil.setTimeout(1000);
        log.info("Loaded Temporary Firefox window");

        Capabilities caps = ((RemoteWebDriver) tempDriver).getCapabilities();

        log.info("Browser name: " + caps.getBrowserName());
        log.info("Current Firefox version extracted from Capabilities: " + caps.getVersion());

        String ffVersion = caps.getVersion();
        WebDriverUtil.setTimeout(1000);
        // if Capabilities can't define Browser's version - use other method below
        if (ffVersion.isEmpty() || ffVersion.equals("")) {
            // load FF support page
            tempDriver.get("about:support");
            // set little timout
            WebDriverUtil.setTimeout(2000);
            ffVersion = tempDriver.findElement(By.id("version-box")).getText();
            // return only 1st number
            ffVersion = ffVersion.substring(0, ffVersion.indexOf("."));
        }
        log.info("Firefox version extracted using Webdriver: " + ffVersion);
        // quit webdriver and close browser
        tempDriver.quit();
        // set little timout
        WebDriverUtil.setTimeout(500);
        return ffVersion;
    }

    /**
     * Set custom folder path for downloaded content in Chrome.
     *
     * @param options ChromeOptions
     */
    private DesiredCapabilities setDownloadFolderChrome(ChromeOptions options) {
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.default_directory", folderPaths.get("programScreenshotsFolderPathPDF"));
        options.setExperimentalOption("prefs", chromePrefs);

        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(ChromeOptions.CAPABILITY, options);

        return cap;
    }

    private void setDownloadFolderFirefox() {

    }

    /**
     * Converts InputStream to File.
     *
     * @param in InputStream
     * @return File object for FF/Chrome extension usage in Webdriver
     * @throws IOException
     */
    private static File stream2file(InputStream in, String browser) throws IOException {
        final File tempFile;

        if (browser.equals("firefox")) {
            tempFile = File.createTempFile(FF_PREFIX, FF_SUFFIX);
        } else {
            // means we are using Chrome
            tempFile = File.createTempFile(CHROME_PREFIX, CHROME_SUFFIX);
        }

        // remove temp file when JVM terminates
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        log.info("Temp file, which contains Fireshot extension for Firefox is: " + tempFile.getName());
        return tempFile;
    }
}
