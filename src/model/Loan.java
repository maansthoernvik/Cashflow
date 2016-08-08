package model;

import java.sql.Date;

/**
 * Created by MTs on 03/08/16.
 *
 *
 */

public class Loan {
    private String name;

    private int amount;             // The loan amount
    private double interestRate;    // The current interest
    private double amortizationRate;
    private int amortizationAmount;

    private Date nextPayment;       // Last time a loan payment was made, in order to keep track of amortization
                                    // payments so that the loan amount gets updated properly.
    private Date boundTo;           // How long the loans interest rate has been bound

    public Loan(String name, int amount, double interestRate) {
        this.name = name;
        this.amount = amount;
        this.interestRate = interestRate;
    }

    public Loan(String name, int amount, double interestRate, double amortizationRate, int amortizationAmount,
                Date nextPayment, Date boundTo) {
        this.name = name;
        this.amount = amount;
        this.interestRate = interestRate;
        this.amortizationRate = amortizationRate;
        this.amortizationAmount = amortizationAmount;
        this.nextPayment = nextPayment;
        this.boundTo = boundTo;
    }

    public String setBankName(String newBankName) {
        name = newBankName;
        return name;
    }

    public int setAmount(int change) {
        amount += change;
        return amount;
    }

    public double setInterestRate(double newRate) {
        interestRate = newRate;
        return interestRate;
    }

    public double setAmortizationRate(double newRate) {
        amortizationRate = newRate;
        return amortizationRate;
    }

    public int setAmortizationAmount(int newAmount) {
        amortizationAmount = newAmount;
        return amortizationAmount;
    }

    public Date setLastPayment(Date newLastPayment) {
        nextPayment = newLastPayment;
        return nextPayment;
    }

    public Date setBoundTo(Date newDate) {
        boundTo = newDate;
        return boundTo;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getAmortizationRate() {
        return amortizationRate;
    }

    public int getAmortizationAmount() {
        return amortizationAmount;
    }

    public Date getNextPayment() {
        return nextPayment;
    }

    public Date getBoundTo() {
        return boundTo;
    }

    public String toString() {
        String amortization = "";
        if (amortizationAmount == 0) {
            amortization = amortizationRate + "%.";
        } else {
            amortization = amortizationAmount + "crowns per month.";
        }

        return amount + "at " + interestRate + "% interest. Bound until " + boundTo + "\n" +
                "Amortization is set to " + amortization +
                "Last payment was made " + nextPayment;
    }
}
