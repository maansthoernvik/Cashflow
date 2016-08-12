package model.Input;

/**
 * Created by MTs on 12/08/16.
 */
public enum Regex {
    NAME("^[a-zA-Z0-9- ]{1,20}$"),
    AMOUNT("^[0-9]{3,9}$"),
    PERCENTAGE("^[0-9]{1,3}(\\.[0-9]{1,3})?$");

    private final String regex;

    Regex(String s) {
        regex = s;
    }

    public String getRegex() {
        return regex;
    }
}
