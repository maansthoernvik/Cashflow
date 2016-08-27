package testing.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Created by MTs on 25/08/16.
 *
 */

public class FooTabPageController {
    @FXML private Label lblText;

    public void handleButton() {
        lblText.setText("Byebye!");
    }
}
