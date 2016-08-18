package model.DateTime;

import java.util.Calendar;
import java.util.TimerTask;
import java.sql.Date;

/**
 * Created by MTs on 03/08/16.
 *
 * This class provides the current date at an interval of your own choice.
 */

public class CurrentDate extends TimerTask {

    private static Date currentDate;

    /**
     * Getter of the current date.
     *
     * @return current sql.Date
     */

    @SuppressWarnings("unused")
    public static Date getCurrentDate() {
        return currentDate;
    }

    /**
     * Updates the current date every x seconds, defined by how this class is used.
     */

    @Override
    public void run() {
        Calendar cal = Calendar.getInstance();
        currentDate = new Date(cal.getTimeInMillis());  // sql.Date uses milliseconds since epoch for its constructor.

        System.out.println(currentDate.toString());     // For testing purposes.
    }
}
