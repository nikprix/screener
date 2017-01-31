package com.mykolabs.screener.controllers;

import com.mykolabs.screener.beans.Domains;
import com.mykolabs.screener.beans.ProgramData;
import com.mykolabs.screener.presentation.MainAppFX;
import com.mykolabs.screener.util.DomainListLoader;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
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

    ProgramData programDetails;

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
    @FXML // fx:id="fireFoxRadio"
    private RadioButton fireFoxRadio;
    @FXML // fx:id="chromeRadio"
    private RadioButton chromeRadio;
    @FXML // fx:id="startScreeningButton"
    private Button startScreeningButton;
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
    }

    /**
     * This method is automatically called after the fxml file has been loaded.
     * Useful if a control must be dynamically configured such as loading data
     * into a table. In this code all it does is log that it has been called.
     */
    @FXML
    private void initialize() {
        log.info("Initializing of ScreenerFXMLController started");

        // setting listener to GetPages button to enable it only if 
        // Domain is selected and collection / presentation ids are entered
        this.enableGetPagesButton();
        // setting listener to StartScreening button to enable it only if 
        // Domain / collection / presentation ids, one item from Pages
        // and browser are populated/selected
        this.enableStartScreeningButton();

        // populating domains choice list drop down with list of domains
        // from domains.csv file.
        domainChoiceList.getItems().addAll(DomainListLoader.getDomains());

        // making pagesSelectList to accept multiple selection
        pagesSelectList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // printing selected domain's URL to log
        domainChoiceList.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<Domains>() {
                    @Override
                    public void changed(ObservableValue observable,
                            Domains oldValue, Domains newValue) {
                        log.info("Selected URL: " + newValue.getUrl());
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
    void exitProgram(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void firefoxRadioSelected(ActionEvent event) {

    }

    @FXML
    void loadAdvancedWindow(ActionEvent event) {

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

        // populating ListView<String> pagesSelectList with ArrayList of pages,
        // retrieved from the request.
        pagesSelectList.setItems(FXCollections.observableArrayList(programDetails.getProgramPagesIds()));
    }

    @FXML
    void takeScreenshots(ActionEvent event) {

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
     * Enables GetPagesButton only when there are values in 2 text fields and
     * Domain is selected.
     */
    private void enableGetPagesButton() {
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

    private void enableStartScreeningButton() {

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
                        booleanBindingOfPagesOptions);

        startScreeningButton.disableProperty().bind(booleanBinding);

    }

}
