package controller;

import javafx.fxml.FXML;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import model.AccountManager;

/**
 * Created by MTs on 06/08/16.
 *
 * This is the main window's controller that has access to all other controllers of its separate tab views since those
 * tabs have been injected using the fx:include function.
 */

public class MainWindowController {

    private AccountManager accountManager;

    @FXML private TabPane tabPane;

    @FXML private Tab overview;
    @FXML private Tab loans;
    @FXML private Tab expenses;

    @FXML private OverviewTabViewController overviewTabViewController;
    @FXML private LoanTabViewController loanTabViewController;
    @FXML private ExpenseTabViewController expenseTabViewController;

    /**
     * FFU
     *
     * @param accountManager current
     */

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * Naming causes automatic call of this method. Sets all fields of the overview to their respective values.
     */

    @SuppressWarnings("unused")
    public void initialize() {
        overviewTabViewController.refreshOverview();    // Load initial values into overview fields.

        tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable,
                                                                            Tab oldTab, Tab newTab) -> {
            // If the selected tab is the overview, refresh the table's content. This is needed since changes can have
            // been made to expenses and/or loans and those changes do not themselves trigger a refresh.
            if (newTab == overview) {
                overviewTabViewController.refreshOverview();
            }
        });
    }
}