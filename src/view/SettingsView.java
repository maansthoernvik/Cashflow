package view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Created by MTs on 06/08/16.
 */

public class SettingsView extends VBox {

    public SettingsView() {
        super();
        this.setAlignment(Pos.TOP_LEFT);

        Label settings = new Label("Settings");

        this.getChildren().addAll(settings);
    }
}
