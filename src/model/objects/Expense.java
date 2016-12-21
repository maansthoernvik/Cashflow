package model.objects;

/**
 * Created by MTs on 03/08/16.
 *
 * Used to represent expenses in the application.
 */

public class Expense extends PaymentElement {
    private long endDate;

    public Expense(int amount, String name, long nextPayment, int dayOffset, int paymentFrequency, long endDate) {
        super(amount, name, nextPayment, dayOffset, paymentFrequency);
        this.endDate = endDate;
    }

    public Expense(int id, int amount, String name, long nextPayment, int dayOffset, int paymentFrequency, long endDate) {
        super(id, amount, name, nextPayment, dayOffset, paymentFrequency);
        this.endDate = endDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
