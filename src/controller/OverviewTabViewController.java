package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;

import model.AccountManager;
import model.objects.Expense;
import model.objects.Loan;

/**
 * Created by MTs on 25/08/16.
 *
 * This is the overview tab's controller.
 */

public class OverviewTabViewController {

    @FXML private TextField tfTotal;
    @FXML private TextField tfLoans;
    @FXML private TextField tfRent;
    @FXML private TextField tfFood;
    @FXML private TextField tfExpenses;

    /**
     * When the overview is refreshed, values of all its fields are updated with changes that can have been made to
     * either expenses and/or loans.
     */

    public void refreshOverview() {
        // Variables for getting the total amount for the various text fields.
        int monthlyTotal = 0,  loanTotal = 0, rent = 0, food = 0, expenseTotal = 0;

        // Get all loans from the User's list of loans.
        ArrayList<Loan> loans = AccountManager.getCurrentUser().getLoans();

        // For each loan gotten, add interest and amortization amounts to vars. monthlyTotal and loanTotal.
        for (Loan loan : loans) {
            // The interest rate is saved as (for 1.41%) 1.41. To get the actual monthly cost, the value needs to be
            // multiplied by 0.01 so that it becomes 0.0141. Divide by 12 to get the payment amount/month.
            monthlyTotal += (loan.getAmount() * loan.getInterestRate() * 0.01) / 12;
            loanTotal += (loan.getAmount() * loan.getInterestRate() * 0.01) / 12;

            monthlyTotal += loan.getAmortizationAmount();
            loanTotal += loan.getAmortizationAmount();
        }

        // Get current user's rent cost, defaults to 0 if no rent has been added.
        if (AccountManager.getCurrentUser().getRent() != null) {
            rent = AccountManager.getCurrentUser().getRent().getAmount();
        }
        monthlyTotal += rent;

        // Get the current user's food cost, defaults to 0 if no food cost has been added.
        if (AccountManager.getCurrentUser().getRent() != null) {
            food = AccountManager.getCurrentUser().getFood().getAmount();
        }
        monthlyTotal += food;

        // Get all expenses from the user's list of expenses.
        ArrayList<Expense> expenses = AccountManager.getCurrentUser().getExpenses();

        // For each expense gotten, add amount to monthly and expense total.
        for (Expense expense : expenses) {
            // Simply add the amounts.
            monthlyTotal += expense.getAmount();
            expenseTotal += expense.getAmount();
        }

        // Set the textfields values to their corresponding calculated amounts.
        tfTotal.setText("" + monthlyTotal);
        tfLoans.setText("" + loanTotal);
        tfRent.setText("" + rent);
        tfFood.setText("" + food);
        tfExpenses.setText("" + expenseTotal);
    }
}