package model.objects;

import java.util.ArrayList;

import controller.SQLiteConnection;

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
    private Rent rent;
    private Food food;

    /**
     * Creates a user object and populates it with the users current loans, expenses and so on.
     *
     * @param id of user entry
     * @param name username
     */

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Populates all the user's fields with values
     */

    public void populateUserFields() {
        addAllLoans();
        addAllExpenses();
        addRent();
        addFood();
    }

    /**
     * Adds all loans of the user in the DB to the array list loans.
     */

    public void addAllLoans() {
        loans = new SQLiteConnection().fetchLoans("SELECT * FROM Loans WHERE UserID = ?", id);
    }

    /**
     * Update an existing loan is the user's list of loans.
     */

    public void updateLoan(Loan oldLoan, Loan newLoan) {
        newLoan.performPayments();

        int i = loans.indexOf(oldLoan);

        if (newLoan.getAmount() > 0) {
            loans.set(i, newLoan);
        } else {
            removeLoan(oldLoan);
        }
    }

    /**
     * Remove a loan from the user's list of loans.
     */

    public void removeLoan(Loan loan) {
        int i = loans.indexOf(loan);
        loans.remove(i);
    }

    /**
     * Adds all expenses of the user in the DB to the array list of expenses.
     */

    public void addAllExpenses() {
        expenses = new SQLiteConnection().fetchExpenses("SELECT * FROM Expenses WHERE UserID = ?", id);
    }

    /**
     * Updates an existing expense by taking the old one and replacing it with a new one.
     */

    public void updateExpense(Expense oldExpense, Expense newExpense) {
        int i = expenses.indexOf(oldExpense);
        expenses.set(i, newExpense);
    }

    /**
     * Deletes an expense from the user's list of expenses.
     */

    public void removeExpense(Expense expense) {
        int i = expenses.indexOf(expense);
        expenses.remove(i);
    }

    /**
     * Initializes the rent object of the user.
     */

    public void addRent() {
        rent = new SQLiteConnection().fetchRent("SELECT * FROM Rent WHERE UserID = ?" , id);
    }

    /**
     * Initializes the food object of the user.
     */

    public void addFood() {
        food = new SQLiteConnection().fetchFood("SELECT * FROM Food WHERE UserID = ?;", id);
    }

    /**
     * Setter for user id.
     *
     * @param newId of user
     * @return new id number
     */

    @SuppressWarnings("usused")
    public int setId(int newId) {
        id = newId;
        return id;
    }

    /**
     * Setter for user name.
     *
     * @param newName of user
     * @return new username
     */

    @SuppressWarnings("usused")
    public String setName(String newName) {
        name = newName;
        return name;
    }

    /**
     * Getter for user id.
     *
     * @return user's id
     */

    public int getId() {
        return id;
    }

    /**
     * Getter for user name.
     *
     * @return user's name
     */

    public String getName() {
        return name;
    }

    /**
     * Getter for user's loans.
     *
     * @return an arraylist of all user's loans
     */

    public ArrayList<Loan> getLoans() {
        return loans;
    }

    /**
     * Getter for user's expenses.
     *
     * @return all the user's expenses as an array list
     */

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Getter of user's Rent.
     *
     * @return user's rent
     */

    public Rent getRent() {
        return rent;
    }

    /**
     * Getter of user's food object.
     *
     * @return user's food object
     */

    public Food getFood() {
        return food;
    }
}
