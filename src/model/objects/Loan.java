package model.objects;

import controller.SQLiteConnection;
import model.time.TimeTracking;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by MTs on 03/08/16.
 *
 * Used to represent Loans.
 */

public class Loan {

    private int id;

    private String name;
    private int amount;             // The loan amount
    private double interestRate;    // The current interest
    private int amortizationAmount;
    private long nextPayment;
    private long boundTo;

    /**
     * Constructor used to insert new loans.
     *
     * @param name of loan
     * @param amount of loan
     * @param interestRate of loan
     * @param amortizationAmount of loan
     * @param nextPayment of loan
     * @param boundTo of loan
     */

    public Loan(String name, int amount, double interestRate, int amortizationAmount, long nextPayment, long boundTo) {
        this.name = name;
        this.amount = amount;
        this.interestRate = interestRate;
        this.amortizationAmount = amortizationAmount;
        this.nextPayment = nextPayment;
        this.boundTo = boundTo;
    }

    /**
     * Constructor used to alter old loans.
     *
     * @param id of loan
     * @param name of loan
     * @param amount of loan
     * @param interestRate of loan
     * @param amortizationAmount of loan
     * @param nextPayment of loan
     * @param boundTo of loan
     */

    public Loan(int id, String name, int amount, double interestRate, int amortizationAmount, long nextPayment,
                long boundTo) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.interestRate = interestRate;
        this.amortizationAmount = amortizationAmount;
        this.nextPayment = nextPayment;
        this.boundTo = boundTo;
    }

    /**
     * This is used to update loans as they are created, if next payment dates are set to before "today", then their
     * amortization amount should be deducted. Loans that reach an amount =< 0 are deleted altogether.
     */

    public void performPayments() {
        if (nextPayment > 86400000 && nextPayment < TimeTracking.getCurrentDate()) {
            while (nextPayment < TimeTracking.getCurrentDate() && amount > 0) {
                System.out.println("Deducted amortization amount.");

                // Deduct amortization amount.
                amount -= amortizationAmount;

                // Change the next payment date by first getting the current one:
                LocalDate nextPaymentDate = new Date(nextPayment).toLocalDate();

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
                        break;
                }
                Calendar nextPaymentCal = new GregorianCalendar(year, month - 1, day);
                //nextPaymentCal.set(year, month - 1, day, 0, 0, 0);
                nextPayment = nextPaymentCal.getTimeInMillis();
            }
            if (amount > 0) {
                new SQLiteConnection().updateLoan(this);    // Updates the loan after while loop comes to an end.
            } else {
                System.out.println("Deleted expired loan");
                new SQLiteConnection().deleteLoan(this);
            }
        }
    }

    /**
     * Setter of loan ID.
     *
     * @param newId of loan
     * @return new ID
     */

    @SuppressWarnings("unused")
    public int setId(int newId) {
        id = newId;
        return id;
    }

    /**
     * Setter of loan name.
     *
     * @param newName of loan
     * @return new name
     */

    @SuppressWarnings("unused")
    public String setName(String newName) {
        name = newName;
        return name;
    }

    /**
     * Setter of loan amount.
     *
     * @param newAmount of loan
     * @return new amount
     */

    @SuppressWarnings("unused")
    public int setAmount(int newAmount) {
        amount = newAmount;
        return amount;
    }

    /**
     * Setter of loan interest rate.
     *
     * @param newRate of interest of loan
     * @return new interest rate
     */

    @SuppressWarnings("unused")
    public double setInterestRate(double newRate) {
        interestRate = newRate;
        return interestRate;
    }

    /**
     * Setter of loan amortization
     *
     * @param newAmortizationAmount of amortization of loan
     * @return new amortization rate
     */

    @SuppressWarnings("unused")
    public double setAmortizationAmount(int newAmortizationAmount) {
        amortizationAmount = newAmortizationAmount;
        return amortizationAmount;
    }

    /**
     * Setter of next payment date of loan.
     *
     * @param newNextPayment of loan
     * @return new next payment date
     */

    @SuppressWarnings("unused")
    public long setNextPayment(long newNextPayment) {
        nextPayment = newNextPayment;
        return nextPayment;
    }

    /**
     * Setter of bound to date of loan.
     *
     * @param newBoundTo of loan
     * @return new bound to date
     */

    @SuppressWarnings("unused")
    public long setBoundTo(long newBoundTo) {
        boundTo = newBoundTo;
        return boundTo;
    }

    /**
     * Getter of loan ID.
     *
     * @return loan ID
     */

    public int getId() {
        return id;
    }

    /**
     * Getter of loan name.
     *
     * @return loan name
     */

    public String getName() {
        return name;
    }

    /**
     * Getter of loan amount.
     *
     * @return loan amount
     */

    public int getAmount() {
        return amount;
    }

    /**
     * Getter of loan interest rate.
     *
     * @return loan interest rate
     */

    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Getter of loan amortization rate.
     *
     * @return loan amortization rate
     */

    public int getAmortizationAmount() {
        return amortizationAmount;
    }

    /**
     * Getter of loan's next payment date.
     *
     * @return next payment date of loan
     */

    public long getNextPayment() {
        return nextPayment;
    }

    /**
     * Getter of loan's bound to date.
     *
     * @return bound to date of loan
     */

    public long getBoundTo() {
        return boundTo;
    }

    /**
     * Standard toString.
     *
     * @return string representation of loan
     */

    public String toString() {
        return amount + " at " + interestRate + " % interest. Bound until " + boundTo + "\n" +
                "Amortization is set to " + amortizationAmount + ".\n" +
                "Next payment is due " + nextPayment;
    }
}
