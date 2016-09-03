package model.objects;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import model.SQLiteConnection;
import model.time.TimeTracking;

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
        addAllLoans();
        addAllExpenses();
        addRent();
        addFood();
    }

    /**
     * Adds all loans of the user in the DB to the array list loans.
     */

    public void addAllLoans() {
        SQLiteConnection SQLiteConn = new SQLiteConnection();
        loans = SQLiteConn.fetchLoans("SELECT * FROM Loans WHERE UserID = ?", id);

        // Loan payment dates are checked upon retrieval to see if amortization amounts need to be deducted in order
        // to display the correct amounts.
        checkPaymentDates();
    }

    /**
     * Used to check the payment dates of all loans and deduct amortization amount for them to be correctly represented
     * in the application.
     */

    private void checkPaymentDates() {
        for (Loan loan : loans) {
            // If the next payment date is after the epoch day, but before today's date - continue.
            if (loan.getNextPayment() > 86400000 && loan.getNextPayment() < TimeTracking.getCurrentDate()) {
                // As long as the next payment date is before the current date - continue.
                while (loan.getNextPayment() < TimeTracking.getCurrentDate()) {
                    // Deduct amortization amount.
                    loan.setAmount(loan.getAmount() - loan.getAmortizationAmount());

                    // Change the next payment date by first getting the current one:
                    LocalDate nextPaymentDate = new Date(loan.getNextPayment()).toLocalDate();

                    int year = nextPaymentDate.getYear();           // Old next payment year.
                    int month = nextPaymentDate.getMonthValue();    // Old next payment month.
                    int day = nextPaymentDate.getDayOfMonth();      // Old next payment day.

                    // Is it a leap year?
                    boolean isLeapYear = TimeTracking.isLeapYear(year);

                    // Depending on what month it is, there will be different payment days.
                    // TODO - If the next payment day is set to 30, make it carry over so that if it passes february it
                    // TODO - is set to 28/29 and then in the next month it is set back to 30!
                    switch (month) {
                        case 1:
                            month = 2;
                            if (isLeapYear) {
                                day = day > 29 ? 29 : day;
                            } else {
                                day = day > 28 ? 28 : day;
                            }
                            break;
                        case 2:
                            month = 3;
                            break;
                        case 3:
                            month = 4;
                            day = day > 30 ? 30 : day;
                            break;
                        case 4:
                            month = 5;
                            break;
                        case 5:
                            month = 6;
                            day = day > 30 ? 30 : day;
                            break;
                        case 6:
                            month = 7;
                            break;
                        case 7:
                            month = 8;
                            break;
                        case 8:
                            month = 9;
                            day = day > 30 ? 30 : day;
                            break;
                        case 9:
                            month = 10;
                            break;
                        case 10:
                            month = 11;
                            day = day > 30 ? 30 : day;
                            break;
                        case 11:
                            month = 12;
                            break;
                        case 12:
                            year += 1;
                            month = 1;
                    }

                    Calendar nextPaymentCal = Calendar.getInstance();
                    nextPaymentCal.set(year, month - 1, day, 0, 0, 0);
                    loan.setNextPayment(nextPaymentCal.getTimeInMillis());
                }
                new SQLiteConnection().updateLoan(loan);    // Updates the loan after while loop comes to an end.
            }
        }
    }

    /**
     * Add a loan to the User's list of loans.
     */

    public void addLoan(Loan loan) {
        loans.add(loan);

        checkPaymentDates();
    }

    /**
     * Update an existing loan is the user's list of loans.
     */

    public void updateLoan(Loan oldLoan, Loan newLoan) {
        int i = loans.indexOf(oldLoan);
        loans.set(i, newLoan);

        checkPaymentDates();
    }

    /**
     * Remove a loan from the user's list of loans.
     */

    public void removeLoan(Loan loan) {
        int i = loans.indexOf(loan);
        loans.remove(i);
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
     * Adds all expenses of the user in the DB to the array list of expenses.
     */

    public void addAllExpenses() {
        SQLiteConnection SQLiteConn = new SQLiteConnection();
        expenses = SQLiteConn.fetchExpenses("SELECT * FROM Expenses WHERE UserID = ?", id);
    }

    /**
     * Adds an expense to the user's list of expenses.
     *
     * @param expense added
     */

    public void addExpense(Expense expense) {
        expenses.add(expense);
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
     * Getter for user's expenses.
     *
     * @return all the user's expenses as an array list
     */

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Initializes the rent object of the user.
     */

    public void addRent() {
        SQLiteConnection SQLiteConn = new SQLiteConnection();
        rent = SQLiteConn.fetchRent("SELECT * FROM Rent WHERE UserID = ?" , id);
    }

    /**
     * Updates the current rent cost of the user's rent object.
     *
     * @param newAmount of rent
     */

    public void updateRent(int newAmount) {
        rent.setAmount(newAmount);
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
     * Initializes the food object of the user.
     */

    public void addFood() {
        SQLiteConnection SQLiteConn = new SQLiteConnection();
        food = SQLiteConn.fetchFood("SELECT * FROM Food WHERE UserID = ?;", id);
    }

    /**
     * Updates the current food cost of the user's food object.
     *
     * @param newAmount cost of food
     */

    public void updateFood(int newAmount) {
        food.setAmount(newAmount);
    }

    /**
     * Getter of user's food object.
     *
     * @return user's food object
     */

    public Food getFood() {
        return food;
    }

    /**
     * Setter for user id.
     *
     * @param newId of user
     * @return new id number
     */

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
}
