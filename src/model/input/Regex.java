package model.input;

/**
 * Created by MTs on 12/08/16.
 *
 * Regular expressions for use with input validation.
 */

public enum Regex {
    NAME("^[a-zA-Z\\å\\ä\\ö0-9- ]{1,20}$"),
    AMOUNT("^[0-9]{1,10}$"),
    PERCENTAGE("^[0-9]{1,3}(\\.[0-9]{1,3})?$"),
    DATE("^(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$");

    private final String regex;

    Regex(String s) {
        regex = s;
    }

    public String getRegex() {
        return regex;
    }
}
