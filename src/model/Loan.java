package model;

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

    public Loan(int id, String name, int amount, double interestRate, int amortizationAmount, long nextPayment, long boundTo) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.interestRate = interestRate;
        this.amortizationAmount = amortizationAmount;
        this.nextPayment = nextPayment;
        this.boundTo = boundTo;
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
    public long setLastPayment(long newNextPayment) {
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
