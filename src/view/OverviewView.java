package view;

import controller.AccountManager;
import controller.SQLiteConnection;
import model.Expense;
import model.Loan;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * Created by MTs on 06/08/16.
 *
 * From here the user can see his or her current economic status with various helpful tools and stuff to provide
 * statistics.
 */

public class OverviewView extends VBox {

    /**
     * Default constructor for OverviewViews, populating the VBox with all items the view contains.
     */

    public OverviewView() {
        super();

        // Connection object for use with the SQLite database.
        SQLiteConnection SQLiteConn = new SQLiteConnection();

        // Textfields to contain bill information of various kinds.
        TextField tfMonthlyBills = new TextField();
        TextField tfLoans = new TextField();
        TextField tfExpenses = new TextField();

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

            monthlyTotal += (loan.getAmount() * loan.getAmortizationRate() * 0.01) / 12;
            loanTotal += (loan.getAmount() * loan.getAmortizationRate() * 0.01) / 12;
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
        tfMonthlyBills.setText("" + monthlyTotal);
        tfLoans.setText("" + loanTotal);
        tfExpenses.setText("" + expenseTotal);

        // Creates HBoxes for different rows of this class (since >this< extends the VBox class) and then it is simply
        // a matter of adding them all in order - hence the naming using numbers. Labels are added directly since there
        // was no reason to instantiate them anywhere else.
        HBox hbFirst = new HBox();
        hbFirst.getChildren().addAll(tfMonthlyBills, new Label("crowns per month"));

        HBox hbSecond = new HBox();
        hbSecond.getChildren().addAll(new Label("Loans:"), new Label("Expenses:"));

        HBox hbThird = new HBox();
        hbThird.getChildren().addAll(tfLoans, new Label("crowns per month"), tfExpenses, new Label("crowns per month"));

        // Adding all of the above HBoxes to >this< VBox.
        this.getChildren().addAll(new Label("Total"), hbFirst, hbSecond, hbThird);
    }
}
