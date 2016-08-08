package model;

import java.sql.Date;

/**
 * Created by MTs on 03/08/16.
 *
 *
 */

public class Expense {
    private String name;

    private int amount;

    private Date endDate;

    public Expense(String name, int amount, Date endDate) {
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

    public Date setEndDate(Date newEndDate) {
        endDate = newEndDate;
        return endDate;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public Date getEndDate() {
        return endDate;
    }
}
