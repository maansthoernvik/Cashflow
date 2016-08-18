package model.Input;

import javafx.css.PseudoClass;
import javafx.scene.control.TextField;

/**
 * Created by MTs on 11/08/16.
 *
 * Modified TextField class that now includes setting the color of the TextFields borders, depending on what input is
 * given. Validation ensures, before save/update, that the input has integrity so that no crap gets submitted into the
 * DB.
 */

public class ModdedTextField extends TextField {

    private final PseudoClass error = PseudoClass.getPseudoClass("error");      // PseudoClass state is changed to show
    private Regex regex;                                                        // that there is an error.

    /**
     * Constructor takes a regular expression as input to define what input the DatePicker will accept. In this case,
     * Regex.AMOUNT/NAME/LESSERAMOUNT etc. should be used.
     *
     * @param regex input format accepted
     */

    public ModdedTextField(Regex regex) {
        super();
        setUpValidation(regex); // Sets what should happen upon input entered.
    }

    /**
     * Sets the validation if this TextField to check against the Regex expression used as a parameter in the
     * constructor.
     *
     * @param regex regular expression to be used for validation
     */

    private void setUpValidation(Regex regex) {
        this.regex = regex;     // Regex is saved to object variable.
        textProperty().addListener((observable, oldValue, newValue) -> {
            validate();     // When TextField value is changed, perform validate() method.
        });
    }

    /**
     * Validates what has been input into the TextField.
     *
     * @return true if input is in accordance to the Regex
     */

    public boolean validate() {
        if (getText().matches(regex.getRegex())) {
            pseudoClassStateChanged(error, false);

            return true;
        } else {
            pseudoClassStateChanged(error, true);

            return false;
        }
    }

    /**
     * Resets the TextField, sets value to default and removes error color by deactivating the PseudoClass error.
     */

    public void reset() {
        clear();
        pseudoClassStateChanged(error, false);
    }
}
