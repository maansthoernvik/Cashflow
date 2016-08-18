package model;

/**
 * Created by MTs on 03/08/16.
 *
 * Used to represent expenses in the application.
 */

public class Expense {

    private int id;

    private String name;
    private int amount;
    private long endDate;

    /**
     * Constructor used for new inserts of expenses.
     *
     * @param name of expense
     * @param amount of expense
     * @param endDate of expense
     */

    public Expense(String name, int amount, long endDate) {
        this.name = name;
        this.amount = amount;
        this.endDate = endDate;
    }

    /**
     * Constructor used to alter old expenses.
     *
     * @param id of expense
     * @param name of expense
     * @param amount of expense
     * @param endDate of expense
     */

    public Expense(int id, String name, int amount, long endDate) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.endDate = endDate;
    }

    /**
     * Setter of expense id.
     *
     * @param newId of expense
     * @return new id
     */

    @SuppressWarnings("unused")
    public int setId(int newId) {
        id = newId;
        return id;
    }

    /**
     * Setter of expense name.
     *
     * @param newName of expense
     * @return new name
     */

    @SuppressWarnings("unused")
    public String setName(String newName) {
        name = newName;
        return name;
    }

    /**
     * Setter of expense amount.
     *
     * @param newAmount of expense
     * @return new amount
     */

    @SuppressWarnings("unused")
    public int setAmount(int newAmount) {
        amount = newAmount;
        return amount;
    }

    /**
     * Setter of expense end date.
     *
     * @param newEndDate of expense
     * @return new end date
     */

    @SuppressWarnings("unused")
    public long setEndDate(long newEndDate) {
        endDate = newEndDate;
        return endDate;
    }

    /**
     * Getter of expense id.
     *
     * @return expense id
     */

    public int getId() {
        return id;
    }

    /**
     * Getter of expense name.
     *
     * @return expense name
     */

    public String getName() {
        return name;
    }

    /**
     * Getter of expense amount.
     *
     * @return expense amount
     */

    public int getAmount() {
        return amount;
    }

    /**
     * Getter of expense end date.
     *
     * @return expense end date
     */

    public long getEndDate() {
        return endDate;
    }
}
