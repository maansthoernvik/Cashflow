package model;

import controller.SQLiteConnection;

import java.util.ArrayList;

/**
 * Created by MTs on 19/08/16.
 *
 * Saves the current users loans and expenses in lists to prevent the need to query the DB all the time.
 */

public class User {

    private int id;
    private String name;
    private ArrayList<Loan> loans;
    private ArrayList<Expense> expenses;

    /**
     *
     * @param id
     * @param name
     */

    public User(int id, String name) {
        this.id = id;
        this.name = name;
        addAllLoans();
        addAllExpenses();
    }

    /**
     *
     */

    public void addAllLoans() {
        SQLiteConnection SQLiteConn = new SQLiteConnection();
        loans = SQLiteConn.fetchLoans("SELECT * FROM Loans WHERE UserID = ?", id);
    }

    /**
     *
     */

    public void addLoan(Loan loan) {
        loans.add(loan);
    }

    /**
     *
     */

    public void updateLoan(Loan oldLoan, Loan newLoan) {
        int i = loans.indexOf(oldLoan);
        loans.set(i, newLoan);
    }

    /**
     *
     */

    public void removeLoan(Loan loan) {
        int i = loans.indexOf(loan);
        loans.remove(i);
    }

    /**
     *
     */

    public ArrayList<Loan> getLoans() {
        return loans;
    }

    /**
     *
     */

    public void addAllExpenses() {
        SQLiteConnection SQLiteConn = new SQLiteConnection();
        expenses = SQLiteConn.fetchExpenses("SELECT * FROM Expenses WHERE UserID = ?", id);
    }

    /**
     *
     * @param expense
     */

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    /**
     *
     */

    public void updateExpense(Expense oldExpense, Expense newExpense) {
        int i = expenses.indexOf(oldExpense);
        expenses.set(i, newExpense);
    }

    /**
     *
     */

    public void removeExpense(Expense expense) {
        int i = expenses.indexOf(expense);
        expenses.remove(i);
    }

    /**
     *
     */

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    /**
     *
     * @param newId
     * @return
     */

    public int setId(int newId) {
        id = newId;
        return id;
    }

    /**
     *
     */

    public String setName(String newName) {
        name = newName;
        return name;
    }

    /**
     *
     */

    public int getId() {
        return id;
    }

    /**
     *
     */

    public String getName() {
        return name;
    }
}
