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
import java.util.Calendar;

/**
 * Created by MTs on 06/08/16.
 *
 *
 */

public class LoanView extends VBox {

    private SQLiteConnection SQLiteConn;
    private TableView<Loan> tvLoans;
    private Loan currentLoan;

    public LoanView() {
        super();
        this.setAlignment(Pos.TOP_LEFT);

        SQLiteConn = new SQLiteConnection();

        tvLoans = new TableView<>();
        tvLoans.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Loan, String> tcolName = new TableColumn<>("Name");
        tcolName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        TableColumn<Loan, Integer> tcolAmount = new TableColumn<>("Amount");
        tcolAmount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        TableColumn<Loan, Double> tcolInterestRate = new TableColumn<>("Interest rate");
        tcolInterestRate.setCellValueFactory(new PropertyValueFactory<>("InterestRate"));
        TableColumn<Loan, Double> tcolAmortizationRate = new TableColumn<>("Amortization rate");
        tcolAmortizationRate.setCellValueFactory(new PropertyValueFactory<>("AmortizationRate"));
        TableColumn<Loan, Integer> tcolAmortizationAmount = new TableColumn<>("Amortization amount");
        tcolAmortizationAmount.setCellValueFactory(new PropertyValueFactory<>("AmortizationAmount"));
        tvLoans.getColumns().addAll(
                tcolName, tcolAmount, tcolInterestRate, tcolAmortizationRate, tcolAmortizationAmount
        );
        tvLoans = refreshTableContent();

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

        Button btnUpdateLoan = new Button("Update");
        btnUpdateLoan.setDisable(true);

        Button btnClearFields = new Button("Clear");
        btnClearFields.setDisable(true);

        Button btnSaveLoan = new Button("Save");

        btnSaveLoan.setOnMouseReleased( releaseEvent -> {
            LocalDate nextPaymentDate = dpNextPayment.getValue();
            Calendar nextPaymentCal = Calendar.getInstance();
            nextPaymentCal.set(nextPaymentDate.getYear(), nextPaymentDate.getMonthValue() - 1, nextPaymentDate.getDayOfMonth());

            LocalDate boundToDate = dpBoundTo.getValue();
            Calendar boundToCal = Calendar.getInstance();
            boundToCal.set(boundToDate.getYear(), boundToDate.getMonthValue() - 1, boundToDate.getDayOfMonth());

            Loan insertedLoan = new Loan(tfName.getText(), Integer.parseInt(tfAmount.getText()),
                    Double.parseDouble(tfInterestRate.getText()), Double.parseDouble(tfAmortizationRate.getText()),
                    Integer.parseInt(tfAmortizationAmount.getText()), nextPaymentCal.getTimeInMillis(),
                    boundToCal.getTimeInMillis());

            SQLiteConn.insertLoan(insertedLoan, "Alpha");

            tfName.clear();
            tfAmount.clear();
            tfInterestRate.clear();
            tfAmortizationRate.clear();
            tfAmortizationAmount.clear();
            dpNextPayment.setValue(null);
            dpBoundTo.setValue(null);
        });

        btnUpdateLoan.setOnMouseReleased( releaseEvent -> {
            if (currentLoan != null) {
                btnSaveLoan.setDisable(false);
                btnUpdateLoan.setDisable(true);
                btnClearFields.setDisable(true);

                LocalDate nextPaymentDate = dpNextPayment.getValue();
                Calendar nextPaymentCal = Calendar.getInstance();
                nextPaymentCal.set(nextPaymentDate.getYear(), nextPaymentDate.getMonthValue() - 1, nextPaymentDate.getDayOfMonth());

                LocalDate boundToDate = dpBoundTo.getValue();
                Calendar boundToCal = Calendar.getInstance();
                boundToCal.set(boundToDate.getYear(), boundToDate.getMonthValue() - 1, boundToDate.getDayOfMonth());

                Loan updatedLoan = new Loan(currentLoan.getId(), tfName.getText(),
                        Integer.parseInt(tfAmount.getText()), Double.parseDouble(tfInterestRate.getText()),
                        Double.parseDouble(tfAmortizationRate.getText()), Integer.parseInt(tfAmortizationAmount.getText()),
                        nextPaymentCal.getTimeInMillis(), boundToCal.getTimeInMillis());

                SQLiteConn.updateLoan(updatedLoan);

                currentLoan = null;
            }
        });

        btnClearFields.setOnMouseReleased( releaseEvent -> {
            btnSaveLoan.setDisable(false);
            btnUpdateLoan.setDisable(true);
            btnClearFields.setDisable(true);

            tfName.clear();
            tfAmount.clear();
            tfInterestRate.clear();
            tfAmortizationRate.clear();
            tfAmortizationAmount.clear();
            dpNextPayment.setValue(null);
            dpBoundTo.setValue(null);

            currentLoan = null;
        });

        tvLoans.setRowFactory( tv -> {
            TableRow<Loan> row = new TableRow<>();
            row.setOnMouseClicked( clickEvent -> {
                if ((clickEvent.getClickCount() == 1) && (!row.isEmpty())) {
                    Loan loan = row.getItem();
                    currentLoan = loan;

                    tfName.setText(loan.getName());
                    tfAmount.setText("" + loan.getAmount());
                    tfInterestRate.setText("" + loan.getInterestRate());
                    tfAmortizationRate.setText("" + loan.getAmortizationRate());
                    tfAmortizationAmount.setText("" + loan.getAmortizationAmount());
                    dpNextPayment.setValue(new Date(loan.getNextPayment()).toLocalDate());
                    dpBoundTo.setValue(new Date(loan.getBoundTo()).toLocalDate());

                    btnSaveLoan.setDisable(true);
                    btnUpdateLoan.setDisable(false);
                    btnClearFields.setDisable(false);
                }
            });
            return row;
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
        hbSixth.getChildren().addAll(dpNextPayment, dpBoundTo, btnUpdateLoan);
        hbSixth.setAlignment(Pos.TOP_LEFT);

        HBox hbSeventh = new HBox();
        hbSeventh.getChildren().addAll(btnSaveLoan, btnUpdateLoan, btnClearFields);

        this.getChildren().addAll(
                tvLoans, hbFirst, hbSecond, hbThird, hbFourth, hbFifth, hbSixth, hbSeventh
        );
    }

    private TableView<Loan> refreshTableContent() {
        ObservableList<Loan> loans = FXCollections.observableArrayList(
                SQLiteConn.fetchLoans("SELECT * FROM Loans WHERE User = ?;", "Alpha"));

        tvLoans.setEditable(true);
        tvLoans.setItems(loans);

        return tvLoans;
    }

    public Loan getCurrentLoan() {
        return currentLoan;
    }
}
