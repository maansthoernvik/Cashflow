package controller;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import model.AccountManager;

/**
 * Created by MTs on 06/08/16.
 *
 *
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
     * Naming causes automatic call of this method.
     */

    @SuppressWarnings("unused")
    public void initialize() {
        overviewTabViewController.refreshOverview();

        tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable,
                                                                            Tab oldTab, Tab newTab) -> {
            if (newTab == overview) {
                overviewTabViewController.refreshOverview();
            } else if (newTab == loans) {
                loanTabViewController.refreshTableContent();
            }
        });
    }

    /**
     *
     * @param accountManager
     */

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }
}
