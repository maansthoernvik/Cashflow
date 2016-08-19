package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;

/**
 * Created by MTs on 19/08/16.
 *
 *
 */

public class LoginController {

    @FXML private TextField tfLogin;
    @FXML private PasswordField pwfLogin;
    @FXML private Button btnLogin;

    /**
     *
     * @param accManager
     * @return
     */

    public void setUpAuthentication(AccountManager accManager) {
        btnLogin.setOnMouseReleased( releaseEvent -> {
            User user = authenticate();

            if (user.getId() > 0) {
                accManager.setCurrentUser(user);
                accManager.showMainView();
            }
        });
    }

    /**
     *
     * @return
     */

    private User authenticate() {
        SQLiteConnection SQLiteConn = new SQLiteConnection();

        return SQLiteConn.fetchUser("SELECT UserID, Username FROM Users WHERE Username = ? AND Password = ?",
                tfLogin.getText(), pwfLogin.getText());
    }
}
