package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import model.Expense;
import model.Loan;
import view.OverviewView;

import java.util.ArrayList;

/**
 * Created by MTs on 06/08/16.
 *
 *
 */

public class MainWindowController {

    private AccountManager accountManager;

    @FXML private TabPane mainTabPane;
    @FXML private Tab overviewTab;

    /**
     *
     * @param accountManager
     */

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public void setUpdateOverview() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab == overviewTab) {
                OverviewView content = (OverviewView) overviewTab.getContent();
                content.refreshOverview();
            }
        });
    }
}
