package com.mykolabs.screener.beans;

import org.openqa.selenium.WebDriver;

/**
 * Interface for creating Webdriver instance with required browser driver.
 *
 * @author nikprixmar
 */
public interface BrowserDriver {

    //interface methods are implicitly abstract and public
    WebDriver getDriver();

}
