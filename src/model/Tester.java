package model;

import model.DateTime.CurrentDate;

import java.util.Timer;

/**
 * Created by MTs on 07/08/16.
 */
public class Tester {

    public static void main(String[] args) {
        Timer dateUpdater = new Timer();
        dateUpdater.schedule(new CurrentDate(), 0, 5000);


    }
}
