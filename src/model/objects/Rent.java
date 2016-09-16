package model.objects;

/**
 * Created by MTs on 02/09/16.
 *
 * Object to represent a users monthly rent cost.
 */

public class Rent {

    private int id;

    private int amount;

    public Rent() {
        this.amount = 0;
    }

    /**
     * Initializes without an id, in case user has not specified a rent cost before - no entry available in DB.
     *
     * @param amount rent cost
     */

    public Rent(int amount) {
        this.amount = amount;
    }

    /**
     * When user already saved rent cost, and entry exists with an id.
     *
     * @param id of rent entry
     * @param amount rent cost
     */

    public Rent(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    /**
     * Changes rent entry id number.
     *
     * @param newId of rent entry
     * @return new id number
     */

    public int setId(int newId) {
        id = newId;
        return id;
    }

    /**
     * Sets the amount paid in rent.
     *
     * @param newAmount of rent
     * @return new rent cost
     */

    public int setAmount(int newAmount) {
       amount = newAmount;
        return amount;
    }

    /**
     * Returns the id of the rent object.
     *
     * @return id of rent entry
     */

    public int getId() {
        return id;
    }

    /**
     * Returns the rent cost.
     *
     * @return rent amount
     */

    public int getAmount() {
       return amount;
    }
}

