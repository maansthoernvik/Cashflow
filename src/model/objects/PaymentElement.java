package model.objects;

/**
 * Created by MTs on 30/10/16.
 */
public class PaymentElement extends Element {
    private int amount;

    private String name;

    private long nextPayment;
    private int dayOffset;
    private int paymentFrequency;

    /* Without id */

    public PaymentElement(int amount, String name, long nextPayment, int dayOffset, int paymentFrequency) {
        this.amount = amount;
        this.name = name;
        this.nextPayment = nextPayment;
        this.dayOffset = dayOffset;
        this.paymentFrequency = paymentFrequency;
    }

    /* With id */

    public PaymentElement(int id, int amount, String name, long nextPayment, int dayOffset, int paymentFrequency) {
        super(id);
        this.amount = amount;
        this.name = name;
        this.nextPayment = nextPayment;
        this.dayOffset = dayOffset;
        this.paymentFrequency = paymentFrequency;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public long getNextPayment() {
        return nextPayment;
    }

    public int getDayOffset() {
        return dayOffset;
    }

    public int getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNextPayment(long nextPayment) {
        this.nextPayment = nextPayment;
    }

    public void setDayOffset(int dayOffset) {
        this.dayOffset = dayOffset;
    }

    public void setPaymentFrequency(int paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    @Override
    public String toString() {
        return "PaymentElement{" +
                "amount=" + amount +
                ", name='" + name + '\'' +
                ", nextPayment=" + nextPayment +
                ", dayOffset=" + dayOffset +
                ", paymentFrequency=" + paymentFrequency +
                '}';
    }
}
