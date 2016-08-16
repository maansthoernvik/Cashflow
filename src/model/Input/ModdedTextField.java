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

    public ModdedTextField(Regex regex) {
        super();
        setUpValidation(regex);
    }

    private void setUpValidation(Regex regex) {
        this.regex = regex;
        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                validate();
            }
        });
    }

    public boolean validate() {
        if (getText().matches(regex.getRegex())) {
            pseudoClassStateChanged(error, false);

            return true;
        } else {
            pseudoClassStateChanged(error, true);

            return false;
        }
    }

    public void reset() {
        clear();
        pseudoClassStateChanged(error, false);
    }
}
