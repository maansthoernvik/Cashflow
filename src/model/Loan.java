package model;

import java.sql.Date;

/**
 * Created by MTs on 03/08/16.
 *
 *
 */

public class Loan {
    private int id;

    private String name;
    private int amount;             // The loan amount
    private double interestRate;    // The current interest
    private double amortizationRate;
    private int amortizationAmount;
    private long nextPayment;
    private long boundTo;

    public Loan(String name, int amount, double interestRate, double amortizationRate, int amortizationAmount,
                long nextPayment, long boundTo) {
        this.name = name;
        this.amount = amount;
        this.interestRate = interestRate;
        this.amortizationRate = amortizationRate;
        this.amortizationAmount = amortizationAmount;
        this.nextPayment = nextPayment;
        this.boundTo = boundTo;
    }

    public Loan(int id, String name, int amount, double interestRate, double amortizationRate, int amortizationAmount,
                long nextPayment, long boundTo) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.interestRate = interestRate;
        this.amortizationRate = amortizationRate;
        this.amortizationAmount = amortizationAmount;
        this.nextPayment = nextPayment;
        this.boundTo = boundTo;
    }

    public int setId(int newId) {
        id = newId;
        return id;
    }

    public String setName(String newName) {
        name = newName;
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

    public long setLastPayment(long newLastPayment) {
        nextPayment = newLastPayment;
        return nextPayment;
    }

    public long setBoundTo(long newDate) {
        boundTo = newDate;
        return boundTo;
    }

    public int getId() {
        return id;
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

    public long getNextPayment() {
        return nextPayment;
    }

    public long getBoundTo() {
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
