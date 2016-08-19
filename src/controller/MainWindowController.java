package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import view.ExpenseView;
import view.LoanView;
import view.MyEconomyView;
import view.SettingsView;

/**
 * Created by MTs on 06/08/16.
 *
 * The MainWindowController is set to the MainWindowViews controller class. It controls what content is displayed by use
 * of the labels to the far left of the application screen. When an item is clicked, new content will be loaded.
 */

public class MainWindowController {

    @FXML private SplitPane mainSplitPane;  // This splitpane is declared in the MainWindowView, hence the @FXML.
    @FXML private Label lblLogout;

    public void setUpLogout(AccountManager accManager) {
        lblLogout.setOnMouseReleased( releaseEvent -> accManager.logout());
    }

    /**
     * Handles the event for when the label "My Economy" is pressed. It sets the current content to that of the
     * MyEconomyView class.
     */

    public void handleLabelMyEconomy() {
        MyEconomyView myEconomy = new MyEconomyView();
        setMainContent(myEconomy);
    }

    /**
     * Handles the event for when the label "Loans" is pressed. It sets the current content to that of the
     * LoanView class.
     */

    public void handleLabelLoans() {
        LoanView loans = new LoanView();
        setMainContent(loans);
    }

    /**
     * Handles the event for when the label "Expenses" is pressed. It sets the current content to that of the
     * ExpenseView class.
     */

    public void handleLabelExpenses() {
        ExpenseView expenses = new ExpenseView();
        setMainContent(expenses);
    }

    /**
     * Handles the event for when the label "Settings" is pressed. It sets the current content to that of the
     * SettingsView class.
     */

    public void handleLabelSettings() {
        SettingsView settings = new SettingsView();
        setMainContent(settings);
    }

    /**
     * Used to change the current content shown. It removes the first position item from the splitpane (which is the
     * content to the right of the separator, the left side has pos. 0) and then adds the VBox sent as a parameter.
     *
     * @param newVBoxContent to be shown in SplitPane
     */

    private void setMainContent(VBox newVBoxContent) {
        mainSplitPane.getItems().remove(1);
        mainSplitPane.getItems().add(newVBoxContent);
    }
}
