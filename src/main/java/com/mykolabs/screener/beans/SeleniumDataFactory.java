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
    private static final String CHROME_FIRESHOT_ADDON = "Take-Webpage-Screenshots-Entirely-FireShot_v0.98.91.crx";
    public static final String FF_PREFIX = "full_web_page_screenshots-0.98.89-sm+fx+tb-windows";
    public static final String FF_SUFFIX = ".xpi";

    private static SeleniumDataFactory instance = new SeleniumDataFactory();

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
     * @return
     */
    public static SeleniumDataFactory getInstance(String driverName) {
        SeleniumDataFactory.driverName = driverName;
        return instance;
    }

    /**
     * Thread local driver object for webdriver.
     */
    ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>() {
        @Override
        protected WebDriver initialValue() {
            switch (driverName) {
                case "firefox":
                    // no need to download Firefox binary since we are using 
                    // Maven dependency: https://github.com/bonigarcia/webdrivermanager
                    // full topic here: http://stackoverflow.com/questions/7450416/selenium-2-chrome-driver
                    FirefoxDriverManager.getInstance().setup();
                    return new FirefoxDriver(createFFprofileWithAddon());
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

                    return new ChromeDriver(options);
                default:
                    // having default case to avoid exceptions
                    FirefoxDriverManager.getInstance().setup();
                    return new FirefoxDriver(createFFprofileWithAddon());
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
     * Creates new FF profile with addons. ref:
     * http://www.abodeqa.com/2013/08/24/adding-add-on-in-firefox-and-chrome-using-webdriver/
     * http://stackoverflow.com/a/8345807
     * http://toolsqa.com/selenium-webdriver/how-to-add-extensions-firebug-firepath-to-firefoxdriver/
     *
     * @return
     */
    private FirefoxProfile createFFprofileWithAddon() {
        // Create new browser profile
        FirefoxProfile firefoxprofile = new FirefoxProfile();

        try {
            // add extension to profile
            firefoxprofile.addExtension(stream2file(PropertiesManager.class.getClassLoader().getResourceAsStream(FIREFOX_FIRESHOT_ADDON)));
        } catch (IOException ex) {
            Logger.getLogger(SeleniumDataFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // this Preference fixes addon compatibility issues in FF
        firefoxprofile.setPreference("extensions.checkCompatibility." + getCurrentFirefoxVersion() + ".0", false);

        return firefoxprofile;
    }

    /**
     * Returns user's Firefox version.
     *
     * @return
     */
    private static String getCurrentFirefoxVersion() {
        WebDriver tempDriver = new FirefoxDriver();
        Capabilities caps = ((RemoteWebDriver) tempDriver).getCapabilities();
        
        log.info("Browser name: " + caps.getBrowserName());
        log.info("Current Firefox version: " + caps.getVersion());
                
        String ffVersion = caps.getVersion();
        
        // if Capabilities can't define Browser's version - use other method below
        if(ffVersion.isEmpty()){
            // load FF support page
            tempDriver.get("about:support");
            // set little timout
            WebDriverUtil.setTimeout(1000);
            ffVersion = tempDriver.findElement(By.id("version-box")).getText();
            // return only 1st number
            ffVersion = ffVersion.substring(0, ffVersion.indexOf(".")); 
        }
        
        // quit webdriver and close browser
        tempDriver.quit();
        return ffVersion;
    }

    /**
     * Converts InputStream to File.
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile(FF_PREFIX, FF_SUFFIX);
        // remove temp file when JVM terminates
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }
}
