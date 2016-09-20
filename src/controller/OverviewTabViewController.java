package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
        final NumberAxis amount = new NumberAxis();
        amount.setLabel("Amount");
        amount.setTickLabelRotation(90.0);

        final CategoryAxis month = new CategoryAxis();
        month.setLabel("Month");

        BarChart<Number, String> barChart = new BarChart<>(amount, month);
        vbox.getChildren().add(barChart);

        HashMap<String, ArrayList<Record>> records = new SQLiteConnection().fetchRecords(
                AccountManager.getCurrentUser().getId());
        ArrayList<Record> loanRecords = records.get("LoanRecords");
        ArrayList<Record> expenseRecords = records.get("ExpenseRecords");
        ArrayList<Record> rentRecords = records.get("RentRecords");
        ArrayList<Record> foodRecords = records.get("FoodRecords");

        // Get info max of 6 months back in time.
        XYChart.Series loans    = new XYChart.Series();
        loans.setName("Loans");
        XYChart.Series expenses = new XYChart.Series();
        expenses.setName("Expenses");
        XYChart.Series rent     = new XYChart.Series();
        rent.setName("Rent");
        XYChart.Series food     = new XYChart.Series();
        food.setName("Food");

        for (int i = 0; loanRecords.size() > i && i < 6; i++) {
            loans.getData().add(new XYChart.Data<Number, String>(loanRecords.get(loanRecords.size() - (i + 1)).getAmount(),
                    new Date(loanRecords.get(loanRecords.size() - (i + 1)).getDate()).toLocalDate().getMonth().toString()));
            expenses.getData().add(new XYChart.Data<Number, String>(expenseRecords.get(expenseRecords.size() - (i + 1)).getAmount(),
                    new Date(expenseRecords.get(expenseRecords.size() - (i + 1)).getDate()).toLocalDate().getMonth().toString()));
            rent.getData().add(new XYChart.Data<Number, String>(rentRecords.get(rentRecords.size() - (i + 1)).getAmount(),
                    new Date(rentRecords.get(rentRecords.size() - (i + 1)).getDate()).toLocalDate().getMonth().toString()));
            food.getData().add(new XYChart.Data<Number, String>(foodRecords.get(foodRecords.size() - (i + 1)).getAmount(),
                    new Date(foodRecords.get(foodRecords.size() - (i + 1)).getDate()).toLocalDate().getMonth().toString()));
        }
        barChart.getData().addAll(loans, expenses, rent, food);
    }
}