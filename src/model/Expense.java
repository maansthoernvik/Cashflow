package model;

/**
 * Created by MTs on 03/08/16.
 *
 *
 */

public class Expense {
    private int id;

    private String name;
    private int amount;
    private long endDate;

    public Expense(String name, int amount, long endDate) {
        this.name = name;
        this.amount = amount;
        this.endDate = endDate;
    }

    public Expense(int id, String name, int amount, long endDate) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.endDate = endDate;
    }

    public String setName(String newName) {
        name = newName;
        return name;
    }

    public int setAmount(int newAmount) {
        amount = newAmount;
        return amount;
    }

    public long setEndDate(long newEndDate) {
        endDate = newEndDate;
        return endDate;
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

    public long getEndDate() {
        return endDate;
    }
}
