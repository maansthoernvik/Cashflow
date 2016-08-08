package view;

import model.Loan;
import controller.SQLiteConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by MTs on 06/08/16.
 *
 *
 */

public class LoanView extends VBox {

    public LoanView() {
        super();
        this.setAlignment(Pos.TOP_LEFT);

        Label lblName = new Label("Name:");
        Label lblAmount = new Label("Amount:");
        Label lblInterestRate = new Label("Interest rate:");
        Label lblAmortizationRate = new Label("Amortization rate:");
        Label lblAmortizationAmount = new Label("Amortization amount:");
        Label lblNextPayment = new Label("Next payment:");
        Label lblBoundTo = new Label("Bound to:");

        TextField tfName = new TextField();
        TextField tfAmount = new TextField();
        TextField tfInterestRate = new TextField();
        TextField tfAmortizationRate = new TextField();
        TextField tfAmortizationAmount = new TextField();
        DatePicker dpNextPayment = new DatePicker();
        DatePicker dpBoundTo = new DatePicker();
        TextField test = new TextField();
        TextField testTwo = new TextField();
        dpBoundTo.setOnAction( actionEvent -> {
            Calendar cal = Calendar.getInstance();
            LocalDate local = dpBoundTo.getValue();
            cal.set(local.getYear(), local.getMonthValue() - 1, local.getDayOfMonth());

            test.setText("" + cal.getTimeInMillis());
            testTwo.setText("" + new Date(cal.getTimeInMillis()).toString());
        });

        HBox hbFirst = new HBox();
        hbFirst.getChildren().addAll(lblName, lblAmount);
        hbFirst.setAlignment(Pos.TOP_LEFT);

        HBox hbSecond = new HBox();
        hbSecond.getChildren().addAll(tfName, tfAmount);
        hbSecond.setAlignment(Pos.TOP_LEFT);

        HBox hbThird = new HBox();
        hbThird.getChildren().addAll(lblInterestRate, lblAmortizationRate, lblAmortizationAmount);
        hbThird.setAlignment(Pos.TOP_LEFT);

        HBox hbFourth = new HBox();
        hbFourth.getChildren().addAll(tfInterestRate, tfAmortizationRate, tfAmortizationAmount);
        hbFourth.setAlignment(Pos.TOP_LEFT);

        HBox hbFifth = new HBox();
        hbFifth.getChildren().addAll(lblNextPayment, lblBoundTo);
        hbFifth.setAlignment(Pos.TOP_LEFT);

        HBox hbSixth = new HBox();
        hbSixth.getChildren().addAll(dpNextPayment, dpBoundTo, test, testTwo);
        hbSixth.setAlignment(Pos.TOP_LEFT);

        TableView<Loan> tvLoans = new TableView<>();
        tvLoans.setEditable(true);

        TableColumn<Loan, String> tcolName = new TableColumn<>("Name");
        TableColumn<Loan, Integer> tcolAmount = new TableColumn<>("Amount");
        TableColumn<Loan, Double> tcolInterestRate = new TableColumn<>("Interest rate");
        TableColumn<Loan, Double> tcolAmortizationRate = new TableColumn<>("Amortization rate");
        TableColumn<Loan, Integer> tcolAmortizationAmount = new TableColumn<>("Amortization amount");

        // The TableView goes directly to the Loan's getters and attempts to attain the data. Adds "get" in front of
        // each string and capitalizes (if not already done) the first letter. Spaces fuck shit up, not using camel-
        // cases fucks things up. Don't fuck shit up.
        tcolName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        tcolAmount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        tcolInterestRate.setCellValueFactory(new PropertyValueFactory<>("InterestRate"));
        tcolAmortizationRate.setCellValueFactory(new PropertyValueFactory<>("AmortizationRate"));
        tcolAmortizationAmount.setCellValueFactory(new PropertyValueFactory<>("AmortizationAmount"));

        tvLoans.setRowFactory( tv -> {
            TableRow<Loan> row = new TableRow<>();
            row.setOnMouseClicked( clickEvent -> {
                if ((clickEvent.getClickCount() == 1) && (!row.isEmpty())) {
                    Loan loan = row.getItem();
                    tfName.setText(loan.getName());
                    tfAmount.setText("" + loan.getAmount());
                    tfInterestRate.setText("" + loan.getInterestRate());
                    tfAmortizationRate.setText("" + loan.getAmortizationRate());
                    tfAmortizationAmount.setText("" + loan.getAmortizationAmount());
                    dpNextPayment.setValue(loan.getNextPayment().toLocalDate());
                    dpBoundTo.setValue(loan.getBoundTo().toLocalDate());
                }
            });
            return row;
        });

        SQLiteConnection SQLiteConn = new SQLiteConnection();

        ArrayList<Loan> loans;
        loans = SQLiteConn.fetchLoans("SELECT * FROM Loans WHERE User = ?;", "Alpha");
        ObservableList<Loan> obsLoans = FXCollections.observableArrayList(loans);

        tvLoans.setItems(obsLoans);
        tvLoans.getColumns().addAll(
                tcolName, tcolAmount, tcolInterestRate, tcolAmortizationRate, tcolAmortizationAmount
        );
        tvLoans.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        this.getChildren().addAll(
                tvLoans, hbFirst, hbSecond, hbThird, hbFourth, hbFifth, hbSixth
        );
    }
}
