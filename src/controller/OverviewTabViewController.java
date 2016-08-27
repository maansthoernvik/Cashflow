package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.AccountManager;
import model.objects.Expense;
import model.objects.Loan;

import java.util.ArrayList;

/**
 * Created by MTs on 25/08/16.
 *
 *
 */

public class OverviewTabViewController {
    @FXML private TextField tfTotal;
    @FXML private TextField tfLoans;
    @FXML private TextField tfExpenses;

    /**
     *
     */

    public void refreshOverview() {
        // Variables for temporarily saving information.
        int monthlyTotal = 0;
        int loanTotal = 0;
        int expenseTotal = 0;

        // Get all loans from the DB.
        ArrayList<Loan> loans = AccountManager.getCurrentUser().getLoans();

        // For each loan gotten, add interest and amortization amounts to vars.
        for (Loan loan : loans) {
            // The interest rate is saved as (for 1.41%) 1.41. To get the actual monthly cost, the value needs to be
            // multiplied by 0.01 so that it becomes 0.0141. Divide by 12 to get the payment amount/month.
            monthlyTotal += (loan.getAmount() * loan.getInterestRate() * 0.01) / 12;
            loanTotal += (loan.getAmount() * loan.getInterestRate() * 0.01) / 12;

            monthlyTotal += loan.getAmortizationAmount();
            loanTotal += loan.getAmortizationAmount();
        }

        // Get all expenses from the DB.
        ArrayList<Expense> expenses = AccountManager.getCurrentUser().getExpenses();

        // For each expense gotten, add amount to monthly and expense total.
        for (Expense expense : expenses) {
            // Simply get the amounts.
            monthlyTotal += expense.getAmount();
            expenseTotal += expense.getAmount();
        }

        // Set the textfields values to their corresponding calculated amounts.
        tfTotal.setText("" + monthlyTotal);
        tfLoans.setText("" + loanTotal);
        tfExpenses.setText("" + expenseTotal);
    }
}
