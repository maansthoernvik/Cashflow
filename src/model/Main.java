package model;

import controller.AccountManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by MTs on 06/08/16.
 *
 * ZIS IS ZE MAIN CLAAZ!
 */

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Overridden method from JavaFX 8. Starts the application with a new stage. Loads the account manager to show the
     * login view.
     *
     * @param primaryStage
     * @throws Exception
     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new StackPane());

        AccountManager accManager = new AccountManager(scene);
        accManager.showLoginView();

        primaryStage.setScene(scene);       // Sets the current scene to what has been prepared.
        primaryStage.setMaximized(true);    // Set the window to start maximized.
        primaryStage.show();                // Show the finished stage.
    }
}
