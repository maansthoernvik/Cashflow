package controller;

import java.time.LocalDate;
import java.util.Calendar;

/**
 * Created by MTs on 09/08/16.
 *
 *
 */

public class InputChecker {

    public static void main(String[] args) {

        // Date filter!
        System.out.println("\nDates:");
        String date = "9/18/2016";
        String notDate = "111/10/2015";

        System.out.println(date.matches("^([1-9]|1[012])/([1-9]|[12][0-9]|3[01])/(19|20)\\d\\d$"));
        System.out.println(notDate.matches("^([1-9]|1[012])/([1-9]|[12][0-9]|3[01])/(19|20)\\d\\d$"));

        // Numeric only filter!
        System.out.println("\nNumeric:");
        String numeric = "123456789";
        String notNumeric = "abcd56789";

        System.out.println(numeric.matches("^[0-9]{3,9}$"));
        System.out.println(notNumeric.matches("^[0-9]{3,9}$"));

        // Only floating point numbers
        System.out.println("\nText: ");
        String floating = "0.0141";
        String notFloating = "1";

        System.out.println(floating.matches("^$"));
        System.out.println(notFloating.matches("^$"));

        // Only text
        System.out.println("\nText: ");
        String text = "Skandia";
        String notText = "Ca$hbank";

        System.out.println(text.matches("^[a-zA-Z0-9- ]{1,20}$"));
        System.out.println(notText.matches("^[a-zA-Z0-9-]{1,20}$"));

        // Only emails
        System.out.println("\nEmail:");
        String email = "mans@mans.se";
        String notEmail = "mans@mans!com";

        System.out.println(email.matches("^([a-z0-9_.-]+)@([a-z0-9_.-]+)\\.([a-z.]{2,6})$"));
        System.out.println(notEmail.matches("^([a-z0-9_.-]+)@([a-z0-9_.-]+)\\.([a-z.]{2,6})$"));
    }

}
