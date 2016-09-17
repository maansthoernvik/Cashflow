package controller;

import javafx.fxml.FXML;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import model.AccountManager;
import model.objects.Expense;
import model.objects.Food;
import model.objects.Loan;
import model.objects.Rent;
import model.time.TimeTracking;

import java.util.ArrayList;

import static oracle.jrockit.jfr.events.Bits.intValue;

/**
 * Created by MTs on 06/08/16.
 *
 * This is the main window's controller that has access to all other controllers of its separate tab views since those
 * tabs have been injected using the fx:include function.
 */

public class MainWindowController {

    private AccountManager accountManager;

    @FXML private TabPane tabPane;

    @FXML private Tab overview;
    @FXML private Tab loans;
    @FXML private Tab expenses;

    @FXML private OverviewTabViewController overviewTabViewController;
    @FXML private LoanTabViewController loanTabViewController;
    @FXML private ExpenseTabViewController expenseTabViewController;

    /**
     * FFU
     *
     * @param accountManager current
     */

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * Naming causes automatic call of this method. Sets all fields of the overview to their respective values.
     */

    @SuppressWarnings("unused")
    public void initialize() {
        ArrayList<Loan> loans = new SQLiteConnection().fetchLoans("SELECT * FROM Loans WHERE UserID = ?",
                AccountManager.getCurrentUser().getId());

        // Check if new records of previous month's expenses need to be created. This checks the time of the last
        // session against the current date.
        int shifts = TimeTracking.howManyMonthShifts();

        // If shifts is bigger then 0, then there needs to be created new records.
        if (shifts > 0) {
            // This is done once to be able to track the progress up until today's date and not add stuff that has an
            // earlier end date.
            long currentDateShift = TimeTracking.getLastSession();

            // For every shift of month, one record needs to be inserted.
            for (int i = shifts; i > 0; i--) {
                int total = 0, loanTotal = 0, expenseTotal = 0;     // Declaring totals for this iteration.

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

                ArrayList<Expense> expenses = new SQLiteConnection().fetchExpenses("SELECT * FROM Expenses WHERE UserID = ?",
                        AccountManager.getCurrentUser().getId());

                for (Expense expense : expenses) {

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
            }

        }

        // Make sure all loans have been "paid"...
        loans.forEach(Loan::performPayments);

        // Give user current DB records, everything should now be UTD.
        AccountManager.getCurrentUser().populateUserFields();

        // Load initial values into overview fields.
        overviewTabViewController.refreshOverview();

        tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable,
                                                                            Tab oldTab, Tab newTab) -> {
            // If the selected tab is the overview, refresh the table's content. This is needed since changes can have
            // been made to expenses and/or loans and those changes do not themselves trigger a refresh.
            if (newTab == overview) {
                overviewTabViewController.refreshOverview();
            }
        });
    }
}