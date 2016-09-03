package model;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

import model.objects.User;
import controller.LoginController;
import controller.MainWindowController;

/**
 * Created by MTs on 19/08/16.
 *
 * The account manager is initialized with the start of the main class ChillBillsDesktop and keeps track of who is
 * logged in. It manages switching between the login and application view.
 */

public class AccountManager {

    private Scene scene;
    private Stage primaryStage;

    private static User currentUser;    // Static since only one user can be logged in at any point in time.

    /**
     * Saves the current scene and stage as class variables to manipulate when new content needs to be shown.
     *
     * @param scene current
     * @param primaryStage current
     */

    public AccountManager(Scene scene, Stage primaryStage) {
        this.scene = scene;
        this.primaryStage = primaryStage;
    }

    /**
     * When called, shows a small window with fields for user to provide login information.
     */

    public void showLoginView() {
        try {
            // Load the FXML file which contains the login view.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));

            scene.setRoot(loader.load());   // Set the root node of the scene to what has been loaded from the FXML.

            LoginController loginController = loader.getController();   // Get the controller class.
            loginController.setAccountManager(this);                    // Forward -this- account manager.

            scene.getStylesheets().add(getClass().getResource("../style.css").toExternalForm());    // Load stylesheets.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method loads the main window by use of an FXML file containing the custom layout of this application. It
     * sets the scene and loads the scene into the current stage. By default, the application will be started in full-
     * screen mode.
     */

    public void showMainView() {
        try {
            // Load the FXML file which contains the main view.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindowView.fxml"));

            scene.setRoot(loader.load());

            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setAccountManager(this);
            primaryStage.setMaximized(true);    // Maximize the window.

            scene.getStylesheets().add(getClass().getResource("../style.css").toExternalForm());    // Load stylesheets.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the current user and shows the login view.
     */

    public void logout() {
        showLoginView();
        currentUser = null;
    }

    /**
     * Sets the currently logged in user to the one defined by the login procedure.
     *
     * @param user returned by login function
     */

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Return the currently logged in user.
     */

    public static User getCurrentUser() {
        return currentUser;
    }
}
