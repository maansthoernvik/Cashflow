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
import model.objects.Expense;
import model.objects.Food;
import model.objects.Rent;

/**
 * Created by MTs on 26/08/16.
 *
 * This is the expense tab's controller.
 */

public class ExpenseTabViewController {

    private Expense currentExpense;                 // To keep track of which expense is currently selected.

    @FXML private TableView<Expense> tvExpenses;

    @FXML private ModdedTextField tfName;
    @FXML private ModdedTextField tfAmount;
    @FXML private ModdedTextField tfRent;
    @FXML private ModdedTextField tfFood;

    @FXML private ModdedDatePicker dpEndDate;

    @FXML private CheckBox chebEndDate;

    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;

    /**
     * Automatically called when this controllers associated FXML is injected in the MainWindowView's fx:include.
     */

    @SuppressWarnings("unused")
    public void initialize() {
        // Default setting is disabled since you should not be able to update/delete an expense when nothing is
        // selected.
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        // Sets up what type of input is accepted. See Regex class for details of regular expression.
        tfName.setUpValidation(Regex.NAME);
        tfAmount.setUpValidation(Regex.AMOUNT);
        tfRent.setUpValidation(Regex.AMOUNT);
        tfFood.setUpValidation(Regex.AMOUNT);

        dpEndDate.setUpValidation(Regex.DATE);

        chebEndDate.setText("No end");
        chebEndDate.setOnAction( actionEvent -> {
            if (chebEndDate.isSelected()) {     // When checkbox is selected, datepicker is disabled.
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
        // Populate all fields with values gotten from the current user (from AccountManager).
        refreshRent();
        refreshFood();
        refreshTableContent();
    }

    /**
     * Handles the update rent button.
     */

    public void handleRent() {
        if (rentValidation()) {     // If and only if all fields have correct input (according to Regex.x).
            Rent newRent = new Rent(Integer.parseInt(tfRent.getText()));    // Create a rent object and load value.

            if (AccountManager.getCurrentUser().getRent() == null) {        // Has the user previously saved rent?: no.
                if (new SQLiteConnection().insertRent(newRent, AccountManager.getCurrentUser().getId())) {  // Insert.
                    AccountManager.getCurrentUser().addRent();              // Add user objects rent variable.
                }
            } else {
                if (new SQLiteConnection().updateRent(newRent, AccountManager.getCurrentUser().getId())) {  // Update.
                    AccountManager.getCurrentUser().getRent().setAmount(newRent.getAmount());   // Update user objects
                                                                                                // rent.
                }
            }
        }
    }

    /**
     * Handles the update food button.
     */

    public void handleFood() {
        // See procedure for saving and updating rent above.
        if (foodValidation()) {
            Food newFood = new Food(Integer.parseInt(tfFood.getText()));

            if (AccountManager.getCurrentUser().getFood() == null) {
                if (new SQLiteConnection().insertFood(newFood, AccountManager.getCurrentUser().getId())) {
                    AccountManager.getCurrentUser().addFood();
                }
            } else {
                if (new SQLiteConnection().updateFood(newFood, AccountManager.getCurrentUser().getId())) {
                    AccountManager.getCurrentUser().getFood().setAmount(newFood.getAmount());
                }
            }
        }
    }

    /**
     * Handles save button.
     */

    public void handleSave() {
        if (expenseValidation()) {    // If all input fields have correct values.
            // The date entered can either be left empty (i.e not is use) or with an actual value. In either case,
            // the values needs to be converted into milliseconds since epoch.

            // 1. Create a local date, new Date(0) creates an epoch date.
            LocalDate endDateDate = dpEndDate.getValue() == null ? new Date(0).toLocalDate() : dpEndDate.getValue();
            // 2. Create Calendar instance.
            Calendar endDateCal = Calendar.getInstance();
            // 3. Set Calendar instance to date gotten from datepicker.
            endDateCal.set(endDateDate.getYear(), endDateDate.getMonthValue() - 1, endDateDate.getDayOfMonth(), 1, 0,
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
     * Handles the update button for expenses.
     */

    public void handleUpdate() {
        if (expenseValidation()) {    // If all input fields have correct values.
            // See saving process used for btnSaveExpense.
            LocalDate endDateDate = dpEndDate.getValue() == null ? new Date(0).toLocalDate() : dpEndDate.getValue();
            Calendar endDateCal = Calendar.getInstance();
            endDateCal.set(endDateDate.getYear(), endDateDate.getMonthValue() - 1, endDateDate.getDayOfMonth(), 1, 0,
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
     * Handles the delete button.
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
     * Handles the clear button.
     */

    public void handleClear() {
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        resetFields();
    }

    /**
     * Check if value of rent field is valid.
     *
     * @return true if value of rent field is valid
     */

    private boolean rentValidation() {
        tfRent.validate();

        return tfRent.validate();
    }

    /**
     * Checks validity of food field.
     *
     * @return true if value of food field is valid.
     */

    private boolean foodValidation() {
        tfFood.validate();

        return tfFood.validate();
    }

    /**
     * Used to validate all expense input fields of either an old expense or a new submission.
     *
     * @return boolean value representing the integrity of the entered expense values
     */

    private boolean expenseValidation() {
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
     * Populates or re-populates the rent field.
     */

    private void refreshRent() {
        Rent rent = AccountManager.getCurrentUser().getRent();

        if (rent != null) {
            tfRent.setText("" + rent.getAmount());
        } else {
            tfRent.setText("0");
        }
    }

    /**
     * Populates or re-populates the food field.
     */

    private void refreshFood() {
        Food food = AccountManager.getCurrentUser().getFood();

        if (food != null) {
            tfFood.setText("" + food.getAmount());
        } else {
            tfFood.setText("0");
        }
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
