package testing.controller;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Created by MTs on 25/08/16.
 *
 */

public class MainTestController {

    @FXML private TabPane tabPane;
    // Inject tab content.
    @FXML private Tab fooTab;
    // Inject controller
    @FXML private FooTabPageController fooTabPageController;

    // Inject tab content.
    @FXML private Tab barTab;
    // Inject controller
    @FXML private BarTabPageController barTabPageController;

    public void init() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable,
                                                                        Tab oldValue, Tab newValue) -> {
            if (newValue == barTab) {
                System.out.println("Bar Tab page");
                barTabPageController.handleButton();
            } else if (newValue == fooTab) {
                System.out.println("Foo Tab page");
                fooTabPageController.handleButton();
            }
        });
    }
}
