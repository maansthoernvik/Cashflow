package model.input;

import javafx.css.PseudoClass;
import javafx.scene.control.DatePicker;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Created by MTs on 14/08/16.
 *
 * Modified DatePicker class that now includes setting the color of the DatePickers borders, depending on what input is
 * given. Validation ensures, before save/update, that the input has integrity so that no crap gets submitted into the
 * DB.
 */

public class ModdedDatePicker extends DatePicker {

    private final PseudoClass error = PseudoClass.getPseudoClass("error");  // PseudoClass state is changed to show
    private Regex regex;                                                    // that there is an error.

    /**
     * Constructor takes a regular expression as input to define what input the DatePicker will accept. In this case,
     * Regex.DATE should be used.
     */

    public ModdedDatePicker() {
        super();
    }

    /**
     * Sets the validation if this DatePicker to check against the Regex expression used as a parameter in the
     * constructor.
     *
     * @param regex regular expression to be used for validation
     */

    public void setUpValidation(Regex regex) {
        this.regex = regex;     // Regex is saved to object variable.
        valueProperty().addListener((observable, oldValue, newValue) -> {
            validate();     // When DatePicker value is changed, perform validate() method.
        });
    }

    /**
     * Validates what has been input into the DatePicker.
     *
     * @return true if input is in accordance to the Regex or null
     */

    public boolean validate() {
        Calendar cal = Calendar.getInstance();  // Gets the current date.
        // If the value is null, its OK that just means that there is no set date, return true and set error to false.
        if (getValue() == null) {
            pseudoClassStateChanged(error, false);

            return true;
            // If the date exists but is set to before todays date, return false, that shit's bad.
        } else if (getValue().isBefore(new Date(cal.getTimeInMillis()).toLocalDate())) {
            pseudoClassStateChanged(error, true);

            return false;
            // matching regex? Well of course come on through!
        } else if (getValue().toString().matches(regex.getRegex())) {
            pseudoClassStateChanged(error, false);

            return true;
            // In any other case, what the actual fuck was entered. FALSE!
        } else {
            pseudoClassStateChanged(error, true);
            return false;
        }
    }

    /**
     * Resets the DatePicker, sets value to default and removes error color by deactivating the PseudoClass error.
     */

    public void reset() {
        setValue(null);
        pseudoClassStateChanged(error, false);
    }
}
