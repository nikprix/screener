package com.mykolabs.screener.presentation;

import com.mykolabs.screener.controllers.ScreenerFXMLController;
import static javafx.application.Application.launch;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * This class starts JavaFX application.
 *
 * @author nikprix
 */
public class MainAppFX extends Application {

    // Logger for output.
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // The primary window or frame of this application
    private Stage primaryStage;

    /**
     * Default constructor.
     */
    public MainAppFX() {
        super();
    }

    /**
     * The application starts here.
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Program loads here");

        // The primaryStage comes from the framework so we need to have a reference to it
        this.primaryStage = primaryStage;
        // Create the Scene and put it on the Stage
        loadMainScreenWindow();

        // Set the window title
        primaryStage.setTitle(ResourceBundle.getBundle("MessagesBundle").getString("title"));
        // Raise the curtain on the Stage
        primaryStage.show();
    }

    /**
     * Loads Screener FXML layout. Creates the Scene and puts it on the Stage
     *
     * Using this approach allows to use loader.getController() to get a
     * reference to the fxml's controller should we need to pass data to it.
     */
    private void loadMainScreenWindow() {

        try {
            // Instantiate the FXMLLoader
            FXMLLoader loader = new FXMLLoader();
            // Set the location of the fxml file in the FXMLLoader
            loader.setLocation(MainAppFX.class.getResource("/fxml/MainScreen.fxml"));

            // Parent is the base class for all nodes that have children in the
            // scene graph such as AnchorPane and most other containers
            Parent parent = (AnchorPane) loader.load();

            // Load the parent into a Scene
            Scene scene = new Scene(parent);

            // Put the Scene on Stage
            primaryStage.setScene(scene);

            // Give the ScreenerFXMLController controller access to the main app.
            ScreenerFXMLController controller = loader.getController();
            controller.setMainAppFX(this);

        } catch (IOException ex) { // getting resources or files could fail
            log.error(null, ex);
            System.exit(1);
        }
    }

    /**
     * Where it all begins.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        System.exit(0);
    }
}
