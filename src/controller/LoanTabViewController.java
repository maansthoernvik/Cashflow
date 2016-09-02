package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import model.AccountManager;
import model.SQLiteConnection;
import model.input.ModdedDatePicker;
import model.input.ModdedTextField;
import model.input.Regex;
import model.objects.Loan;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Created by MTs on 26/08/16.
 *
 *
 */

public class LoanTabViewController {

    private Loan currentLoan;

    @FXML private TableView<Loan> tvLoans;

    @FXML private ModdedTextField tfName;
    @FXML private ModdedTextField tfAmount;
    @FXML private ModdedTextField tfInterestRate;
    @FXML private ModdedTextField tfAmortizationAmount;

    @FXML private ModdedDatePicker dpNextPayment;
    @FXML private ModdedDatePicker dpBoundTo;

    @FXML private CheckBox chebNextPayment;
    @FXML private CheckBox chebBoundTo;

    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;

    /**
     *
     */

    @SuppressWarnings("unused")
    public void initialize() {
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        tfName.setUpValidation(Regex.NAME);
        tfAmount.setUpValidation(Regex.AMOUNT);
        tfInterestRate.setUpValidation(Regex.PERCENTAGE);
        tfAmortizationAmount.setUpValidation(Regex.AMOUNT);

        dpNextPayment.setUpValidation(Regex.DATE);
        dpBoundTo.setUpValidation(Regex.DATE);

        chebBoundTo.setOnAction( actionEvent -> {
            if (chebBoundTo.isSelected()) {
                dpBoundTo.setDisable(true);
                dpBoundTo.setValue(null);
            } else {
                dpBoundTo.setDisable(false);
            }
        });

        chebNextPayment.setOnAction( actionEvent -> {
            if (chebNextPayment.isSelected()) {
                dpNextPayment.setDisable(true);
                dpNextPayment.setValue(null);
            } else {
                dpNextPayment.setDisable(false);
            }
        });

        tvLoans.setRowFactory( tv -> {
            TableRow<Loan> row = new TableRow<>();
            row.setOnMouseClicked( clickEvent -> {  // If an item is clicked.
                if ((clickEvent.getClickCount() == 1) && (!row.isEmpty())) {    // Item was clicked once and the row is
                    // not empty.
                    // Enable update and delete buttons and disable save.
                    btnSave.setDisable(true);
                    btnUpdate.setDisable(false);
                    btnDelete.setDisable(false);

                    currentLoan = row.getItem();             // Assign the currently selected loan to global variable.

                    // Set all loan fields to the values of the currently selected loan.
                    tfName.setText(currentLoan.getName());
                    tfAmount.setText("" + currentLoan.getAmount());
                    tfInterestRate.setText("" + currentLoan.getInterestRate());
                    tfAmortizationAmount.setText("" + currentLoan.getAmortizationAmount());

                    // If the value of NextPayment is greater than 86 400 000 milliseconds, the date is greater than
                    // epoch and the date shall be displayed. This is so since dates that are left empty are assigned
                    // the epoch value. Otherwise, the date is simply set to null and checkbox is selected.
                    // TODO make it so that is check for dates between 86400000 and now(), if in this range then do...
                    if (currentLoan.getNextPayment() > 86400000) {
                        dpNextPayment.setDisable(false);
                        dpNextPayment.setValue(new Date(currentLoan.getNextPayment()).toLocalDate());
                        chebNextPayment.setSelected(false);
                    } else {
                        dpNextPayment.setDisable(true);
                        dpNextPayment.setValue(null);
                        chebNextPayment.setSelected(true);
                    }
                    if (currentLoan.getBoundTo() > 86400000) {
                        dpBoundTo.setDisable(false);
                        dpBoundTo.setValue(new Date(currentLoan.getBoundTo()).toLocalDate());
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
    }

    /**
     *
     */

    public void handleSave() {
        if (inputValidation()) {    // If all input fields have correct values.
            // The date entered can either be left empty (i.e not is use) or with an actual value. In either case,
            // the values needs to be converted into milliseconds since epoch.

            // 1. Create a local date.
            LocalDate nextPaymentDate = dpNextPayment.getValue() == null ? new Date(0).toLocalDate() :
                    dpNextPayment.getValue();
            // 2. Create Calendar instance.
            Calendar nextPaymentCal = Calendar.getInstance();
            // 3. Set Calendar instance to date gotten from datepicker.
            nextPaymentCal.set(nextPaymentDate.getYear(), nextPaymentDate.getMonthValue() - 1,
                    nextPaymentDate.getDayOfMonth(), 0, 0, 0);

            // 1. Create a local date.
            LocalDate boundToDate = dpBoundTo.getValue() == null ? new Date(0).toLocalDate() : dpBoundTo.getValue();
            // 2. Create Calendar instance.
            Calendar boundToCal = Calendar.getInstance();
            // 3. Set Calendar instance to date gotten from datepicker.
            boundToCal.set(boundToDate.getYear(), boundToDate.getMonthValue() - 1, boundToDate.getDayOfMonth(), 0, 0,
                    0);

            // All fields are converted into their respective data types as all types are strings until this point.
            // Doubles and its need to be parsed before submission into the DB. Calendar values are converted into
            // longs by use of getTimeInMillis() method from the Calendar class.
            Loan insertedLoan = new Loan(tfName.getText(), Integer.parseInt(tfAmount.getText()),
                    Double.parseDouble(tfInterestRate.getText()), Integer.parseInt(tfAmortizationAmount.getText()),
                    nextPaymentCal.getTimeInMillis(), boundToCal.getTimeInMillis());

            if (new SQLiteConnection().insertLoan(insertedLoan, AccountManager.getCurrentUser().getId())) {
                // Since a new loan has been inserted into the DB, all loans now need to be re-loaded into the
                // current users list of loans. This is because when inserted, the loans are not created with
                // their ID's, so a full value loan is not inserted into the list and it hence cannot be deleted
                // (without the loan's ID number).
                AccountManager.getCurrentUser().addAllLoans();

                // Reset all field after submission into the DB.
                resetFields();
                refreshTableContent();
            }
        }
    }

    /**
     *
     */

    public void handleUpdate() {
        if (inputValidation()) {    // If all input fields have correct values.
            // See saving process used for btnSaveLoan.
            LocalDate nextPaymentDate = dpNextPayment.getValue() == null ? new Date(0).toLocalDate() :
                    dpNextPayment.getValue();
            Calendar nextPaymentCal = Calendar.getInstance();
            nextPaymentCal.set(nextPaymentDate.getYear(), nextPaymentDate.getMonthValue() - 1,
                    nextPaymentDate.getDayOfMonth(), 0, 0, 0);

            LocalDate boundToDate = dpBoundTo.getValue() == null ? new Date(0).toLocalDate() : dpBoundTo.getValue();
            Calendar boundToCal = Calendar.getInstance();
            boundToCal.set(boundToDate.getYear(), boundToDate.getMonthValue() - 1, boundToDate.getDayOfMonth(), 0, 0, 0);

            Loan updatedLoan = new Loan(currentLoan.getId(), tfName.getText(),
                    Integer.parseInt(tfAmount.getText()), Double.parseDouble(tfInterestRate.getText()),
                    Integer.parseInt(tfAmortizationAmount.getText()), nextPaymentCal.getTimeInMillis(),
                    boundToCal.getTimeInMillis());

            // No need to specify user here, the ID of the loan in question is used.
            if (new SQLiteConnection().updateLoan(updatedLoan)) {
                // Reset button status back to how it is when the loanview is entered.
                btnSave.setDisable(false);
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);

                // Update the loan in the users list of loans so that it corresponds to its updated values.
                AccountManager.getCurrentUser().updateLoan(currentLoan, updatedLoan);

                resetFields();
                refreshTableContent();
            }

        }
    }

    /**
     *
     */

    public void handleDelete() {
        // No need to specify user here, the ID of the loan in question is used.
        if (new SQLiteConnection().deleteLoan(currentLoan)) {
            btnSave.setDisable(false);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);

            // Simply remove the loan from the users list of loans.
            AccountManager.getCurrentUser().removeLoan(currentLoan);

            resetFields();
            refreshTableContent();
        }
    }

    /**
     *
     */

    public void handleClear() {
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        resetFields();
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
        tfAmortizationAmount.validate();
        dpNextPayment.validate();
        dpBoundTo.validate();

        // Return validity.
        return tfName.validate() && tfAmount.validate() && tfInterestRate.validate() && tfAmortizationAmount.validate() &&
                dpNextPayment.validate() && dpBoundTo.validate();
    }

    /**
     * Used to either populate the table view or update it when a new loan has been saved/updated/deleted.
     */

    public void refreshTableContent() {
        // Get the current list of loans from the users list of loans.
        ObservableList<Loan> loans = FXCollections.observableArrayList(
                AccountManager.getCurrentUser().getLoans()
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
        tfAmortizationAmount.reset();

        dpNextPayment.reset();
        dpNextPayment.setDisable(false);
        dpBoundTo.reset();
        dpBoundTo.setDisable(false);

        chebNextPayment.setSelected(false);
        chebBoundTo.setSelected(false);

        currentLoan = null;
    }
}
