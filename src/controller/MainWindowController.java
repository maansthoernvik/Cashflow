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

        // Check if new records of previous month's expenses need to be created.
        int shifts = TimeTracking.howManyMonthShifts(TimeTracking.getLastSession(), TimeTracking.getCurrentDate());

        // If new records need to be created, then all expenses need to be gotten first to create correct input.
        if (shifts > 0) {
            ArrayList<Expense> expenses = new SQLiteConnection().fetchExpenses("SELECT * FROM Expenses WHERE UserID = ?",
                    AccountManager.getCurrentUser().getId());

            Rent rent = new SQLiteConnection().fetchRent("SELECT * FROM Rent WHERE UserID = ?" ,
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