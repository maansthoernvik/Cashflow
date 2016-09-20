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
    private int dayOffset;
    private long boundTo;

    /**
     * Constructor used to insert new loans.
     *
     * @param name of loan
     * @param amount of loan
     * @param interestRate of loan
     * @param amortizationAmount of loan
     * @param nextPayment of loan
     * @param dayOffset of loan
     * @param boundTo of loan
     */

    public Loan(String name, int amount, double interestRate, int amortizationAmount, long nextPayment, int dayOffset,
                long boundTo) {
        this.name = name;
        this.amount = amount;
        this.interestRate = interestRate;
        this.amortizationAmount = amortizationAmount;
        this.nextPayment = nextPayment;
        this.dayOffset = dayOffset;
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
     * @param dayOffset of loan
     * @param boundTo of loan
     */

    public Loan(int id, String name, int amount, double interestRate, int amortizationAmount, long nextPayment,
                int dayOffset, long boundTo) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.interestRate = interestRate;
        this.amortizationAmount = amortizationAmount;
        this.nextPayment = nextPayment;
        this.dayOffset = dayOffset;
        this.boundTo = boundTo;
    }

    /**
     * This is used to update loans as they are created, if next payment dates are set to before "today", then their
     * amortization amount should be deducted. Loans that reach an amount =< 0 are deleted altogether.
     */

    public void performPayments() {
        // No use to enter payments if set to epoch or is amortization set to less than 1...
        if (nextPayment > 86400000 && nextPayment < TimeTracking.getCurrentDate() && amortizationAmount > 0) {

            while (nextPayment < TimeTracking.getCurrentDate() && amount > 0) {
                System.out.println("Deducting amortization amount of loan " + name);

                // Deduct amortization amount.
                amount -= amortizationAmount;

                // TODO check if works.
                nextPayment = TimeTracking.addOneMonth(nextPayment, dayOffset);
            }

            /* ************** *
             * Loop broken!!! *
             * ************** */

            if (amount > 0) {   // This means that the loan has been updated but there is still left to pay.

                new SQLiteConnection().updateLoan(this);    // Updates the loan after while loop comes to an end.
                System.out.println("Updated loan payments and amount of " + name);
            } else {            // Loop terminated due to loan amount being <= 0, delete the crap.

                System.out.println("Deleted expired loan " + name);
                new SQLiteConnection().deleteLoan(this);
            }
        } else {
            System.out.println("No payments needed to be performed for " + name);
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
     * Setter of day offset of loans.
     *
     * @param newDayOffset of loan
     * @return new day offset
     */

    @SuppressWarnings("unused")
    public int setDayOffset(int newDayOffset) {
        dayOffset = newDayOffset;
        return dayOffset;
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
     * Getter of loan's day offset.
     *
     * @return day offset of loan
     */

    public int getDayOffset() {
        return dayOffset;
    }

    /**
     * Getter of loan's bound to date.
     *
     * @return bound to date of loan
     */

    public long getBoundTo() {
        return boundTo;
    }
}
