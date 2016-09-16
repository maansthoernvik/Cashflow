package controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import model.AccountManager;
import model.objects.User;

/**
 * Created by MTs on 19/08/16.
 *
 * The controller for the login view, handling the login function of the application.
 */

public class LoginController {

    // Injected FXML fields.
    @FXML private TextField tfLogin;
    @FXML private PasswordField pwfLogin;

    private AccountManager accountManager;

    /**
     * Sets the account manager of this session. This is needed for the login to function.
     */

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * Automatically called method upon initialization of the controller. All methods called initialize will be
     * auto-called.
     */

    @SuppressWarnings("unused")
    public void initialize() {
        tfLogin.setText("alpha");                       // This is only here for debugging/during incipient development
        pwfLogin.setText("opq531");                     // to ease access to the application.
    }

    /**
     * Handler for the login button.
     */

    public void handleLogin() {
        User user = authenticate();                 // Authentication returns a matching user of what has been entered
                                                    // into login fields.
        user.populateUserFields();
        // Fetch User will return null upon error.
        if (user != null) {
            accountManager.setCurrentUser(user);    // Set the current user of the account manager to the logged in
            accountManager.showMainView();          // user.
        }
    }

    /**
     * Checks what has been entered into username and password field against records in the database.
     *
     * @return user if one can be matched
     */

    private User authenticate() {
        SQLiteConnection SQLiteConn = new SQLiteConnection();

        User result;
        result = SQLiteConn.fetchUser("SELECT UserID, Username FROM Users WHERE Username = ? AND Password = ?",
                tfLogin.getText(), pwfLogin.getText());

        return result;
    }
}
