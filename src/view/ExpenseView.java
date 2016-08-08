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

import java.util.ArrayList;

/**
 * Created by MTs on 06/08/16.
 *
 *
 */

public class ExpenseView extends VBox {

    public ExpenseView() {
        super();
        this.setAlignment(Pos.TOP_LEFT);

        Label lblName = new Label("Name:");
        Label lblAmount = new Label("Amount:");
        Label lblEndDate = new Label("Ends:");

        TextField tfName = new TextField();
        TextField tfAmount = new TextField();
        TextField tfEndDate = new TextField();

        HBox hbFirst = new HBox();
        hbFirst.getChildren().addAll(lblName, lblAmount);
        hbFirst.setAlignment(Pos.TOP_LEFT);

        HBox hbSecond = new HBox();
        hbSecond.getChildren().addAll(tfName, tfAmount);
        hbSecond.setAlignment(Pos.TOP_LEFT);

        TableView<Expense> tvExpenses = new TableView<>();
        tvExpenses.setEditable(true);

        TableColumn<Expense, String> tcolName = new TableColumn<>("Name");
        TableColumn<Expense, Integer> tcolAmount = new TableColumn<>("Amount");

        tcolName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        tcolAmount.setCellValueFactory(new PropertyValueFactory<>("Amount"));

        tvExpenses.setRowFactory( tv -> {
            TableRow<Expense> row = new TableRow<>();
            row.setOnMouseClicked( clickEvent -> {
                if ((clickEvent.getClickCount() == 1) && (!row.isEmpty())) {
                    Expense expense = row.getItem();
                    tfName.setText(expense.getName());
                    tfAmount.setText("" + expense.getAmount());
                    tfEndDate.setText("" + expense.getEndDate().toString());
                }
            });
            return row;
        });

        SQLiteConnection SQLiteConn = new SQLiteConnection();

        ArrayList<Expense> expenses;
        expenses = SQLiteConn.fetchExpenses("SELECT * FROM Expenses WHERE User = ?", "Alpha");
        ObservableList<Expense> obsExpenses = FXCollections.observableArrayList(expenses);

        tvExpenses.setItems(obsExpenses);
        tvExpenses.getColumns().addAll(
                tcolName, tcolAmount
        );
        tvExpenses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        this.getChildren().addAll(tvExpenses, hbFirst, hbSecond, lblEndDate, tfEndDate);
    }
}
