package model.Input;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;

import java.time.LocalDate;

/**
 * Created by MTs on 14/08/16.
 *
 *
 */
public class ModdedDatePicker extends DatePicker {
    private final PseudoClass error = PseudoClass.getPseudoClass("error");
    private Regex regex;

    public ModdedDatePicker(Regex regex) {
        super();
        setUpValidation(regex);
    }

    private void setUpValidation(Regex regex) {
        this.regex = regex;
        valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                validate();
            }
        });
    }

    public boolean validate() {
        if (getValue() == null) {
            pseudoClassStateChanged(error, false);

            return true;
        }

        if (getValue().toString().matches(regex.getRegex())) {
            pseudoClassStateChanged(error, false);

            return true;
        } else {
            pseudoClassStateChanged(error, true);

            return false;
        }
    }

    public void reset() {
        pseudoClassStateChanged(error, false);
    }
}
