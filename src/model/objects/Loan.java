package model.objects;

import controller.SQLiteConnection;
import model.time.TimeTracking;

import java.sql.Date;

/**
 * Created by MTs on 03/08/16.
 *
 * Used to represent Loans.
 */

public class Loan extends PaymentElement {
    private double interestRate;    // The current interest
    private int amortizationAmount;
    private long boundTo;

    public Loan(int amount, String name, long nextPayment, int dayOffset, int paymentFrequency, double interestRate, int amortizationAmount, long boundTo) {
        super(amount, name, nextPayment, dayOffset, paymentFrequency);
        this.interestRate = interestRate;
        this.amortizationAmount = amortizationAmount;
        this.boundTo = boundTo;
    }

    public Loan(int id, int amount, String name, long nextPayment, int dayOffset, int paymentFrequency, double interestRate, int amortizationAmount, long boundTo) {
        super(id, amount, name, nextPayment, dayOffset, paymentFrequency);
        this.interestRate = interestRate;
        this.amortizationAmount = amortizationAmount;
        this.boundTo = boundTo;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getAmortizationAmount() {
        return amortizationAmount;
    }

    public void setAmortizationAmount(int amortizationAmount) {
        this.amortizationAmount = amortizationAmount;
    }

    public long getBoundTo() {
        return boundTo;
    }

    public void setBoundTo(long boundTo) {
        this.boundTo = boundTo;
    }

    /**
     * This is used to update loans as they are created, if next payment dates are set to before "today", then their
     * amortization amount should be deducted. Loans that reach an amount =< 0 are deleted altogether.
     */

    public void performPayments() {
        // No use to enter payments if set to epoch or is amortization set to less than 1...
        if (nextPayment > 86400000 && nextPayment < TimeTracking.getCurrentDate() && amortizationAmount > 0) {
            System.out.println("Payments need to be performed for loan " + name + ". NextPayment was " +
                    new Date(nextPayment).toLocalDate());
            while (nextPayment < TimeTracking.getCurrentDate() && amount > 0) {
                System.out.println("Deducting amortization amount of loan " + name + ".");

                // Deduct amortization amount.
                amount -= amortizationAmount;

                nextPayment = TimeTracking.addOneMonth(nextPayment, dayOffset);
            }

            System.out.println("Payments performed. NextPayment is now " + new Date(nextPayment).toLocalDate());

            /* ************** *
             * Loop broken!!! *
             * ************** */

            if (amount > 0) {   // This means that the loan has been updated but there is still left to pay.

                new SQLiteConnection().updateLoan(this);    // Updates the loan after while loop comes to an end.
                System.out.println("Updated loan " + name);
            } else {            // Loop terminated due to loan amount being <= 0, delete the crap.

                System.out.println("Deleted loan " + name);
                new SQLiteConnection().deleteLoan(this);
            }
        } else {
            System.out.println("No payments needed to be performed for loan " + name);
        }
    }
}
