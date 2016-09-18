package controller;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import model.AccountManager;
import model.input.ModdedDatePicker;
import model.input.ModdedTextField;
import model.input.Regex;
import model.objects.Loan;
import model.time.TimeTracking;

/**
 * Created by MTs on 26/08/16.
 *
 * This is the loan tab's controller.
 */

public class LoanTabViewController {

    private Loan currentLoan;                           // To keep track of what loan has been selected in the table
                                                        // view and what loan should be updated/deleted based on this.
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
     * Automatically called when this controllers associated FXML is injected in the MainWindowView's fx:include.
     */

    @SuppressWarnings("unused")
    public void initialize() {
        // Buttons to update and delete are disabled by default since no loan has been selected when the tab is opened.
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        // Set up what type of input is expected for the different input fields.
        tfName.setUpValidation(Regex.NAME);                 // Accepts characters and numbers.
        tfAmount.setUpValidation(Regex.AMOUNT);             // Accepts numbers up to 10~ digits.
        tfInterestRate.setUpValidation(Regex.PERCENTAGE);   // Accepts up to three digits (0-100%).
        tfAmortizationAmount.setUpValidation(Regex.AMOUNT);

        dpNextPayment.setUpValidation(Regex.DATE);          // Only accepts input in the correct date form. See Regex
        dpBoundTo.setUpValidation(Regex.DATE);

        chebBoundTo.setOnAction( actionEvent -> {
            if (chebBoundTo.isSelected()) {         // If checkbox is selected, disable datepicker and null its value.
                dpBoundTo.setDisable(true);
                dpBoundTo.setValue(null);
            } else {
                dpBoundTo.setDisable(false);
            }
        });

        chebNextPayment.setOnAction( actionEvent -> {
            if (chebNextPayment.isSelected()) {     // If checkbox is selected, disable datepicker and null its value.
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
                    if (currentLoan.getNextPayment() > 86400000) {
                        dpNextPayment.setDisable(false);
                        chebNextPayment.setSelected(false);
                        dpNextPayment.setValue(new Date(currentLoan.getNextPayment()).toLocalDate());
                    } else {
                        dpNextPayment.setDisable(true);
                        chebNextPayment.setSelected(true);
                        dpNextPayment.setValue(null);
                    }

                    if (currentLoan.getBoundTo() > 86400000) {
                        dpBoundTo.setDisable(false);
                        chebBoundTo.setSelected(false);
                        dpBoundTo.setValue(new Date(currentLoan.getBoundTo()).toLocalDate());
                    } else {
                        dpBoundTo.setDisable(true);
                        chebBoundTo.setSelected(true);
                        dpBoundTo.setValue(null);
                    }
                }
            });
            return row;
        });
        refreshTableContent();      // Populate table with all available loans.
    }

    /**
     * Handles the save button.
     */

    public void handleSave() {
        if (inputValidation()) {    // If all input fields have correct values.
            // The date entered can either be left empty (i.e not is use) or with an actual value. In either case,
            // the values needs to be converted into milliseconds since epoch.

            // 1. Create a local date, new Date(0) creates an epoch date object.
            // 2. Create Calendar instance.
            // 3. Set Calendar instance to date gotten from datepicker, monthValue is set 0 for january, hence - 1.
            LocalDate nextPaymentDate = dpNextPayment.getValue() == null ? new Date(0).toLocalDate() :
                    dpNextPayment.getValue();
            Calendar nextPaymentCal = Calendar.getInstance();
            nextPaymentCal.set(nextPaymentDate.getYear(), nextPaymentDate.getMonthValue() - 1,
                    nextPaymentDate.getDayOfMonth(), 0, 0, 0);

            // 1. Create a local date.
            // 2. Create Calendar instance.
            // 3. Set Calendar instance to date gotten from datepicker.
            LocalDate boundToDate = dpBoundTo.getValue() == null ? new Date(0).toLocalDate() : dpBoundTo.getValue();
            Calendar boundToCal = Calendar.getInstance();
            boundToCal.set(boundToDate.getYear(), boundToDate.getMonthValue() - 1, boundToDate.getDayOfMonth(), 0, 0,
                    0);

            // Offset between end of month and day of month is calculated.
            int dayOffset = TimeTracking.getDayOffset(nextPaymentCal.getActualMaximum(Calendar.DAY_OF_MONTH),
                    nextPaymentCal.get(Calendar.DAY_OF_MONTH));

            // All fields are converted into their respective data types as all types are strings until this point.
            // Doubles and its need to be parsed before submission into the DB. Calendar values are converted into
            // longs by use of getTimeInMillis() method from the Calendar class.
            Loan insertedLoan = new Loan(tfName.getText(), Integer.parseInt(tfAmount.getText()),
                    Double.parseDouble(tfInterestRate.getText()), Integer.parseInt(tfAmortizationAmount.getText()),
                    nextPaymentCal.getTimeInMillis(), dayOffset, boundToCal.getTimeInMillis());

            if (new SQLiteConnection().insertLoan(insertedLoan, AccountManager.getCurrentUser().getId())) {
                // Since a new loan has been inserted into the DB, all loans now need to be re-loaded into the
                // current users list of loans. This is because when inserted, the loans are not created with
                // their ID's, so a full value loan is not inserted into the list and it hence cannot be deleted
                // (without the loan's ID number).
                AccountManager.getCurrentUser().addAllLoans();

                // Reset all field after submission into the DB.
                resetFields();
                refreshTableContent();      // Re-populate table view to view newly inserted loan.
            }
        }
    }

    /**
     * Handles the update button.
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

            int dayOffset = TimeTracking.getDayOffset(nextPaymentCal.getActualMaximum(Calendar.DAY_OF_MONTH),
                    nextPaymentCal.get(Calendar.DAY_OF_MONTH));

            Loan updatedLoan = new Loan(currentLoan.getId(), tfName.getText(),
                    Integer.parseInt(tfAmount.getText()), Double.parseDouble(tfInterestRate.getText()),
                    Integer.parseInt(tfAmortizationAmount.getText()), nextPaymentCal.getTimeInMillis(), dayOffset,
                    boundToCal.getTimeInMillis());

            // No need to specify user here, the ID of the loan in question is used.
            if (new SQLiteConnection().updateLoan(updatedLoan)) {
                // Reset button status back to how it is when the loanview is entered.
                btnSave.setDisable(false);
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);

                // Update the loan in the users list of loans so that it corresponds to its updated values.
                AccountManager.getCurrentUser().updateLoan(currentLoan, updatedLoan);

                resetFields();              // Empty fields after an update.
                refreshTableContent();      // Re-populate table view to get updated values.
            }
        }
    }

    /**
     * Handles the delete button.
     */

    public void handleDelete() {
        // No need to specify user here, the ID of the loan in question is used.
        if (new SQLiteConnection().deleteLoan(currentLoan)) {
            btnSave.setDisable(false);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);

            // Simply remove the loan from the users list of loans as well.
            AccountManager.getCurrentUser().removeLoan(currentLoan);

            resetFields();
            refreshTableContent();      // Re-populate table view to remove deleted loan from there.
        }
    }

    /**
     * Handles the clear button.
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

    private void refreshTableContent() {
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
