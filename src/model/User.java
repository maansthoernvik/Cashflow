package model;

import controller.SQLiteConnection;
import model.DateTime.TimeTracking;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

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

        for (Loan loan : loans) {
            if (loan.getNextPayment() > 86400000 && loan.getNextPayment() < TimeTracking.getCurrentDate()) {
                while (loan.getNextPayment() < TimeTracking.getCurrentDate()) {
                    loan.setAmount(loan.getAmount() - loan.getAmortizationAmount());

                    LocalDate nextPaymentDate = new Date(loan.getNextPayment()).toLocalDate();

                    int year = nextPaymentDate.getYear();
                    int month = nextPaymentDate.getMonthValue();
                    int day = nextPaymentDate.getDayOfMonth();

                    boolean isLeapYear = TimeTracking.isLeapYear(year);

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
                SQLiteConn.updateLoan(loan);
            }
        }
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

    /**
     *
     */


}
