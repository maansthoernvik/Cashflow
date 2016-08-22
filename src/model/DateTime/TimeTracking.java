package model.DateTime;

import java.util.Calendar;
import java.util.TimerTask;
import java.sql.Date;

/**
 * Created by MTs on 03/08/16.
 *
 * This class provides the current date at an interval of your own choice.
 */

public class TimeTracking extends TimerTask {

    private static long CURRENT_DATE;

    /**
     * Getter of the current date.
     *
     * @return current date represented as a long
     */

    @SuppressWarnings("unused")
    public static long getCurrentDate() {
        return CURRENT_DATE;
    }

    /**
     *
     * @param year
     * @return
     */

    public static boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }

    /**
     *
     */

    public static int daysOfMonth() {
        return 1;
    }

    /**
     * Updates the current date every x seconds when this class is instantiated, defined by how this class is used.
     */


    @Override
    public void run() {
        Calendar cal = Calendar.getInstance();
        CURRENT_DATE = cal.getTimeInMillis();

        System.out.println(new Date(cal.getTimeInMillis()).toString());     // For testing purposes.
    }
}
