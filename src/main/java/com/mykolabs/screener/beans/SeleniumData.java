package com.mykolabs.screener.beans;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author nikprixmar
 */
public class SeleniumData implements BrowserDriver {

    private final String driver;

    /**
     * Initializing fields.
     *
     * @param driver
     */
    public SeleniumData(String driver) {
        this.driver = driver;
    }

    /**
     * Returns an instance of WebDriver depending from what browser was selected
     * by user.
     *
     * @param driver
     * @return
     */
    @Override
    public WebDriver getDriver() {

        WebDriver newDriver;

        switch (driver) {
            case "firefox":
                newDriver = new FirefoxDriver();
                break;
            case "chrome":
                // no need to download Chrome binary since we are using 
                // Maven dependency: https://github.com/bonigarcia/webdrivermanager
                // full topic here: http://stackoverflow.com/questions/7450416/selenium-2-chrome-driver
                ChromeDriverManager.getInstance().setup();
                newDriver = new ChromeDriver();
                break;
            default:
                newDriver = new FirefoxDriver();
        }
        return newDriver;
    }

}
