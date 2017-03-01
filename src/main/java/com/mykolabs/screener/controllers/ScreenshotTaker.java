package com.mykolabs.screener.controllers;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import com.mykolabs.screener.beans.ProgramData;
import com.mykolabs.screener.beans.SeleniumDataFactory;
import com.mykolabs.screener.util.FolderManager;
import com.mykolabs.screener.util.PropertiesManager;
import com.mykolabs.screener.util.WebDriverUtil;
import java.awt.image.BufferedImage;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;

import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

/**
 *
 * @author nikprixmar
 */
public class ScreenshotTaker {

    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private final static String DOMAIN_PROTOCOL = "http://";
    private final static String WWW = "www";
    private final static String FIRESHOT_API_JS = "fsapi.js";
    private final static String FIRESHOT_API_PREFIX = "fsapi";
    private final static String FIRESHOT_API_SUFFIX = ".js";

    private ProgramData programData;
    private String browser;
    private String screenshotsFolderPath = "";
    private String programScreenshotsFolderPath = "";

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
     *
     * @param option
     */
    public void takeScreenshots(String option) {

        // getting Driver instance, which can be either Firefox or Chrome
        // depending from selected Radio button
        WebDriver driver = SeleniumDataFactory.getInstance(browser).getDriver();

        // maximizing windows
        this.maximizeWindow(driver);

        // creating required folders to save screenshots into
        // this includes general folder + program folder
        this.createFolders();

        // looping through ALL selected pages IDS
        programData.getProgramPagesIds().forEach(pageId
                -> {

            log.info("Loading URL: " + this.createPageUrl(pageId));

            // Taking screenshots depending from the selected by user option and browser
            if (driver instanceof ChromeDriver) {
                switch (option) {
                    case "Fireshot":
                        loadProgram(driver, pageId);
                         {
                            try {
                                takeSingleScreenshotWithFireshot(driver, programScreenshotsFolderPath, pageId);
                            } catch (IOException ex) {
                                log.info("There was a problem with launching Fireshot: " + ex);
                            }
                        }
                        break;
                    case "Webdriver":
                        loadProgramInIframe(driver, pageId);
                        takeSingleScreenshotShutterbugChrome(driver, programScreenshotsFolderPath, pageId);
                        break;
                }

            } else {
                switch (option) {
                    case "Fireshot":
                        loadProgram(driver, pageId);
                        //takeSingleScreenshotWithFireshotFirefox(driver, programScreenshotsFolderPath, pageId);
                        break;
                    case "Native":
                        //takeSingleScreenshotWithFirefoxDevTools(driver, programScreenshotsFolderPath, pageId);
                        break;
                    case "Webdriver":
                        loadProgramInIframe(driver, pageId);
                        takeSingleScreenshotWithAshot(driver, programScreenshotsFolderPath, pageId);
                        break;
                }

            }

            // convert PNG to PDF.
        }
        );
        WebDriverUtil.setTimeout(100000000);
        // Closing browser and removing Webdriver's instance
        SeleniumDataFactory.getInstance(browser).removeDriver();
    }

    /**
     * Loads program in the selected browser.
     *
     * @param driver
     * @param pageId
     */
    private void loadProgram(WebDriver driver, String pageId) {

        // need to pause Thread completely BEFORE page load
        WebDriverUtil.setTimeout(1000);

        // loading program's page for screenshot
        driver.get(this.createPageUrl(pageId));

        // need to pause Thread completely AFTER page load
        WebDriverUtil.setTimeout(10000);

    }

    /**
     * Allows avoiding problem while making screenhsots on pages with fixed
     * elements by using iFrames.
     *
     * Options, which use it: <br>
     * - Native;<br>
     * - Webdriver;<br>
     *
     * @param driver
     * @param pageId
     */
    private void loadProgramInIframe(WebDriver driver, String pageId) {

        // need to pause Thread completely BEFORE page load
        WebDriverUtil.setTimeout(1000);

        /*NEED NEW Instance of JS Executor*/
        JavascriptExecutor js = (JavascriptExecutor) driver;

// CUSTOM RESIZER #1:
// http://stackoverflow.com/a/17714422/5971690
        String innerHtml1 = "\'<head><style type=\"text/css\">body, html{margin: 0; padding: 0; height: 100%;}#content{position:absolute; left: 0; right: 0; bottom: 0; top: 0px; }</style></head><body><div id=\"content\"><iframe id=\"iFrame1\" width=\"100%\" height=\"100%\" frameborder=\"0\" src=\"" + this.createPageUrl(pageId) + "\"></iframe></div></body>\'";

        // This trick allows to open new browser's window with simple HTML page
        // which later needs to be altered with JS
        driver.get("about:blank");

        js.executeScript("document.getElementsByTagName('html')[0].innerHTML = " + innerHtml1);

        WebDriverUtil.setTimeout(8000);

        WebElement iframe = driver.findElement(By.tagName("iframe"));
        driver.switchTo().frame(iframe);

        // retrieving HTML page's width
        Long pageWidth = (Long) js.executeScript("var width = document.documentElement.scrollWidth;return width;");
        // retrieving HTML page's max heigth
        Long pageHeigth = (Long) js.executeScript("var heigth = document.documentElement.scrollHeight;return heigth;");
        // retrieving Browser window heigth
        Long windowHeigth = (Long) js.executeScript("var heigth = $(window).height();return heigth;");

        // to avoid truncations, checking if retrieved page heigth less than 
        // window heigth
        if (pageHeigth < windowHeigth) {
            pageHeigth = windowHeigth;
        }

        WebDriverUtil.setTimeout(1000);

        log.info(pageId + " heigth: " + pageHeigth + "px");
        log.info(pageId + " width: " + pageWidth + "px");

        // This HTML contains explicit page heigth for iframe
        String innerHtml2 = "\'<head><style type=\"text/css\">#wrapper {margin: 0; padding: 0; height: 100%;} #content {position:absolute; left: 0; right: 0; bottom: 0; top: 0px;}</style></head><body id=\"wrapper\"><div id=\"content\"><iframe id=\"iFrame1\" width=\"100%\" height=\"" + pageHeigth + "\" frameborder=\"0\" src=\"" + this.createPageUrl(pageId) + "\"></iframe></div></body>\'";

        WebDriverUtil.setTimeout(2000);

        driver.get("about:blank");

        js.executeScript("document.getElementsByTagName('html')[0].innerHTML = " + innerHtml2);
        // need to pause Thread completely AFTER page load
        WebDriverUtil.setTimeout(10000);

    }

    /**
     * Takes screenshot using Fireshot's API.
     *
     * @param driver
     * @param folderPath
     * @param screenshotName
     */
    private void takeSingleScreenshotWithFireshot(WebDriver driver, String folderPath, String screenshotName) throws IOException {

        /*NEED NEW Instance of JS Executor*/
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        String script =
                "var s=window.document.createElement('script'); "
                + "s.type = 'text/javascript'; "
                + "s.text = '" + getFireshotAPIfile() + "'; "
                + "window.document.head.appendChild(s);";

        js.executeScript(script);

    }

    /**
     * Takes single page screenshot and saves to the provided folder using Ashot
     * lib. REF: https://github.com/yandex-qatools/ashot
     *
     * @param driver
     */
    private void takeSingleScreenshotWithAshot(WebDriver driver, String folderPath, String screenshotName) {

        try {
            final Screenshot screenshot = new AShot()
                    // retina support
                    .shootingStrategy(ShootingStrategies.scaling(2))
                    // capturing entire page with scrolling
                    .shootingStrategy(ShootingStrategies.viewportPasting(500))
                    .takeScreenshot(driver);
            final BufferedImage image = screenshot.getImage();

            ImageIO.write(image, "PNG", new File(folderPath + screenshotName + ".png"));

        } catch (IOException e) {
            log.error("Failed to capture screenshot: " + e.getMessage());
        }
    }

    /**
     * Takes single page screenshot by CSS id value and saves to the provided
     * folder using Ashot lib. REF: https://github.com/yandex-qatools/ashot
     *
     * @param driver
     */
    private void takeSingleScreenshotWithAshot(WebDriver driver, String folderPath, String screenshotName, String id) {

        try {
            final Screenshot screenshot = new AShot()
                    .coordsProvider(new WebDriverCoordsProvider()) //find coordinates with WebDriver API
                    .takeScreenshot(driver, driver.findElement(By.id(id)));

//            new AShot()
//                    .takeScreenshot(driver, driver.findElement(By.id(id)));
            final BufferedImage image = screenshot.getImage();

            ImageIO.write(image, "PNG", new File(folderPath + screenshotName + ".png"));

        } catch (IOException e) {
            log.error("Failed to capture screenshot: " + e.getMessage());
        }
    }

    /**
     * Takes single page screenshot and saves to the provided folder using
     * native API.
     *
     * @param driver
     */
    private void takeSingleScreenshotNativeAPI(WebDriver driver, String folderPath, String screenshotName) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            // copy to folder
            FileUtils.copyFile(scrFile, new File(folderPath + screenshotName + ".png"));
        } catch (IOException ex) {
            log.error("Failed to capture screenshot: " + ex.getMessage());
        }
    }

    /**
     * Takes single page screenshot and saves to the provided folder using
     * Shutterbug. ref:
     * https://www.assertthat.com/posts/selenium_shutterbug_make_custom_screenshots_with_selenium_webdriver
     *
     * @param driver
     */
    private void takeSingleScreenshotShutterbugChrome(WebDriver driver, String folderPath, String screenshotName) {
        Shutterbug.shootPage(driver, ScrollStrategy.VERTICALLY, 500, true).save(folderPath);
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
                .append("&SpecialtyID=18") // >>>>>>>>>>>>>>>>>>> TEST - to remove !!!!!!!!
                .append("#")
                .append(pageId)
                .toString();
    }

    /**
     * Applies some approaches to maximize Chrome and FF windows
     */
    private void maximizeWindow(WebDriver driver) {
        // maximizing window, should work for FF and Chrome
        driver.manage().window().maximize();

        // maximize Chrome on Mac
        if (driver instanceof ChromeDriver && WebDriverUtil.OSDetector().contains("Mac")) {
            WebDriverUtil.maximizeScreen(driver);
        }
    }

    /**
     * Creates folders, required for saving screenshots
     */
    private void createFolders() {
        // creating parent folder for screenshots:
        screenshotsFolderPath = FolderManager.createScreensDir(
                FolderManager.getUserDesktopDirPath(), "mDADI_Screens");

        // creating specific Program's screenshots folder
        programScreenshotsFolderPath = FolderManager.createScreensDir(screenshotsFolderPath,
                programData.getCollectionId() + "_" + programData.getPresentationId());
    }

    /**
     * Returns absolute path for the Fireshot API file.
     */
    private String getFireshotAPIfile() throws IOException {
        String FireshotAPIfile = IOUtils.toString(PropertiesManager.class.getClassLoader().getResourceAsStream(FIRESHOT_API_JS), StandardCharsets.UTF_8);

       // escaping single / double quotes / tabs / line breaks / so on
       FireshotAPIfile = escapeJS(FireshotAPIfile);

        log.info("FireShot API file with no line breaks: " + FireshotAPIfile);

        return FireshotAPIfile;
    }

    /**
     * Escapes JS.
     *
     * @param value
     * @return
     */
    public static String escapeJS(String value) {
        return StringEscapeUtils.escapeEcmaScript(value);
    }

}
