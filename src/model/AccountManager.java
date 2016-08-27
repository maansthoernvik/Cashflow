package model;

import controller.LoginController;
import controller.MainWindowController;
import model.objects.User;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

/**
 * Created by MTs on 19/08/16.
 *
 *
 */

public class AccountManager {

    private Scene scene;

    private static User currentUser;

    /**
     *
     * @param scene
     */

    public AccountManager(Scene scene) {
        this.scene = scene;
    }

    /**
     *
     */

    public void showLoginView() {
        try {
            // Load the FXML file which contains the login view.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));

            scene.setRoot(loader.load());

            LoginController loginController = loader.getController();
            loginController.setAccountManager(this);
            loginController.setUpAuthentication();

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

            scene.getStylesheets().add(getClass().getResource("../style.css").toExternalForm());    // Load stylesheets.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */

    public void logout() {
        showLoginView();
        currentUser = null;
    }

    /**
     *
     */

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     *
     */

    public static User getCurrentUser() {
        return currentUser;
    }
}
