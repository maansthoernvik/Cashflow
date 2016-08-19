package model;

import java.util.ArrayList;

/**
 * Created by MTs on 19/08/16.
 *
 * Saves the current users loans and expenses in lists to prevent the need to query the DB all the time.
 */

public class User {

    private int id;
    private String name;
    private ArrayList<Loan> loans;
    private ArrayList<Expense> expenses;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
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
     */

    public String setName(String newName) {
        name = newName;
        return name;
    }

    /**
     *
     */

    public int getId() {
        return id;
    }

    /**
     *
     */

    public String getName() {
        return name;
    }
}
