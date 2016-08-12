package controller;

/**
 * Created by MTs on 09/08/16.
 *
 *
 */

public class InputChecker {

    public static void main(String[] args) {

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
