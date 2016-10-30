package testing;

import controller.SQLiteConnection;
import model.AccountManager;
import model.objects.*;
import model.time.TimeTracking;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import static oracle.jrockit.jfr.events.Bits.intValue;

/**
 * Created by MTs on 19/09/16.
 *
 * Testing inserts of Records.
 */

public class TestInsert {

    public static void testRecord() {
        User user = new User(1, "alpha");   // Authentication returns a matching user of what has been entered
        // into login fields.
        // Fetch User will return null upon error.
        if (user != null) {
            // Init loans here since it is going to be used at some point, might as well fix it here...
            ArrayList<Loan> loans;

            // Check if new records of previous month's expenses need to be created. This checks the time of the last
            // session against the current date.
            int shifts = TimeTracking.howManyMonthShifts();

            // If shifts is bigger then 0, then there needs to be created new records.
            if (shifts > 0) {

                // Fill loans.
                loans = new SQLiteConnection().fetchLoans("SELECT * FROM Loans WHERE UserID = ?", user.getId());
                // Get expenses, only gotten in case of new shifts.
                ArrayList<Expense> expenses = new SQLiteConnection().fetchExpenses(
                        "SELECT * FROM Expenses WHERE UserID = ?", user.getId());

                // This is done once to be able to track the progress up until today's date and not add stuff that has
                // an earlier end date. Needs to be outside for loop!
                long currentDateShift = TimeTracking.getLastSessionLastDayOfMonth();

                // For every shift of month, one record needs to be inserted. For-loop iteration counter checked OK!
                for (int i = shifts; i > 0; i--) {
                    System.out.print("Iteration " + i + " of shift loop. ");
                    int loanTotal = 0, expenseTotal = 0;     // Declaring totals for this iteration.

                    // Start by adding loan values to loanTotal:
                    for (Loan loan : loans) {
                        // Loan amount might be zero, in which case do not add it. It could become zero during
                        // calculations. Also, check if the payment date has been met or passed.
                        if (currentDateShift >= loan.getNextPayment() && loan.getAmount() > 0) {
                            // Add the current iteration's loan amount to loanTotal.
                            loanTotal += intValue((loan.getAmount() * loan.getInterestRate() * 0.01) / 12) +
                                    loan.getAmortizationAmount();

                            // Adds a month to Loan
                            // TODO fix loans to also handle other update frequencies than one month...
                            loan.setNextPayment(TimeTracking.addOneMonth(loan.getNextPayment(), loan.getDayOffset()));

                            // Deducts amortization amount for next iteration of shift-loop (top level loop).
                            loan.setAmount(loan.getAmount() - loan.getAmortizationAmount());
                        }
                    }

                    for (Expense expense : expenses) {
                        // Keep adding the expenses amounts until the end date of the expense is met. Expenses with
                        // dates before 86400000 are set to "no end date" and their amounts are added all the way.
                        if (expense.getEndDate() < 86400000 || currentDateShift < expense.getEndDate()) {
                            expenseTotal += expense.getAmount();
                        }
                    }

                    Rent rent = new SQLiteConnection().fetchRent("SELECT * FROM Rent WHERE UserID = ?", user.getId());
                    if (rent == null) {
                        rent = new Rent();
                    }

                    Food food = new SQLiteConnection().fetchFood("SELECT * FROM Food WHERE UserID = ?;", user.getId());
                    if (food == null) {
                        food = new Food();
                    }

                    /*
                    ORDER OF INSERTIONS!

                     1. Loans
                     2. Expenses
                     3. Rent
                     4. Food

                     */

                    new SQLiteConnection().insertRecord(new Record(loanTotal, currentDateShift),
                                                        "LoanRecords", user.getId());

                    new SQLiteConnection().insertRecord(new Record(expenseTotal, currentDateShift),
                                                        "ExpenseRecords", user.getId());

                    new SQLiteConnection().insertRecord(new Record(rent.getAmount(), currentDateShift),
                                                        "RentRecords", user.getId());

                    new SQLiteConnection().insertRecord(new Record(food.getAmount(), currentDateShift),
                                                        "FoodRecords", user.getId());

                    LocalDate currentDate = new Date(currentDateShift).toLocalDate();   // Used for record creation
                    // print.

                    // After records created, step forward one month in time. 0 is for always save with the last day of
                    // month.
                    currentDateShift = TimeTracking.addOneMonth(currentDateShift, 0);

                    System.out.println("The month of " + currentDate.getYear() + "/" + currentDate.getMonthValue() + "/"
                            + currentDate.getDayOfMonth() + " had the following total: " + (loanTotal + expenseTotal +
                            rent.getAmount() + food.getAmount()));
                }
            }

            // TODO test extracting records here!

            // TODO test extracting records here!
        }
    }
}
