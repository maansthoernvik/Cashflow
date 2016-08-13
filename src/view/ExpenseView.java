package view;

import controller.SQLiteConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Expense;
import model.Input.ModdedTextField;
import model.Input.Regex;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by MTs on 06/08/16.
 *
 *
 */

public class ExpenseView extends VBox {

    private SQLiteConnection SQLiteConn;
    private Expense currentExpense;

    private TableView<Expense> tvExpenses;
    private ModdedTextField tfName;
    private ModdedTextField tfAmount;
    private DatePicker dpEndDate;

    public ExpenseView() {
        super();
        this.setAlignment(Pos.TOP_LEFT);

        SQLiteConn = new SQLiteConnection();

        tvExpenses = new TableView<>();
        tvExpenses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Expense, String> tcolName = new TableColumn<>("Name");
        tcolName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        TableColumn<Expense, Integer> tcolAmount = new TableColumn<>("Amount");
        tcolAmount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        tvExpenses.getColumns().addAll(
                tcolName, tcolAmount
        );
        refreshTableContent();

        tfName = new ModdedTextField();
        tfName.setUpValidation(Regex.NAME);
        tfAmount = new ModdedTextField();
        tfAmount.setUpValidation(Regex.AMOUNT);

        dpEndDate = new DatePicker();

        Button btnSaveExpense = new Button("Save");
        Button btnUpdateExpense = new Button("Update");
        Button btnClearFields = new Button("Clear");
        Button btnDeleteExpense = new Button("Delete");

        btnSaveExpense.setOnMouseReleased( releaseEvent -> {
            LocalDate endDateDate = dpEndDate.getValue() == null ? LocalDate.ofEpochDay(0) : dpEndDate.getValue();
            Calendar endDateCal = Calendar.getInstance();
            endDateCal.set(endDateDate.getYear(), endDateDate.getMonthValue() - 1, endDateDate.getDayOfMonth());

            Expense insertedExpense = new Expense(tfName.getText(), Integer.parseInt(tfAmount.getText()),
                    endDateCal.getTimeInMillis());

            SQLiteConn.insertExpense(insertedExpense, "Alpha");

            clearFields();
            refreshTableContent();
        });

        btnUpdateExpense.setDisable(true);
        btnUpdateExpense.setOnMouseReleased( releaseEvent -> {
            if (currentExpense != null) {
                btnSaveExpense.setDisable(false);
                btnUpdateExpense.setDisable(true);
                btnClearFields.setDisable(true);
                btnDeleteExpense.setDisable(true);

                LocalDate endDateDate = dpEndDate.getValue() == null ? LocalDate.ofEpochDay(0) : dpEndDate.getValue();
                Calendar endDateCal = Calendar.getInstance();
                endDateCal.set(endDateDate.getYear(), endDateDate.getMonthValue() - 1, endDateDate.getDayOfMonth());

                Expense updatedExpense = new Expense(currentExpense.getId(), currentExpense.getName(),
                        Integer.parseInt(tfAmount.getText()), endDateCal.getTimeInMillis());

                SQLiteConn.updateExpense(updatedExpense);

                clearFields();
                refreshTableContent();
            }
        });

        btnClearFields.setDisable(true);
        btnClearFields.setOnMouseReleased( releaseEvent ->  {
            btnSaveExpense.setDisable(false);
            btnUpdateExpense.setDisable(true);
            btnClearFields.setDisable(true);
            btnDeleteExpense.setDisable(true);

            clearFields();
        });

        btnDeleteExpense.setDisable(true);
        btnDeleteExpense.setOnMouseReleased( releaseEvent -> {
            btnSaveExpense.setDisable(false);
            btnUpdateExpense.setDisable(true);
            btnClearFields.setDisable(true);
            btnDeleteExpense.setDisable(true);

            SQLiteConn.deleteExpense(currentExpense);

            clearFields();
            refreshTableContent();
        });

        tvExpenses.setRowFactory( tv -> {
            TableRow<Expense> row = new TableRow<>();
            row.setOnMouseClicked( clickEvent -> {
                if ((clickEvent.getClickCount() == 1) && (!row.isEmpty())) {
                    Expense expense = row.getItem();
                    currentExpense = expense;

                    tfName.setText(expense.getName());
                    tfAmount.setText("" + expense.getAmount());
                    dpEndDate.setValue(new Date(expense.getEndDate()).toLocalDate());

                    btnSaveExpense.setDisable(true);
                    btnUpdateExpense.setDisable(false);
                    btnClearFields.setDisable(false);
                    btnDeleteExpense.setDisable(false);
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
        hbThird.getChildren().addAll(btnSaveExpense, btnUpdateExpense, btnClearFields, btnDeleteExpense);

        this.getChildren().addAll(tvExpenses, hbFirst, hbSecond, new Label("Ends:"), dpEndDate, hbThird);
    }

    public Expense getCurrentExpense() {
        return currentExpense;
    }

    private TableView<Expense> refreshTableContent() {
        ObservableList<Expense> expenses = FXCollections.observableArrayList(
                SQLiteConn.fetchExpenses("SELECT * FROM Expenses WHERE User = ?", "Alpha")
        );

        tvExpenses.setEditable(true);
        tvExpenses.setItems(expenses);

        return tvExpenses;
    }

    private void clearFields() {
        tfName.clear();
        tfAmount.clear();
        dpEndDate.setValue(null);

        currentExpense = null;
    }
}
