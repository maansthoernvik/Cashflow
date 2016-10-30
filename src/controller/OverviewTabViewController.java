package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.layout.VBox;
import model.AccountManager;
import model.objects.Expense;
import model.objects.Loan;
import model.objects.Record;

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

    @FXML private VBox vbox;

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

    /**
     * Sets up the bar chart containing expense history.
     */

    public void createChart() {
        final CategoryAxis month = new CategoryAxis();
        month.setLabel("Month");

        final NumberAxis amount = new NumberAxis();
        amount.setLabel("Amount");

        LineChart<String, Number> barChart = new LineChart<>(month, amount);
        vbox.getChildren().add(barChart);

        HashMap<String, ArrayList<Record>> records = new SQLiteConnection().fetchRecords(
                AccountManager.getCurrentUser().getId());
        ArrayList<Record> loanRecords       = records.get("LoanRecords");
        ArrayList<Record> expenseRecords    = records.get("ExpenseRecords");
        ArrayList<Record> rentRecords       = records.get("RentRecords");
        ArrayList<Record> foodRecords       = records.get("FoodRecords");

        ArrayList<Record> totals = new SQLiteConnection().fetchRecord(
                "SELECT SUM(Amount) AS Amount, Date, UserID FROM (" +
                        "SELECT Amount, Date, UserID FROM LoanRecords UNION ALL " +
                        "SELECT Amount, Date, UserID FROM ExpenseRecords UNION ALL " +
                        "SELECT Amount, Date, UserID FROM RentRecords UNION ALL " +
                        "SELECT Amount, Date, UserID From FoodRecords) " +
                        "WHERE UserID = ? GROUP BY Date;",
                AccountManager.getCurrentUser().getId());

        // 5 or size() - 1 since the index starts at 0
        int recordSize = loanRecords.size() - 1;

        // Get info max of 6 months back in time.
        XYChart.Series total    = new XYChart.Series();
        total.setName("Total");
        XYChart.Series loans    = new XYChart.Series();
        loans.setName("Loans");
        XYChart.Series expenses = new XYChart.Series();
        expenses.setName("Expenses");
        XYChart.Series rent     = new XYChart.Series();
        rent.setName("Rent");
        XYChart.Series food     = new XYChart.Series();
        food.setName("Food");

        for (int i = recordSize - 6; i <= recordSize; i++) {
            total.getData().add(new XYChart.Data<String, Number>(
                    new Date(   loanRecords.get(i).getDate()).toLocalDate().getMonth().toString(),
                                totals.get(i).getAmount()));

            loans.getData().add(new XYChart.Data<String, Number>(
                    new Date(   loanRecords.get(i).getDate()).toLocalDate().getMonth().toString(),
                                loanRecords.get(i).getAmount()));

            expenses.getData().add(new XYChart.Data<String, Number>(
                    new Date(   expenseRecords.get(i).getDate()).toLocalDate().getMonth().toString(),
                                expenseRecords.get(i).getAmount()));

            rent.getData().add(new XYChart.Data<String, Number>(
                    new Date(   rentRecords.get(i).getDate()).toLocalDate().getMonth().toString(),
                                rentRecords.get(i).getAmount()));

            food.getData().add(new XYChart.Data<String, Number>(
                    new Date(   foodRecords.get(i).getDate()).toLocalDate().getMonth().toString(),
                                foodRecords.get(i).getAmount()));
        }
        barChart.getData().addAll(total, loans, expenses, rent, food);

        total.getNode().setOnMouseReleased( releaseEvent -> {
            barChart.getData().remove(1, 5);
        });

    }
}