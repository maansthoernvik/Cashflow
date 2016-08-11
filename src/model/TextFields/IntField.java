package model.TextFields;

import javafx.scene.control.TextField;

/**
 * Created by MTs on 11/08/16.
 */

public class IntField extends TextField {

    private static final String regex = "^[0-9]{3,9}$";

    public IntField() {
        super();
        setOnKeyTyped( keyTyped -> {
            if (getText().matches(regex)) {

            } else {

            }
        });
    }

    public boolean checkValidity() {
        return getText().matches(regex);
    }
}
