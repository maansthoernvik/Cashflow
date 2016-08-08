package controller;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import model.Main;
import view.ExpenseView;
import view.LoanView;
import view.MyEconomyView;
import view.SettingsView;

/**
 * Created by MTs on 06/08/16.
 *
 *
 */

public class MainWindowController {
    @FXML private SplitPane mainSplitPane;

    private Main main;

    private MyEconomyView myEconomy;
    private LoanView loans;
    private ExpenseView expenses;
    private SettingsView settings;

    public void setMain(Main main) {
        this.main = main;
    }

    public void handleLabelMyEconomy() {
        myEconomy = new MyEconomyView();
        setMainContent(myEconomy);
    }

    public void handleLabelLoans() {
        loans = new LoanView();
        setMainContent(loans);
    }

    public void handleLabelExpenses() {
        expenses = new ExpenseView();
        setMainContent(expenses);
    }

    public void handleLabelSettings() {
        settings = new SettingsView();
        setMainContent(settings);
    }

    public void setMainContent(VBox newVBoxContent) {
        mainSplitPane.getItems().remove(1);
        mainSplitPane.getItems().add(newVBoxContent);
    }
}
