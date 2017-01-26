package com.mykolabs.screener.controllers;

import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample Skeleton for 'MainScreen.fxml' Controller Class
 * 
 * @author nikprix
 */

public class screenerFXMLController {

    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // The @FXML annotation on a class variable results in the matching
    // reference being injected into the variable
    // label is defined in the fxml file
    // This is the value injected by FXMLLoader
    @FXML // fx:id="collectionIdField"
    private TextField collectionIdField;
    @FXML // fx:id="presentationIdField"
    private TextField presentationIdField;
    @FXML // fx:id="allPagesCheckBox"
    private CheckBox allPagesCheckBox;
    @FXML // fx:id="pagesSelectList"
    private ListView<?> pagesSelectList;
    @FXML // fx:id="singlePageIdField"
    private TextField singlePageIdField;
    @FXML // fx:id="fireFoxRadio"
    private RadioButton fireFoxRadio;
    @FXML // fx:id="chromeRadio"
    private RadioButton chromeRadio;
    // resources were from the FXMLLoader
    @FXML
    private ResourceBundle resources;

    /**
     * This method is automatically called after the fxml file has been loaded.
     * Useful if a control must be dynamically configured such as loading data
     * into a table. In this code all it does is log that it has been called.
     */
    @FXML
    private void initialize() {
        log.info("initialize called");
    }
    
     @FXML
    void allPagesCheckBoxChecked(ActionEvent event) {

    }

    @FXML
    void chromeRadioSelected(ActionEvent event) {

    }

    @FXML
    void exitProgram(ActionEvent event) {

    }

    @FXML
    void firefoxRadioSelected(ActionEvent event) {

    }

    @FXML
    void loadAdvancedWindow(ActionEvent event) {

    }

    @FXML
    void retrieveAllPages(ActionEvent event) {

    }

    @FXML
    void takeScreenshots(ActionEvent event) {

    }
    
    
}

