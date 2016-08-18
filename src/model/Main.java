package model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Created by MTs on 06/08/16.
 *
 * ZIS IS ZE MAIN CLAAZ!
 */

public class Main extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Overridden method from JavaFX 8. Starts the application with a new stage.
     *
     * @param primaryStage
     * @throws Exception
     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Create the main window.
        mainWindow();
    }

    /**
     * This method loads the main window by use of an FXML file containing the custom layout of this application. It
     * sets the scene and loads the scene into the current stage. By default, the application will be started in full-
     * screen mode.
     */

    private void mainWindow() {
        try {
            // Load the FXML file which contains the main view.
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindowView.fxml"));
            AnchorPane pane = loader.load();    // Attach crap from the FXML to a new anchor pane.

            Scene scene = new Scene(pane);      // Insert the pane into the scene.
            scene.getStylesheets().add(getClass().getResource("../style.css").toExternalForm());    // Load stylesheets.

            primaryStage.setScene(scene);       // Sets the current scene to what has been prepared.
            primaryStage.setMaximized(true);    // Set the window to start maximized.
            primaryStage.show();                // Show the finished stage.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for the primary stage.
     *
     * @return the primary stage
     */

    @SuppressWarnings("unused")
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
