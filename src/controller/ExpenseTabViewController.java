package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import model.AccountManager;
import model.objects.Expense;
import model.SQLiteConnection;
import model.input.ModdedDatePicker;
import model.input.ModdedTextField;
import model.input.Regex;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Created by MTs on 26/08/16.
 *
 *
 */

public class ExpenseTabViewController {

    private Expense currentExpense;

    @FXML private TableView<Expense> tvExpenses;

    @FXML private ModdedTextField tfName;
    @FXML private ModdedTextField tfAmount;

    @FXML private ModdedDatePicker dpEndDate;

    @FXML private CheckBox chebEndDate;

    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;

    /**
     * This method is automatically called due to its name
     */

    @SuppressWarnings("unused")
    public void initialize() {
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        tfName.setUpValidation(Regex.NAME);
        tfAmount.setUpValidation(Regex.LESSERAMOUNT);

        dpEndDate.setUpValidation(Regex.DATE);

        chebEndDate.setText("No end");
        chebEndDate.setOnAction( actionEvent -> {
            if (chebEndDate.isSelected()) {
                dpEndDate.setDisable(true);
                dpEndDate.setValue(null);
            } else {
                dpEndDate.setDisable(false);
            }
        });

        // TableView populated by all expenses from the database.
        tvExpenses.setRowFactory( tv -> {
            TableRow<Expense> row = new TableRow<>();
            row.setOnMouseClicked( clickEvent -> {  // If an item is clicked.
                if ((clickEvent.getClickCount() == 1) && (!row.isEmpty())) {    // Item was clicked once and the row is
                    // not empty.
                    // Enable update and delete buttons and disable save.
                    btnSave.setDisable(true);
                    btnUpdate.setDisable(false);
                    btnDelete.setDisable(false);

                    currentExpense = row.getItem();     // Assign the currently selected loan to global variable.

                    // Set all expense fields to the values of the currently selected expense.
                    tfName.setText(currentExpense.getName());
                    tfAmount.setText("" + currentExpense.getAmount());

                    // TODO - How shall end dates be handled for expenses?
                    if (currentExpense.getEndDate() > 86400000) {
                        dpEndDate.setDisable(false);
                        dpEndDate.setValue(new Date(currentExpense.getEndDate()).toLocalDate());
                        chebEndDate.setSelected(false);
                    } else {
                        dpEndDate.setDisable(true);
                        dpEndDate.setValue(null);
                        chebEndDate.setSelected(true);
                    }
                }
            });
            return row;
        });
        refreshTableContent();
    }

    /**
     *
     */

    public void handleSave() {
        if (inputValidation()) {    // If all input fields have correct values.
            // The date entered can either be left empty (i.e not is use) or with an actual value. In either case,
            // the values needs to be converted into milliseconds since epoch.

            // 1. Create a local date.
            LocalDate endDateDate = dpEndDate.getValue() == null ? new Date(0).toLocalDate() : dpEndDate.getValue();
            // 2. Create Calendar instance.
            Calendar endDateCal = Calendar.getInstance();
            // 3. Set Calendar instance to date gotten from datepicker.
            endDateCal.set(endDateDate.getYear(), endDateDate.getMonthValue() - 1, endDateDate.getDayOfMonth(), 0, 0,
                    0);

            // All fields are converted into their respective data types as all types are strings until this point.
            // Doubles and its need to be parsed before submission into the DB. Calendar values are converted into
            // longs by use of getTimeInMillis() method from the Calendar class.
            Expense insertedExpense = new Expense(tfName.getText(), Integer.parseInt(tfAmount.getText()),
                    endDateCal.getTimeInMillis());

            if (new SQLiteConnection().insertExpense(insertedExpense, AccountManager.getCurrentUser().getId())) {
                // Since a new expense has been inserted into the DB, all expenses now need to be re-loaded into the
                // current users list of expenses. This is because when inserted, the expenses are not created with
                // their ID's, so a full value expense is not inserted into the list and it hence cannot be deleted
                // (without the expense's ID number).
                AccountManager.getCurrentUser().addAllExpenses();

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
            // See saving process used for btnSaveExpense.
            LocalDate endDateDate = dpEndDate.getValue() == null ? new Date(0).toLocalDate() : dpEndDate.getValue();
            Calendar endDateCal = Calendar.getInstance();
            endDateCal.set(endDateDate.getYear(), endDateDate.getMonthValue() - 1, endDateDate.getDayOfMonth(), 0, 0,
                    0);

            Expense updatedExpense = new Expense(currentExpense.getId(), tfName.getText(),
                    Integer.parseInt(tfAmount.getText()), endDateCal.getTimeInMillis());

            // No need to specify user here, the ID of the expense in question is used.
            if (new SQLiteConnection().updateExpense(updatedExpense)) {
                // Reset button status back to how it is when the expenseview is entered.
                btnSave.setDisable(false);
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);

                // Update the expense in the users list of expenses so that it corresponds to its updated values.
                AccountManager.getCurrentUser().updateExpense(currentExpense, updatedExpense);

                resetFields();
                refreshTableContent();
            }

        }
    }

    /**
     *
     */

    public void handleDelete() {
        // No need to specify user here, the ID of the expense in question is used.
        if (new SQLiteConnection().deleteExpense(currentExpense)) {
            btnSave.setDisable(false);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);

            // Simply remove the expense from the users list of expenses.
            AccountManager.getCurrentUser().removeExpense(currentExpense);

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
     * Used to validate all expense input fields of either an old expense or a new submission.
     *
     * @return boolean value representing the integrity of the entered expense values
     */

    private boolean inputValidation() {
        // This process is only used to ensure fields have their correct border color (either error red or normal blue).
        tfName.validate();
        tfAmount.validate();
        dpEndDate.validate();

        // Return validity.
        return tfName.validate() && tfAmount.validate() && dpEndDate.validate();
    }

    /**
     * Used to either populate the table view or update it when a new expense has been saved/updated/deleted.
     */

    private void refreshTableContent() {
        // Get the current list of expenses from the users list of expenses.
        ObservableList<Expense> expenses = FXCollections.observableArrayList(
                AccountManager.getCurrentUser().getExpenses()
        );

        tvExpenses.setItems(expenses);
    }

    /**
     * Resets all input fields to their default values and checkboxes to their unchecked state.
     */

    private void resetFields() {
        tfName.reset();
        tfAmount.reset();

        dpEndDate.reset();
        dpEndDate.setDisable(false);

        chebEndDate.setSelected(false);

        currentExpense = null;
    }
}
