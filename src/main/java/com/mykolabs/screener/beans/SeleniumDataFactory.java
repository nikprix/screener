package com.mykolabs.screener.beans;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

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

    private static String driverName;

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
                    return new FirefoxDriver();
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
}
