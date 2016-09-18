package controller;

import model.AccountManager;
import model.objects.*;
import model.time.TimeTracking;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import static oracle.jrockit.jfr.events.Bits.intValue;

/**
 * Created by MTs on 19/08/16.
 *
 * The controller for the login view, handling the login function of the application.
 */

public class LoginController {

    // Injected FXML fields.
    @FXML private TextField tfLogin;
    @FXML private PasswordField pwfLogin;

    private AccountManager accountManager;

    /**
     * Sets the account manager of this session. This is needed for the login to function.
     */

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * Automatically called method upon initialization of the controller. All methods called initialize will be
     * auto-called.
     */

    @SuppressWarnings("unused")
    public void initialize() {
        tfLogin.setText("alpha");                       // This is only here for debugging/during incipient development
        pwfLogin.setText("opq531");                     // to ease access to the application.
    }

    /**
     * Handler for the login button.
     */

    public void handleLogin() {
        User user = authenticate();                 // Authentication returns a matching user of what has been entered
                                                    // into login fields.
        // Fetch User will return null upon error.
        if (user != null) {
            accountManager.setCurrentUser(user);    // Set the current user of the account manager to the logged in
                                                    // user.

            ArrayList<Loan> loans;

            // Check if new records of previous month's expenses need to be created. This checks the time of the last
            // session against the current date.
            int shifts = TimeTracking.howManyMonthShifts();

            // If shifts is bigger then 0, then there needs to be created new records.
            if (shifts > 0) {

                loans = new SQLiteConnection().fetchLoans("SELECT * FROM Loans WHERE UserID = ?",
                        AccountManager.getCurrentUser().getId());
                ArrayList<Expense> expenses = new SQLiteConnection().fetchExpenses(
                        "SELECT * FROM Expenses WHERE UserID = ?", AccountManager.getCurrentUser().getId());

                // This is done once to be able to track the progress up until today's date and not add stuff that has
                // an earlier end date.
                long currentDateShift = TimeTracking.getLastSessionFirstDayOfMonth();

                // For every shift of month, one record needs to be inserted.
                for (int i = shifts; i > 0; i--) {
                    System.out.print("Iteration " + i + " of shift loop.");
                    int total, loanTotal = 0, expenseTotal = 0;     // Declaring totals for this iteration.

                    // Start by adding loan values to loanTotal:
                    for (Loan loan : loans) {
                        // Loan amount might be zero, in which case do not add it.
                        if (loan.getAmount() > 0) {
                            // Add the loans amounts to total.
                            loanTotal += intValue((loan.getAmount() * loan.getInterestRate()) +
                                    loan.getAmortizationAmount());
                            // Deducts amortization amount for next iteration of shift-loop (top level loop).
                            loan.setAmount(loan.getAmount() - loan.getAmortizationAmount());
                        }
                    }

                    for (Expense expense : expenses) {
                        if (expense.getEndDate() > currentDateShift) {
                            expenseTotal += expense.getAmount();
                        }
                    }

                    Rent rent = new SQLiteConnection().fetchRent("SELECT * FROM Rent WHERE UserID = ?",
                            AccountManager.getCurrentUser().getId());
                    if (rent == null) {
                        rent = new Rent();
                    }

                    Food food = new SQLiteConnection().fetchFood("SELECT * FROM Food WHERE UserID = ?;",
                            AccountManager.getCurrentUser().getId());
                    if (food == null) {
                        food = new Food();
                    }

                    // Add totals to total variable, this amount will be saved.
                    total = loanTotal + expenseTotal + rent.getAmount() + food.getAmount();

                    // Step forward one month in time. -1 is for "no specific dayOffset".
                    LocalDate currentDate = new Date(currentDateShift).toLocalDate();   // Used for record creation.
                    currentDateShift = TimeTracking.addOneMonth(currentDateShift, -1);

                    // TODO something with the total besides printing it.
                    System.out.println("The month of " + currentDate.getYear() + "-" + currentDate.getMonthValue() +
                            " had the following total: " + total);

                }

            }

            // Refresh loans arraylist in case the above DB record creation needed to be performed.
            loans = new SQLiteConnection().fetchLoans("SELECT * FROM Loans WHERE UserID = ?",
                    AccountManager.getCurrentUser().getId());

            // Make sure all loans have been "paid"...
            loans.forEach(Loan::performPayments);

            // Give user current DB records, everything should now be UTD.
            AccountManager.getCurrentUser().populateUserFields();

            accountManager.showMainView();
        }
    }

    /**
     * Checks what has been entered into username and password field against records in the database.
     *
     * @return user if one can be matched
     */

    private User authenticate() {
        SQLiteConnection SQLiteConn = new SQLiteConnection();

        User result;
        result = SQLiteConn.fetchUser("SELECT UserID, Username FROM Users WHERE Username = ? AND Password = ?",
                tfLogin.getText(), pwfLogin.getText());

        return result;
    }
}
