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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;

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
     * Loads alert on the screen with desired info.
     *
     * @param type
     * @param message
     * @param flagForValue
     * @param value
     */
    public void getAlert(Alert.AlertType type, String message, int flagForValue, String value) {
        Alert alert = new Alert(type, "", ButtonType.CLOSE);
        // alert.initOwner(mainApp.getPrimaryStage());
        // remove the icon and use only minimal window decorations.
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setHeaderText(null);

        if (flagForValue == 1) {
            alert.setContentText(message + value);
        } else if (flagForValue == 0) {
            alert.setContentText(message);
        }

        // Adding inline CSS to the Alert pop up // http://stackoverflow.com/a/26811135
        DialogPane dialogPane = alert.getDialogPane();

        // root
        if (type.equals(Alert.AlertType.WARNING)) {
            dialogPane.setStyle("-fx-background-color: #C53E2F;");
        } else {
            dialogPane.setStyle("-fx-background-color: #edeef0;");
        }

        // 1. Grid
        // remove style to customize header
        if (type.equals(Alert.AlertType.INFORMATION)) {
            dialogPane.getStyleClass().remove("alert");
        }

        // custom icon
//        StackPane stackPane = new StackPane(new ImageView(new Image(getClass().getResourceAsStream("myicon.png"))));
//        stackPane.setPrefSize(24, 24);
//        stackPane.setAlignment(Pos.CENTER);
//        dialogPane.setGraphic(stackPane);
        // 2. ContentText with just a Label
        dialogPane.lookup(".content.label").setStyle("-fx-font-size: 16px; "
                + "-fx-font-weight: bold; ");

        // 3- ButtonBar
        ButtonBar buttonBar = (ButtonBar) alert.getDialogPane().lookup(".button-bar");
        buttonBar.setStyle("-fx-font-size: 16px;"
                + "-fx-background-color: #039ED3;");
        buttonBar.getButtons().forEach(b -> b.setStyle("-fx-font-family: \"Segoe UI\", Helvetica, Arial, sans-serif;"));

        alert.showAndWait();
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
