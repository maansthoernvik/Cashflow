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

                // Change the next payment date by first getting the current one:
                LocalDate nextPaymentDate = new Date(nextPayment).toLocalDate();

                int year = nextPaymentDate.getYear();           // Old next payment year.
                int month = nextPaymentDate.getMonthValue();    // Old next payment month.
                int day = nextPaymentDate.getDayOfMonth();      // Old next payment day.

                // Is it a leap year?
                boolean isLeapYear = TimeTracking.isLeapYear(year);

                // Depending on what month it is, there will be different payment days.
                switch (month) {
                    case 1:
                        month = 2;

                        if (isLeapYear && dayOffset < 2) {
                            day = 29;
                        } else if (dayOffset < 3) {
                            day = 28;
                        }

                        break;

                    case 2:
                        month = 3;

                        day = 31 - dayOffset;

                        break;

                    case 3:
                        month = 4;

                        if (dayOffset < 1) {
                            day = 30;
                        } else {
                            day = 30 - (dayOffset - 1); // How does this work?...
                        }

                        /* Months that have less than 31 days get 1 deducted from their dayOffsets.
                         * For example:
                         * Loan is saved on the 30th of January, dayOffset in hence 1 (31 - 1).
                         * February, day is set to either 29th or 28th depending on leap years.
                         * March comes, day is set to march total days - dayOffset (31 - 1).
                         * April comes, day is set to april total days - (dayOffset - 1) = 30 - (1 - 1) = 30.
                         *
                         * Another example:
                         * Loan is saved march 25th, dayoffset = 6
                         * april comes, day is set to 30 - (6 - 1) = 25
                         * may comes, day is set to 31 - 6 = 25
                         * june comes, day is set to 30 - (6 - 1) = 25
                         * ... and so on
                         */

                        break;

                    case 4:
                        month = 5;

                        day = 31 - dayOffset;

                        break;

                    case 5:
                        month = 6;

                        if (dayOffset < 1) {
                            day = 30;
                        } else {
                            day = 30 - (dayOffset - 1);
                        }

                        break;

                    case 6:
                        month = 7;

                        day = 31 - dayOffset;

                        break;

                    case 7:
                        month = 8;

                        day = 31 - dayOffset;

                        break;

                    case 8:
                        month = 9;

                        if (dayOffset < 1) {
                            day = 30;
                        } else {
                            day = 30 - (dayOffset - 1);
                        }

                        break;

                    case 9:
                        month = 10;

                        day = 31 - dayOffset;

                        break;

                    case 10:
                        month = 11;

                        if (dayOffset < 1) {
                            day = 30;
                        } else {
                            day = 30 - (dayOffset - 1);
                        }

                        break;

                    case 11:
                        month = 12;

                        day = 31 - dayOffset;

                        break;

                    case 12:
                        year += 1;
                        month = 1;

                        day = 31 - dayOffset;

                        break;
                }
                Calendar nextPaymentCal = new GregorianCalendar(year, month - 1, day);
                nextPayment = nextPaymentCal.getTimeInMillis();
            }

            if (amount > 0) {   // This means that the loan has been updated but there is still left to pay.

                new SQLiteConnection().updateLoan(this);    // Updates the loan after while loop comes to an end.
                System.out.println("Updated loan payments and amount of " + name);
            } else {            // Loop terminated due to loan amount being <= 0, delete the crap.

                System.out.println("Deleted expired loan " + name);
                new SQLiteConnection().deleteLoan(this);
            }
        }
        System.out.println("No payments needed to be performed for " + name);
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
