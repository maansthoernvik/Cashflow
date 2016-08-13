package view;

import controller.SQLiteConnection;
import model.Expense;
import model.Loan;

import javafx.geometry.Pos;
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

public class MyEconomyView extends VBox {

    public MyEconomyView() {
        super();

        /*
        Fields and labels.
         */

        Label lblMonthlyBills = new Label("Monthly bills:");
        Label lblLoans = new Label("Loans:");
        Label lblExpenses = new Label("Expenses:");

        TextField tfMonthlyBills = new TextField();
        TextField tfLoans = new TextField();
        TextField tfExpenses = new TextField();

        HBox hbFirst = new HBox();
        hbFirst.getChildren().addAll(tfMonthlyBills, new Label("crowns per month"));
        hbFirst.setAlignment(Pos.TOP_LEFT);

        HBox hbSecond = new HBox();
        hbSecond.getChildren().addAll(lblLoans, lblExpenses);
        hbSecond.setAlignment(Pos.TOP_LEFT);

        HBox hbThird = new HBox();
        hbThird.getChildren().addAll(tfLoans, new Label("crowns per month"), tfExpenses, new Label("crowns per month"));
        hbThird.setAlignment(Pos.TOP_LEFT);

        this.getChildren().addAll(lblMonthlyBills, hbFirst, hbSecond, hbThird);
        this.setAlignment(Pos.TOP_LEFT);

        /*
        Content of fields.
         */

        SQLiteConnection SQLiteConn = new SQLiteConnection();
        int monthlyTotal = 0;
        int loanTotal = 0;
        int expenseTotal = 0;

        ArrayList<Loan> loans;
        loans = SQLiteConn.fetchLoans("SELECT * FROM Loans WHERE User = ?;", "Alpha");

        for (int i = 0; i < loans.size(); i++) {
            Loan temp = loans.get(i);
            monthlyTotal += (temp.getAmount() * temp.getInterestRate() * 0.01) / 12;
            loanTotal += (temp.getAmount() * temp.getInterestRate() * 0.01) / 12;

            monthlyTotal += (temp.getAmount() * temp.getAmortizationRate() * 0.01) / 12;
            loanTotal += (temp.getAmount() * temp.getAmortizationRate() * 0.01) / 12;
        }

        ArrayList<Expense> expenses;
        expenses = SQLiteConn.fetchExpenses("SELECT * FROM Expenses WHERE User = ?;", "Alpha");

        for (int i = 0; i < expenses.size(); i++) {
            Expense temp = expenses.get(i);
            monthlyTotal += temp.getAmount();
            expenseTotal += temp.getAmount();
        }

        tfMonthlyBills.setText("" + monthlyTotal);
        tfLoans.setText("" + loanTotal);
        tfExpenses.setText("" + expenseTotal);
    }
}
