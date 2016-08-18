package model;

import model.DateTime.CurrentDate;

import java.util.Timer;

public class Tester {

    public static void main(String[] args) {
        Timer dateUpdater = new Timer();
        dateUpdater.schedule(new CurrentDate(), 0, 5000);
    }
}
