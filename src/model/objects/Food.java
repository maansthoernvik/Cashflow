package model.objects;

/**
 * Created by MTs on 03/09/16.
 *
 * Food object to represent cost of ones food.
 */

public class Food {

    private int id;

    private int amount;

    /**
     * Basic constructor to create a food object with no id.
     *
     * @param amount cost of food
     */

    public Food(int amount) {
        this.amount = amount;
    }

    /**
     * Food already present in db, id number available.
     *
     * @param id of food entry
     * @param amount cost of food
     */

    public Food(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    /**
     * Sets the id of the food entry.
     *
     * @param newId number
     * @return new ID number
     */

    public int setId(int newId) {
        id = newId;
        return id;
    }

    /**
     * Sets the amount spent on food.
     *
     * @param newAmount spent on food
     * @return the new amount
     */

    public int setAmount(int newAmount) {
        amount = newAmount;
        return amount;
    }

    /**
     * Get id of food entry.
     *
     * @return id of food entry
     */

    public int getId() {
        return id;
    }

    /**
     * Get amount spent on food.
     *
     * @return amount spent on food
     */

    public int getAmount() {
        return amount;
    }
}
