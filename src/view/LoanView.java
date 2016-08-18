package view;

import model.Input.ModdedDatePicker;
import model.Loan;
import model.Input.ModdedTextField;
import model.Input.Regex;
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
 * This view shows an overview of all registered loans of a user. The user can edit, create a new and delete loans.
 */

public class LoanView extends VBox {

    private SQLiteConnection SQLiteConn;
    private Loan currentLoan;

    private TableView<Loan> tvLoans;
    private ModdedTextField tfName;
    private ModdedTextField tfAmount;
    private ModdedTextField tfInterestRate;
    private ModdedTextField tfAmortizationRate;
    private ModdedDatePicker dpNextPayment;
    private ModdedDatePicker dpBoundTo;

    private CheckBox chebNextPayment;
    private CheckBox chebBoundTo;

    /**
     * Default constructor for LoanViews, populating the VBox with all items the view contains.
     */

    @SuppressWarnings("unchecked")
    public LoanView() {
        super();

        // Connection object for use with the SQLite database.
        SQLiteConn = new SQLiteConnection();

        // For CellValueFactories it is extremely important to keep naming consistent with getters of the datatype.
        // If this is spelled wrong, the value will not be gotten.

        tvLoans = new TableView<>();
        tvLoans.setEditable(true);
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

        // Textfields and datepickers are loaded with Regex values from the Enum class. This defines the type of data
        // they will handle. If the wrong regex is entered, the border will turn red upon wrong data entered and you
        // will not be able to save.

        tfName = new ModdedTextField(Regex.NAME);
        tfAmount = new ModdedTextField(Regex.AMOUNT);
        tfInterestRate = new ModdedTextField(Regex.PERCENTAGE);
        tfAmortizationRate = new ModdedTextField(Regex.PERCENTAGE);

        dpNextPayment = new ModdedDatePicker(Regex.DATE);
        dpBoundTo = new ModdedDatePicker(Regex.DATE);

        chebNextPayment = new CheckBox("No payments");
        chebNextPayment.setOnAction( actionEvent -> {
            if (chebNextPayment.isSelected()) {
                dpNextPayment.setDisable(true);
                dpNextPayment.setValue(null);
            } else {
                dpNextPayment.setDisable(false);
            }
        });
        chebBoundTo = new CheckBox("Unbound");
        chebBoundTo.setOnAction( actionEvent -> {
            if (chebBoundTo.isSelected()) {
                dpBoundTo.setDisable(true);
                dpBoundTo.setValue(null);
            } else {
                dpBoundTo.setDisable(false);
            }
        });

        // Buttons for managing the loans.

        Button btnSaveLoan = new Button("Save");
        Button btnUpdateLoan = new Button("Update");
        Button btnClearFields = new Button("Clear");
        Button btnDeleteLoan = new Button("Delete");

        btnSaveLoan.setOnMouseReleased( releaseEvent -> {
            if (inputValidation()) {    // If all input fields have correct values.
                // The date entered can either be left empty (i.e not is use) or with an actual value. In either case,
                // the values needs to be converted into milliseconds since epoch.

                // 1. Create a local date.
                LocalDate nextPaymentDate = dpNextPayment.getValue() == null ?  LocalDate.ofEpochDay(0) :
                                                                                dpNextPayment.getValue();
                // 2. Create Calendar instance.
                Calendar nextPaymentCal = Calendar.getInstance();
                // 3. Set Calendar instance to date gotten from datepicker.
                nextPaymentCal.set(nextPaymentDate.getYear(), nextPaymentDate.getMonthValue() - 1,
                        nextPaymentDate.getDayOfMonth());

                // 1. Create a local date.
                LocalDate boundToDate = dpBoundTo.getValue() == null ?  LocalDate.ofEpochDay(0) :
                                                                        dpBoundTo.getValue();
                // 2. Create Calendar instance.
                Calendar boundToCal = Calendar.getInstance();
                // 3. Set Calendar instance to date gotten from datepicker.
                boundToCal.set(boundToDate.getYear(), boundToDate.getMonthValue() - 1, boundToDate.getDayOfMonth());

                // All fields are converted into their respective data types as all types are strings until this point.
                // Doubles and its need to be parsed before submission into the DB. Calendar values are converted into
                // longs by use of getTimeInMillis() method from the Calendar class.
                Loan insertedLoan = new Loan(tfName.getText(), Integer.parseInt(tfAmount.getText()),
                        Double.parseDouble(tfInterestRate.getText()), Double.parseDouble(tfAmortizationRate.getText()),
                        nextPaymentCal.getTimeInMillis(), boundToCal.getTimeInMillis());

                // TODO - "Alpha" is to be replaced by the name of the current user, through a separate login class.
                SQLiteConn.insertLoan(insertedLoan, "Alpha");

                // Reset all field after submission into the DB.
                resetFields();
                refreshTableContent();
            }
        });

        // Update button disabled by default, enabled when a loan is selected.
        btnUpdateLoan.setDisable(true);
        btnUpdateLoan.setOnMouseReleased( releaseEvent -> {
            if (inputValidation()) {    // If all input fields have correct values.
                // Reset button status back to how it is when the loanview is entered.
                btnSaveLoan.setDisable(false);
                btnUpdateLoan.setDisable(true);
                btnDeleteLoan.setDisable(true);

                // See saving process used for btnSaveLoan.
                LocalDate nextPaymentDate = dpNextPayment.getValue() == null ?  LocalDate.ofEpochDay(0) :
                                                                                dpNextPayment.getValue();
                Calendar nextPaymentCal = Calendar.getInstance();
                nextPaymentCal.set(nextPaymentDate.getYear(), nextPaymentDate.getMonthValue() - 1,
                        nextPaymentDate.getDayOfMonth());

                LocalDate boundToDate = dpBoundTo.getValue() == null ?  LocalDate.ofEpochDay(0) :
                                                                        dpBoundTo.getValue();
                Calendar boundToCal = Calendar.getInstance();
                boundToCal.set(boundToDate.getYear(), boundToDate.getMonthValue() - 1, boundToDate.getDayOfMonth());

                Loan updatedLoan = new Loan(currentLoan.getId(), tfName.getText(),
                        Integer.parseInt(tfAmount.getText()), Double.parseDouble(tfInterestRate.getText()),
                        Double.parseDouble(tfAmortizationRate.getText()), nextPaymentCal.getTimeInMillis(),
                        boundToCal.getTimeInMillis());

                // No need to specify user here, the ID of the loan in question is used.
                SQLiteConn.updateLoan(updatedLoan);

                resetFields();
                refreshTableContent();
            }
        });

        btnClearFields.setOnMouseReleased( releaseEvent -> {
            btnSaveLoan.setDisable(false);
            btnUpdateLoan.setDisable(true);
            btnDeleteLoan.setDisable(true);

            resetFields();
        });

        // Delete button disabled by default, enabled when a loan is selected.
        btnDeleteLoan.setDisable(true);
        btnDeleteLoan.setOnMouseReleased( releaseEvent -> {
            btnSaveLoan.setDisable(false);
            btnUpdateLoan.setDisable(true);
            btnDeleteLoan.setDisable(true);

            // No need to specify user here, the ID of the loan in question is used.
            SQLiteConn.deleteLoan(currentLoan);

            resetFields();
            refreshTableContent();
        });

        // TableView populated by all loans from the database.
        tvLoans.setRowFactory( tv -> {
            TableRow<Loan> row = new TableRow<>();
            row.setOnMouseClicked( clickEvent -> {  // If an item is clicked.
                if ((clickEvent.getClickCount() == 1) && (!row.isEmpty())) {    // Item was clicked once and the row is
                                                                                // not empty.
                    // Enable update and delete buttons and disable save.
                    btnSaveLoan.setDisable(true);
                    btnUpdateLoan.setDisable(false);
                    btnDeleteLoan.setDisable(false);

                    Loan loan = row.getItem();      // Load the loan from the row.
                    currentLoan = loan;             // Assign the currently selected loan to global variable.

                    // Set all loan fields to the values of the currently selected loan.
                    tfName.setText(loan.getName());
                    tfAmount.setText("" + loan.getAmount());
                    tfInterestRate.setText("" + loan.getInterestRate());
                    tfAmortizationRate.setText("" + loan.getAmortizationRate());

                    // If the value of NextPayment is greater than 86 400 000 milliseconds, the date is greater than
                    // epoch and the date shall be displayed. This is so since dates that are left empty are assigned
                    // the epoch value. Otherwise, the date is simply set to null and checkbox is selected.
                    if (loan.getNextPayment() > 86400000) {
                        dpNextPayment.setDisable(false);
                        dpNextPayment.setValue(new Date(loan.getNextPayment()).toLocalDate());
                        chebNextPayment.setSelected(false);
                    } else {
                        dpNextPayment.setDisable(true);
                        dpNextPayment.setValue(null);
                        chebNextPayment.setSelected(true);
                    }
                    if (loan.getBoundTo() > 86400000) {
                        dpBoundTo.setDisable(false);
                        dpBoundTo.setValue(new Date(loan.getBoundTo()).toLocalDate());
                        chebBoundTo.setSelected(false);
                    } else {
                        dpBoundTo.setDisable(true);
                        dpBoundTo.setValue(null);
                        chebBoundTo.setSelected(true);
                    }
                }
            });
            return row;
        });

        // Creates HBoxes for different rows of this class (since >this< extends the VBox class) and then it is simply
        // a matter of adding them all in order - hence the naming using numbers. Labels are added directly since there
        // was no reason to instantiate them anywhere else.
        HBox hbFirst = new HBox();
        hbFirst.getChildren().addAll(new Label("Name:"), new Label("Amount:"));
        HBox hbSecond = new HBox();
        hbSecond.getChildren().addAll(tfName, tfAmount);
        HBox hbThird = new HBox();
        hbThird.getChildren().addAll(new Label("Interest rate:"), new Label("Amortization rate:"));
        HBox hbFourth = new HBox();
        hbFourth.getChildren().addAll(tfInterestRate, tfAmortizationRate);
        HBox hbFifth = new HBox();
        hbFifth.getChildren().addAll(new Label("Next payment:"), new Label("Bound to:"));
        HBox hbSixth = new HBox();
        hbSixth.getChildren().addAll(dpNextPayment, chebNextPayment, dpBoundTo, chebBoundTo);
        HBox hbSeventh = new HBox();
        hbSeventh.getChildren().addAll(btnSaveLoan, btnUpdateLoan, btnDeleteLoan, btnClearFields);

        // Adding all of the above HBoxes to >this< VBox.
        getChildren().addAll(
                tvLoans, hbFirst, hbSecond, hbThird, hbFourth, hbFifth, hbSixth, hbSeventh
        );
    }

    /**
     * Getter getter of the current loan.
     *
     * @return currently loaded loan
     */

    @SuppressWarnings("unused")
    public Loan getCurrentLoan() {
        return currentLoan;
    }

    /**
     * Used to validate all loan input fields of either an old loan or a new submission.
     *
     * @return boolean value representing the integrity of the entered loan values
     */

    private boolean inputValidation() {
        // This process is only used to ensure fields have their correct border color (either error red or normal blue).
        tfName.validate();
        tfAmount.validate();
        tfInterestRate.validate();
        tfAmortizationRate.validate();
        dpNextPayment.validate();
        dpBoundTo.validate();

        // Return validity.
        return tfName.validate() && tfAmount.validate() && tfInterestRate.validate() && tfAmortizationRate.validate() &&
                dpNextPayment.validate() && dpBoundTo.validate();
    }

    /**
     * Used to either populate the table view or update it when a new loan has been saved/updated/deleted.
     */

    private void refreshTableContent() {
        ObservableList<Loan> loans = FXCollections.observableArrayList(
                SQLiteConn.fetchLoans("SELECT * FROM Loans WHERE User = ?;", "Alpha")
        );

        tvLoans.setItems(loans);
    }

    /**
     * Resets all input fields to their default values and checkboxes to their unchecked state.
     */

    private void resetFields() {
        tfName.reset();
        tfAmount.reset();
        tfInterestRate.reset();
        tfAmortizationRate.reset();

        dpNextPayment.reset();
        dpNextPayment.setDisable(false);

        dpBoundTo.reset();
        dpBoundTo.setDisable(false);

        chebNextPayment.setSelected(false);
        chebBoundTo.setSelected(false);

        currentLoan = null;
    }
}
