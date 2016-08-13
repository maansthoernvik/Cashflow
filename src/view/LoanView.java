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
import model.Input.ModdedTextField;
import model.Input.Regex;

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
    private Loan currentLoan;

    private TableView<Loan> tvLoans;
    private ModdedTextField tfName;
    private ModdedTextField tfAmount;
    private ModdedTextField tfInterestRate;
    private ModdedTextField tfAmortizationRate;
    private DatePicker dpNextPayment;
    private DatePicker dpBoundTo;

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
        tvLoans.getColumns().addAll(
                tcolName, tcolAmount, tcolInterestRate, tcolAmortizationRate
        );
        refreshTableContent();

        tfName = new ModdedTextField();
        tfName.setUpValidation(Regex.NAME);
        tfAmount = new ModdedTextField();
        tfAmount.setUpValidation(Regex.AMOUNT);
        tfInterestRate = new ModdedTextField();
        tfInterestRate.setUpValidation(Regex.PERCENTAGE);
        tfAmortizationRate = new ModdedTextField();
        tfAmortizationRate.setUpValidation(Regex.PERCENTAGE);

        dpNextPayment = new DatePicker();
        dpBoundTo = new DatePicker();

        Button btnSaveLoan = new Button("Save");
        Button btnUpdateLoan = new Button("Update");
        Button btnClearFields = new Button("Clear");
        Button btnDeleteLoan = new Button("Delete");

        btnSaveLoan.setOnMouseReleased( releaseEvent -> {
            LocalDate nextPaymentDate = dpNextPayment.getValue() == null ? LocalDate.ofEpochDay(0) : dpNextPayment.getValue();
            Calendar nextPaymentCal = Calendar.getInstance();
            nextPaymentCal.set(nextPaymentDate.getYear(), nextPaymentDate.getMonthValue() - 1, nextPaymentDate.getDayOfMonth());

            LocalDate boundToDate = dpBoundTo.getValue() == null ? LocalDate.ofEpochDay(0) : dpBoundTo.getValue();
            Calendar boundToCal = Calendar.getInstance();
            boundToCal.set(boundToDate.getYear(), boundToDate.getMonthValue() - 1, boundToDate.getDayOfMonth());

            Loan insertedLoan = new Loan(tfName.getText(), Integer.parseInt(tfAmount.getText()),
                    Double.parseDouble(tfInterestRate.getText()), Double.parseDouble(tfAmortizationRate.getText()),
                    nextPaymentCal.getTimeInMillis(), boundToCal.getTimeInMillis());

            SQLiteConn.insertLoan(insertedLoan, "Alpha");

            clearFields();
            refreshTableContent();
        });

        btnUpdateLoan.setDisable(true);
        btnUpdateLoan.setOnMouseReleased( releaseEvent -> {
            if (currentLoan != null) {
                btnSaveLoan.setDisable(false);
                btnUpdateLoan.setDisable(true);
                btnClearFields.setDisable(true);
                btnDeleteLoan.setDisable(true);

                LocalDate nextPaymentDate = dpNextPayment.getValue() == null ? LocalDate.ofEpochDay(0) : dpNextPayment.getValue();
                Calendar nextPaymentCal = Calendar.getInstance();
                nextPaymentCal.set(nextPaymentDate.getYear(), nextPaymentDate.getMonthValue() - 1, nextPaymentDate.getDayOfMonth());

                LocalDate boundToDate = dpBoundTo.getValue() == null ? LocalDate.ofEpochDay(0) : dpBoundTo.getValue();
                Calendar boundToCal = Calendar.getInstance();
                boundToCal.set(boundToDate.getYear(), boundToDate.getMonthValue() - 1, boundToDate.getDayOfMonth());

                Loan updatedLoan = new Loan(currentLoan.getId(), tfName.getText(),
                        Integer.parseInt(tfAmount.getText()), Double.parseDouble(tfInterestRate.getText()),
                        Double.parseDouble(tfAmortizationRate.getText()), nextPaymentCal.getTimeInMillis(),
                        boundToCal.getTimeInMillis());

                SQLiteConn.updateLoan(updatedLoan);

                clearFields();
                refreshTableContent();
            }
        });

        btnClearFields.setDisable(true);
        btnClearFields.setOnMouseReleased( releaseEvent -> {
            btnSaveLoan.setDisable(false);
            btnUpdateLoan.setDisable(true);
            btnClearFields.setDisable(true);
            btnDeleteLoan.setDisable(true);

            clearFields();
        });

        btnDeleteLoan.setDisable(true);
        btnDeleteLoan.setOnMouseReleased( releaseEvent -> {
            btnSaveLoan.setDisable(false);
            btnUpdateLoan.setDisable(true);
            btnClearFields.setDisable(true);
            btnDeleteLoan.setDisable(true);

            SQLiteConn.deleteLoan(currentLoan);

            clearFields();
            refreshTableContent();
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
                    dpNextPayment.setValue(new Date(loan.getNextPayment()).toLocalDate());
                    dpBoundTo.setValue(new Date(loan.getBoundTo()).toLocalDate());

                    btnSaveLoan.setDisable(true);
                    btnUpdateLoan.setDisable(false);
                    btnClearFields.setDisable(false);
                    btnDeleteLoan.setDisable(false);
                }
            });
            return row;
        });

        HBox hbFirst = new HBox();
        hbFirst.getChildren().addAll(new Label("Name:"), new Label("Amount:"));
        hbFirst.setAlignment(Pos.TOP_LEFT);

        HBox hbSecond = new HBox();
        hbSecond.getChildren().addAll(tfName, tfAmount);
        hbSecond.setAlignment(Pos.TOP_LEFT);

        HBox hbThird = new HBox();
        hbThird.getChildren().addAll(new Label("Interest rate:"), new Label("Amortization rate:"));
        hbThird.setAlignment(Pos.TOP_LEFT);

        HBox hbFourth = new HBox();
        hbFourth.getChildren().addAll(tfInterestRate, tfAmortizationRate);
        hbFourth.setAlignment(Pos.TOP_LEFT);

        HBox hbFifth = new HBox();
        hbFifth.getChildren().addAll(new Label("Next payment:"), new Label("Bound to:"));
        hbFifth.setAlignment(Pos.TOP_LEFT);

        HBox hbSixth = new HBox();
        hbSixth.getChildren().addAll(dpNextPayment, dpBoundTo, btnUpdateLoan);
        hbSixth.setAlignment(Pos.TOP_LEFT);

        HBox hbSeventh = new HBox();
        hbSeventh.getChildren().addAll(btnSaveLoan, btnUpdateLoan, btnDeleteLoan, btnClearFields);

        this.getChildren().addAll(
                tvLoans, hbFirst, hbSecond, hbThird, hbFourth, hbFifth, hbSixth, hbSeventh
        );
    }

    public Loan getCurrentLoan() {
        return currentLoan;
    }

    private TableView<Loan> refreshTableContent() {
        ObservableList<Loan> loans = FXCollections.observableArrayList(
                SQLiteConn.fetchLoans("SELECT * FROM Loans WHERE User = ?;", "Alpha")
        );

        tvLoans.setEditable(true);
        tvLoans.setItems(loans);

        return tvLoans;
    }


    private void clearFields() {
        tfName.clear();
        tfAmount.clear();
        tfInterestRate.clear();
        tfAmortizationRate.clear();
        dpNextPayment.setValue(null);
        dpBoundTo.setValue(null);

        currentLoan = null;
    }
}
