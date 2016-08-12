package model.Input;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.control.TextField;

/**
 * Created by MTs on 11/08/16.
 *
 *
 */

public class ModdedTextField extends TextField {
    private final PseudoClass error = PseudoClass.getPseudoClass("error");
    private Regex regex;

    public ModdedTextField() {
        super();
        //this.getStyleClass().add("text-field");
    }

    public void setUpValidation(Regex regex) {
        this.regex = regex;
        this.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                validate();
            }
        });
    }

    private void validate() {
        if (this.getText().matches(regex.getRegex())) {
            this.pseudoClassStateChanged(error, false);
        } else {
            this.pseudoClassStateChanged(error, true);
        }
    }

    public boolean checkInput() {
        return getText().matches(regex.getRegex());
    }
}
