//package view;
//
//import model.AccountManager;
//import controller.SQLiteConnection;
//import model.objects.Expense;
//import model.input.ModdedDatePicker;
//import model.input.ModdedTextField;
//import model.input.Regex;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//
//import java.sql.Date;
//import java.time.LocalDate;
//import java.util.Calendar;
//
///**
// * Created by MTs on 06/08/16.
// *
// * This view enables the user to view and manage his or her expenses. Add new, update and delete.
// */
//
//public class ExpenseView extends VBox {
//
//    private SQLiteConnection SQLiteConn;
//    private Expense currentExpense;
//
//    private TableView<Expense> tvExpenses;
//    private ModdedTextField tfName;
//    private ModdedTextField tfAmount;
//    private ModdedDatePicker dpEndDate;
//
//    private CheckBox chebEndDate;
//
//    /**
//     * Default constructor for ExpenseViews, populating the VBox with all items the view contains.
//     */
//
//    @SuppressWarnings("unchecked")
//    public ExpenseView() {
//        super();
//
//        // Connection object for use with the SQLite database.
//        SQLiteConn = new SQLiteConnection();
//
//        // For CellValueFactories it is extremely important to keep naming consistent with getters of the datatype.
//        // If this is spelled wrong, the value will not be gotten.
//
//        tvExpenses = new TableView<>();
//        tvExpenses.setEditable(true);
//        tvExpenses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        TableColumn<Expense, String> tcolName = new TableColumn<>("Name");
//        tcolName.setCellValueFactory(new PropertyValueFactory<>("Name"));
//        TableColumn<Expense, Integer> tcolAmount = new TableColumn<>("Amount");
//        tcolAmount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
//        tvExpenses.getColumns().addAll(
//                tcolName, tcolAmount
//        );
//        refreshTableContent();
//
//        // Textfields and datepickers are loaded with Regex values from the Enum class. This defines the type of data
//        // they will handle. If the wrong regex is entered, the border will turn red upon wrong data entered and you
//        // will not be able to save.
//
//        tfName = new ModdedTextField(Regex.NAME);
//        tfAmount = new ModdedTextField(Regex.LESSERAMOUNT);
//
//        dpEndDate = new ModdedDatePicker(Regex.DATE);
//
//        chebEndDate = new CheckBox("No end");
//        chebEndDate.setOnAction( actionEvent -> {
//            if (chebEndDate.isSelected()) {
//                dpEndDate.setDisable(true);
//                dpEndDate.setValue(null);
//            } else {
//                dpEndDate.setDisable(false);
//            }
//        });
//
//        // Buttons for managing the expenses.
//
//        Button btnSaveExpense = new Button("Save");
//        Button btnUpdateExpense = new Button("Update");
//        Button btnClearFields = new Button("Clear");
//        Button btnDeleteExpense = new Button("Delete");
//
//        btnSaveExpense.setOnMouseReleased( releaseEvent -> {
//            if (inputValidation()) {    // If all input fields have correct values.
//                // The date entered can either be left empty (i.e not is use) or with an actual value. In either case,
//                // the values needs to be converted into milliseconds since epoch.
//
//                // 1. Create a local date.
//                LocalDate endDateDate = dpEndDate.getValue() == null ? new Date(0).toLocalDate() : dpEndDate.getValue();
//                // 2. Create Calendar instance.
//                Calendar endDateCal = Calendar.getInstance();
//                // 3. Set Calendar instance to date gotten from datepicker.
//                endDateCal.set(endDateDate.getYear(), endDateDate.getMonthValue() - 1, endDateDate.getDayOfMonth(), 0,
//                        0, 0);
//
//                // All fields are converted into their respective data types as all types are strings until this point.
//                // Doubles and its need to be parsed before submission into the DB. Calendar values are converted into
//                // longs by use of getTimeInMillis() method from the Calendar class.
//                Expense insertedExpense = new Expense(tfName.getText(), Integer.parseInt(tfAmount.getText()),
//                        endDateCal.getTimeInMillis());
//
//                if (SQLiteConn.insertExpense(insertedExpense, AccountManager.getCurrentUser().getId())) {
//                    // Since a new expense has been inserted into the DB, all expenses now need to be re-loaded into the
//                    // current users list of expenses. This is because when inserted, the expenses are not created with
//                    // their ID's, so a full value expense is not inserted into the list and it hence cannot be deleted
//                    // (without the expense's ID number).
//                    AccountManager.getCurrentUser().addAllExpenses();
//
//                    // Reset all field after submission into the DB.
//                    resetFields();
//                    refreshTableContent();
//                }
//            }
//        });
//
//        // Update button disabled by default, enabled when an expense is selected.
//        btnUpdateExpense.setDisable(true);
//        btnUpdateExpense.setOnMouseReleased( releaseEvent -> {
//            if (inputValidation()) {    // If all input fields have correct values.
//                // Reset button status back to how it is when the expenseview is entered.
//                btnSaveExpense.setDisable(false);
//                btnUpdateExpense.setDisable(true);
//                btnDeleteExpense.setDisable(true);
//
//                // See saving process used for btnSaveExpense.
//                LocalDate endDateDate = dpEndDate.getValue() == null ? new Date(0).toLocalDate() : dpEndDate.getValue();
//                Calendar endDateCal = Calendar.getInstance();
//                endDateCal.set(endDateDate.getYear(), endDateDate.getMonthValue() - 1, endDateDate.getDayOfMonth(), 0,
//                        0, 0);
//
//                Expense updatedExpense = new Expense(currentExpense.getId(), tfName.getText(),
//                        Integer.parseInt(tfAmount.getText()), endDateCal.getTimeInMillis());
//
//                // No need to specify user here, the ID of the expense in question is used.
//                if (SQLiteConn.updateExpense(updatedExpense)) {
//                    // Update the expense in the users list of expenses so that it corresponds to its updated values.
//                    AccountManager.getCurrentUser().updateExpense(currentExpense, updatedExpense);
//
//                    resetFields();
//                    refreshTableContent();
//                }
//
//            }
//        });
//
//        btnClearFields.setOnMouseReleased( releaseEvent ->  {
//            btnSaveExpense.setDisable(false);
//            btnUpdateExpense.setDisable(true);
//            btnDeleteExpense.setDisable(true);
//
//            resetFields();
//        });
//
//        // Delete button disabled by default, enabled when an expense is selected.
//        btnDeleteExpense.setDisable(true);
//        btnDeleteExpense.setOnMouseReleased( releaseEvent -> {
//            btnSaveExpense.setDisable(false);
//            btnUpdateExpense.setDisable(true);
//            btnDeleteExpense.setDisable(true);
//
//            // No need to specify user here, the ID of the expense in question is used.
//            if (SQLiteConn.deleteExpense(currentExpense)) {
//                // Simply remove the expense from the users list of expenses.
//                AccountManager.getCurrentUser().removeExpense(currentExpense);
//
//                resetFields();
//                refreshTableContent();
//            }
//        });
//
//        // TableView populated by all expenses from the database.
//        tvExpenses.setRowFactory( tv -> {
//            TableRow<Expense> row = new TableRow<>();
//            row.setOnMouseClicked( clickEvent -> {  // If an item is clicked.
//                if ((clickEvent.getClickCount() == 1) && (!row.isEmpty())) {    // Item was clicked once and the row is
//                                                                                // not empty.
//                    // Enable update and delete buttons and disable save.
//                    btnSaveExpense.setDisable(true);
//                    btnUpdateExpense.setDisable(false);
//                    btnDeleteExpense.setDisable(false);
//
//                    currentExpense = row.getItem();     // Assign the currently selected loan to global variable.
//
//                    // Set all expense fields to the values of the currently selected expense.
//                    tfName.setText(currentExpense.getName());
//                    tfAmount.setText("" + currentExpense.getAmount());
//
//                    // TODO - How shall end dates be handled for expenses?
//                    if (currentExpense.getEndDate() > 86400000) {
//                        dpEndDate.setDisable(false);
//                        dpEndDate.setValue(new Date(currentExpense.getEndDate()).toLocalDate());
//                        chebEndDate.setSelected(false);
//                    } else {
//                        dpEndDate.setDisable(true);
//                        dpEndDate.setValue(null);
//                        chebEndDate.setSelected(true);
//                    }
//                }
//            });
//            return row;
//        });
//
//        // Creates HBoxes for different rows of this class (since >this< extends the VBox class) and then it is simply
//        // a matter of adding them all in order - hence the naming using numbers. Labels are added directly since there
//        // was no reason to instantiate them anywhere else.
//        HBox hbFirst = new HBox();
//        hbFirst.getChildren().addAll(new Label("Name:"), new Label("Amount:"));
//
//        HBox hbSecond = new HBox();
//        hbSecond.getChildren().addAll(tfName, tfAmount);
//
//        HBox hbThird = new HBox();
//        hbThird.getChildren().addAll(dpEndDate, chebEndDate);
//
//        HBox hbFourth = new HBox();
//        hbFourth.getChildren().addAll(btnSaveExpense, btnUpdateExpense, btnDeleteExpense, btnClearFields);
//
//        // Adding all of the above HBoxes to >this< VBox.
//        getChildren().addAll(tvExpenses, hbFirst, hbSecond, new Label("Ends:"), hbThird, hbFourth);
//    }
//
//    /**
//     * Getter getter of the current expense.
//     *
//     * @return currently loaded expense
//     */
//
//    @SuppressWarnings("unused")
//    public Expense getCurrentExpense() {
//        return currentExpense;
//    }
//
//    /**
//     * Used to validate all expense input fields of either an old expense or a new submission.
//     *
//     * @return boolean value representing the integrity of the entered expense values
//     */
//
//    private boolean inputValidation() {
//        // This process is only used to ensure fields have their correct border color (either error red or normal blue).
//        tfName.validate();
//        tfAmount.validate();
//        dpEndDate.validate();
//
//        // Return validity.
//        return tfName.validate() && tfAmount.validate() && dpEndDate.validate();
//    }
//
//    /**
//     * Used to either populate the table view or update it when a new expense has been saved/updated/deleted.
//     */
//
//    private void refreshTableContent() {
//        // Get the current list of expenses from the users list of expenses.
//        ObservableList<Expense> expenses = FXCollections.observableArrayList(
//                AccountManager.getCurrentUser().getExpenses()
//        );
//
//        tvExpenses.setItems(expenses);
//    }
//
//    /**
//     * Resets all input fields to their default values and checkboxes to their unchecked state.
//     */
//
//    private void resetFields() {
//        tfName.reset();
//        tfAmount.reset();
//
//        dpEndDate.reset();
//        dpEndDate.setDisable(false);
//
//        chebEndDate.setSelected(false);
//
//        currentExpense = null;
//    }
//}
