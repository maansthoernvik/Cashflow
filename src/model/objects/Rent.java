package model.objects;

/**
 * Created by MTs on 02/09/16.
 *
 *
 */

public class Rent {

    private int id;

    private int amount;

    /**
     *
     * @param amount
     */

    public Rent(int amount) {
        this.amount = amount;
    }

    /**
     *
     * @param id
     * @param amount
     */

    public Rent(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    /**
     *
     * @param newId
     * @return
     */

    public int setId(int newId) {
        id = newId;
        return id;
    }

    /**
     *
     * @param newAmount
     * @return
     */

    public int setAmount(int newAmount) {
       amount = newAmount;
        return amount;
    }

    /**
     *
     * @return
     */

    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */

    public int getAmount() {
       return amount;
    }
}

