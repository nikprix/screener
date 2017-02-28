package com.mykolabs.screener.controllers;

import com.mykolabs.screener.beans.Domains;
import com.mykolabs.screener.beans.ProgramData;
import com.mykolabs.screener.beans.SeleniumDataFactory;
import com.mykolabs.screener.presentation.MainAppFX;
import com.mykolabs.screener.util.DomainListLoader;
import com.mykolabs.screener.util.WebDriverUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample Skeleton for 'MainScreen.fxml' Controller Class
 *
 * @author nikprix
 */
public class ScreenerFXMLController {

    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // Reference to the main application.
    private MainAppFX mainApp;

    private ProgramData programDetails;

    private BooleanProperty isPageIdsListHasValuesProperty;

    private String userOs;

    // The @FXML annotation on a class variable results in the matching
    // reference being injected into the variable
    // label is defined in the fxml file
    // This is the value injected by FXMLLoader
    @FXML // fx:id="domainChoiceList"
    private ChoiceBox<Domains> domainChoiceList;
    @FXML // fx:id="collectionIdField"
    private TextField collectionIdField;
    @FXML // fx:id="presentationIdField"
    private TextField presentationIdField;
    @FXML // fx:id="allPagesCheckBox"
    private CheckBox allPagesCheckBox;
    @FXML // fx:id="pagesSelectList"
    private ListView<String> pagesSelectList;
    @FXML // fx:id="singlePageIdField"
    private TextField singlePageIdField;
    @FXML // fx:id="fireShotRadio"
    private RadioButton fireShotRadio;
    @FXML // fx:id="nativeRadio"
    private RadioButton nativeRadio;
    @FXML // fx:id="seleniumRadio"
    private RadioButton seleniumRadio;
    @FXML // fx:id="fireFoxRadio"
    private RadioButton fireFoxRadio;
    @FXML // fx:id="chromeRadio"
    private RadioButton chromeRadio;
    @FXML // fx:id="startScreeningButton"
    private Button startScreeningButton;
    @FXML // fx:id="stopScreeningButton"
    private Button stopScreeningButton;
    @FXML // fx:id="getPagesButton"
    private Button getPagesButton;
    // resources were from the FXMLLoader
    @FXML
    private ResourceBundle resources;

    /**
     * The constructor. The constructor is called before the initialize()
     * method.
     */
    public ScreenerFXMLController() {
        super();
        // Create empty ProgramData object and initialize its fields later when required
        programDetails = new ProgramData();
        // init isPageIdsListHasValuesProperty;
        isPageIdsListHasValuesProperty = new SimpleBooleanProperty();
    }

    /**
     * This method is automatically called after the fxml file has been loaded.
     * Useful if a control must be dynamically configured such as loading data
     * into a table. In this code all it does is log that it has been called.
     */
    @FXML
    private void initialize() {
        log.info("Initializing of ScreenerFXMLController started");

        // at the beginning, pages List is empty, so its property as well
        isPageIdsListHasValuesProperty.set(false);

        // populating domains choice list drop down with list of domains
        // from domains.csv file.
        domainChoiceList.getItems().addAll(DomainListLoader.getInstance().getDomains());

        // making pagesSelectList to accept multiple selection
        pagesSelectList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        /* 
        Check user's OS and based on it set corresponding parameters
        screening options / browser selections for takeScreenshots() method 
         */
        userOs = WebDriverUtil.OSDetector();
        switch (userOs) {
            case "Mac":
                // FireShot API is not available on Mac
                fireShotRadio.setDisable(true);
                fireShotRadio.getStyleClass().add("disabled-radio-button");
            case "Windows":
                break;
            default:
            //throw error message;
        }

        //**************************
        //**** Listeners **** 
        //**************************
        // setting listener to GetPages button to enable it only if 
        // Domain is selected and collection / presentation ids are entered
        this.disableGetPagesButton();
        // setting listener to StartScreening button to enable it only if 
        // Domain / collection / presentation ids, one item from Pages
        // and browser are populated/selected
        this.disableStartScreeningButton();

        // need to set listener for singlePageIdField field and update
        // isPageIdsListEmptyProperty to true if page id is entered   
        singlePageIdField.textProperty().addListener((observable, oldValue, newValue)
                -> {
            if (!newValue.isEmpty()) {
                // page Ids List will have at least one value
                isPageIdsListHasValuesProperty.set(true);
            } else if (newValue.isEmpty() && programDetails.getProgramPagesIds().isEmpty()) {
                isPageIdsListHasValuesProperty.set(false);
            }
        }
        );

        // Adding listener to the Native Radio Button
        nativeRadio.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) -> {
            if (isNowSelected) {
                disableChromeRadio();
                fireFoxRadio.selectedProperty().set(true);
            } else {
                enableChromeRadio();
            }
        });

        // Adding listener to the Selenium Radio Button
        seleniumRadio.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) -> {
            // Mac specific for Selenium
            if (userOs.equals("Mac") && isNowSelected) {
               disableFirefoxRadio();
               chromeRadio.selectedProperty().set(true);
            } else {
                enableFirefoxRadio();
            }
        });

        log.info("end of initializing");
    }

    @FXML
    void allPagesCheckBoxChecked(ActionEvent event) {

    }

    @FXML
    void chromeRadioSelected(ActionEvent event) {

    }

    @FXML
    void firefoxRadioSelected(ActionEvent event) {

    }

    @FXML
    void loadAdvancedWindow(ActionEvent event) {

    }

    @FXML
    void fireShotRadioSelected(ActionEvent event) {

    }

    @FXML
    void nativeRadioSelected(ActionEvent event) {

    }

    @FXML
    void seleniumRadioSelected(ActionEvent event) {

    }

    @FXML
    void stopScreenshoting(ActionEvent event) {

    }

    @FXML
    void retrieveAllPages(ActionEvent event) {

        /* At first, initialize fields in ProgramData - programDetails object for later usage */
        programDetails.setDomainURL(domainChoiceList
                .getSelectionModel()
                .selectedItemProperty()
                .getValue()
                .getUrl());
        programDetails.setCollectionId(collectionIdField.getText());
        programDetails.setPresentationId(presentationIdField.getText());

        // creating HttpRequestController object to perform GET request
        HttpRequestController request
                = new HttpRequestController(
                        programDetails.getDomainURL(),
                        programDetails.getCollectionId(),
                        programDetails.getPresentationId());

        // add ALL retrieved pages to ProgramData - programDetails object
        programDetails.setProgramPagesIds(request.getPagesList());

        // if pagesList is not returned empty need to set isPageIdsListEmptyProperty value to true
        // to avoid StartScreening button being activated if no pages returned
        if (!programDetails.getProgramPagesIds().isEmpty()) {
            isPageIdsListHasValuesProperty.set(true);
        }

        // populating ListView<String> pagesSelectList with ArrayList of pages,
        // retrieved from the request.
        pagesSelectList.setItems(FXCollections.observableArrayList(programDetails.getProgramPagesIds()));
    }

    @FXML
    void takeScreenshots(ActionEvent event) {

        ScreenshotTaker screenshoter;

        // vaidate that only one of 3 'Pages Selection Options was selected'
        if (multiplePagesOptionsSelected() == true) {
            mainApp.getAlert(Alert.AlertType.WARNING, "Only one Pages Selection Option can be chosen!", 0, "");
            return;
        }

        // Re-populate 'programDetails' object again with final details
        this.setProgramDetailsForScreenshots();

        // get selected browser and instantiate Screenshoter:
        if (fireFoxRadio.isSelected()) {
            screenshoter
                    = ScreenshotTaker.getInstance("firefox", programDetails);
        } else {
            // means that Chrome was selected
            screenshoter
                    = ScreenshotTaker.getInstance("chrome", programDetails);
        }

        //********************************************************
        // New conditions below run different screenshoting methods
        // depending from the option being selected.
        //********************************************************
        
        if(fireShotRadio.isSelected()){
        screenshoter.takeScreenshots("Fireshot");
        } else if(nativeRadio.isSelected()) {
        screenshoter.takeScreenshots("Native");
        } else if(seleniumRadio.isSelected()){
            screenshoter.takeScreenshots("Webdriver");
        }
        

        //
        // create folder
        // make screenshots
        // save them as .pdf
        // combine screenshots in single pdf
        // open folder or provide a button to open it
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * This class receives the reference of the main class.
     *
     * @param mainApp
     */
    public void setMainAppFX(MainAppFX mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Disables GetPagesButton only when there are values in 2 text fields and
     * Domain is selected.
     */
    private void disableGetPagesButton() {
        // setting Binding to the button for 2 text fields & ChoiceBox
        BooleanBinding booleanBinding
                = collectionIdField.textProperty().isEqualTo("").or(
                presentationIdField.textProperty().isEqualTo("")).or(
                domainChoiceList.getSelectionModel().selectedItemProperty().isNull());

        getPagesButton.disableProperty().bind(booleanBinding);

        // ALTERNATIVE for 2 fields:
//        getPagesButton.disableProperty().bind(
//                Bindings.and(
//                        collectionIdField.textProperty().isEqualTo(""),
//                        presentationIdField.textProperty().isEqualTo("")));
    }

    /**
     * Disables StartScreeningButton until all required fields have values and
     * items selected.
     */
    private void disableStartScreeningButton() {

        // Binding 3 pages selection options
        BooleanBinding booleanBindingOfPagesOptions
                = singlePageIdField.textProperty().isEqualTo("").and(
                pagesSelectList.getSelectionModel().selectedItemProperty().isNull()).and(
                        allPagesCheckBox.selectedProperty().not());

        // setting Binding to the button for 2 text fields
        // (collectionIdField / presentationIdField) / ChoiceBox
        // & 'booleanBindingOfPagesOptions'
        BooleanBinding booleanBinding
                = collectionIdField.textProperty().isEqualTo("").or(
                presentationIdField.textProperty().isEqualTo("")).or(
                domainChoiceList.getSelectionModel().selectedItemProperty().isNull()).or(
                        isPageIdsListHasValuesProperty.not()).or(
                        booleanBindingOfPagesOptions);

        // disabling button until widgets above are empty or not selected
        startScreeningButton.disableProperty().bind(booleanBinding);
    }

    /**
     * Validates Pages Selection Options and returns true if multiple options
     * are selected.
     *
     * @return
     */
    private boolean multiplePagesOptionsSelected() {

        boolean selection = false;

        // checking if at least 2 options selected and setting selection to true
        if (allPagesCheckBox.isSelected() && !pagesSelectList.getSelectionModel().isEmpty()) {
            selection = true;
        }
        if (allPagesCheckBox.isSelected() && !singlePageIdField.getText().isEmpty()) {
            selection = true;
        }
        if (!singlePageIdField.getText().isEmpty() && !pagesSelectList.getSelectionModel().isEmpty()) {
            selection = true;
        }

        return selection;
    }

    /**
     * Re-populates programDetails object for starting taking screenshots.
     */
    public void setProgramDetailsForScreenshots() {
        // Re-populate 'programDetails' object again with final details
        programDetails.setDomainURL(domainChoiceList
                .getSelectionModel()
                .selectedItemProperty()
                .getValue()
                .getUrl());
        programDetails.setDomainPath(domainChoiceList
                .getSelectionModel()
                .selectedItemProperty()
                .getValue()
                .getPath());
        programDetails.setCollectionId(collectionIdField.getText());
        programDetails.setPresentationId(presentationIdField.getText());

        // pick selected 'Pages Selection Option'
        if (allPagesCheckBox.isSelected()) {
            // Do nothing since programDetails.getProgramPagesIds() already 
            // has all page ids
        } else if (!singlePageIdField.getText().isEmpty()) {
            List<String> pages = new ArrayList<>();
            pages.add(singlePageIdField.getText());
            programDetails.setProgramPagesIds(pages);
        } else if (!pagesSelectList.getSelectionModel().isEmpty()) {
            programDetails.setProgramPagesIds(pagesSelectList.getSelectionModel().getSelectedItems());
        }
    }

    /**
     * Disables Firefox radio button
     */
    private void disableFirefoxRadio() {
        fireFoxRadio.selectedProperty().set(false);
        fireFoxRadio.setDisable(true);
        fireFoxRadio.getStyleClass().add("disabled-radio-button");
    }

    /**
     * Enables Firefox radio button
     */
    private void enableFirefoxRadio() {
        fireFoxRadio.setDisable(false);
        fireFoxRadio.getStyleClass().remove("disabled-radio-button");
    }

    /**
     * Disables Chrome radio button
     */
    private void disableChromeRadio() {
        chromeRadio.selectedProperty().set(false);
        chromeRadio.setDisable(true);
        chromeRadio.getStyleClass().add("disabled-radio-button");
    }

    /**
     * Enables Chrome radio button
     */
    private void enableChromeRadio() {
        chromeRadio.setDisable(false);
        chromeRadio.getStyleClass().remove("disabled-radio-button");
    }

    /**
     * Exits program.
     *
     * @param event
     */
    @FXML
    void exitProgram(ActionEvent event) {
        Platform.exit();
    }

}
