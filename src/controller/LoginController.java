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

            // Init loans here since it is going to be used at some point, might as well fix it here...
            ArrayList<Loan> loans;

            // Check if new records of previous month's expenses need to be created. This checks the time of the last
            // session against the current date.
            int shifts = TimeTracking.howManyMonthShifts();

            // If shifts is bigger then 0, then there needs to be created new records.
            if (shifts > 0) {
                System.out.println("Month shifts detected, performing record creation...");

                // Fill loans.
                loans = new SQLiteConnection().fetchLoans("SELECT * FROM Loans WHERE UserID = ?", user.getId());
                // Get expenses, only gotten in case of new shifts.
                ArrayList<Expense> expenses = new SQLiteConnection().fetchExpenses(
                        "SELECT * FROM Expenses WHERE UserID = ?", user.getId());

                // This is done once to be able to track the progress up until today's date and not add stuff that has
                // an earlier end date. Needs to be outside for loop!
                long currentDateShift = TimeTracking.getLastSessionLastDayOfMonth();

                // For every shift of month, one record needs to be inserted. For-loop iteration counter checked OK!
                for (int i = shifts; i > 0; i--) {
                    System.out.print("Iteration " + i + " of shift loop. ");
                    int loanTotal = 0, expenseTotal = 0;     // Declaring totals for this iteration.

                    // Start by adding loan values to loanTotal:
                    for (Loan loan : loans) {
                        // Loan amount might be zero, in which case do not add it. It could become zero during
                        // calculations. Also, check if the payment date has been met or passed.
                        if (currentDateShift >= loan.getNextPayment() && loan.getAmount() > 0) {
                            // Add the current iteration's loan amount to loanTotal.
                            loanTotal += intValue((loan.getAmount() * loan.getInterestRate() * 0.01) / 12) +
                                    loan.getAmortizationAmount();

                            // Adds a month to Loan
                            // TODO fix loans to also handle other update frequencies than one month...
                            loan.setNextPayment(TimeTracking.addOneMonth(loan.getNextPayment(), loan.getDayOffset()));

                            // Deducts amortization amount for next iteration of shift-loop (top level loop).
                            loan.setAmount(loan.getAmount() - loan.getAmortizationAmount());
                        }
                    }

                    for (Expense expense : expenses) {
                        // Keep adding the expenses amounts until the end date of the expense is met. Expenses with
                        // dates before 86400000 are set to "no end date" and their amounts are added all the way.
                        if (expense.getEndDate() < 86400000 || currentDateShift < expense.getEndDate()) {
                            expenseTotal += expense.getAmount();
                        }
                    }

                    Rent rent = new SQLiteConnection().fetchRent("SELECT * FROM Rent WHERE UserID = ?", user.getId());
                    if (rent == null) {
                        rent = new Rent();
                    }

                    Food food = new SQLiteConnection().fetchFood("SELECT * FROM Food WHERE UserID = ?;", user.getId());
                    if (food == null) {
                        food = new Food();
                    }

                    /*
                    ORDER OF INSERTIONS!

                     1. Loans
                     2. Expenses
                     3. Rent
                     4. Food

                     */

                    new SQLiteConnection().insertRecord(new Record(loanTotal, currentDateShift),
                            "LoanRecords", user.getId());

                    new SQLiteConnection().insertRecord(new Record(expenseTotal, currentDateShift),
                            "ExpenseRecords", user.getId());

                    new SQLiteConnection().insertRecord(new Record(rent.getAmount(), currentDateShift),
                            "RentRecords", user.getId());

                    new SQLiteConnection().insertRecord(new Record(food.getAmount(), currentDateShift),
                            "FoodRecords", user.getId());

                    LocalDate currentDate = new Date(currentDateShift).toLocalDate();   // Used for record creation
                    // print.

                    // After records created, step forward one month in time. 0 is for always save with the last day of
                    // month.
                    currentDateShift = TimeTracking.addOneMonth(currentDateShift, 0);

                    System.out.println("The month of " + currentDate.getYear() + "/" + currentDate.getMonthValue() + "/"
                            + currentDate.getDayOfMonth() + " had the following total: " + (loanTotal + expenseTotal +
                            rent.getAmount() + food.getAmount()));
                }
            }

            // Refresh loans ArrayList in case the above DB record creation needed to be performed.
            loans = new SQLiteConnection().fetchLoans("SELECT * FROM Loans WHERE UserID = ?",
                    AccountManager.getCurrentUser().getId());

            // Make sure all loans have been "paid"...
            loans.forEach(Loan::performPayments);

            // Give user current DB records, everything should now be UpToDate.
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
