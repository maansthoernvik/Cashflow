package model.time;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimerTask;

/**
 * Created by MTs on 03/08/16.
 *
 * This class provides the current date at an interval of your own choice.
 */

public class TimeTracking extends TimerTask {

    private static long CURRENT_DATE;
    private static long LAST_SESSION = 0;

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
     * @return
     */

    public static long getLastSession() {
        return LAST_SESSION;
    }

    /**
     *
     * @return
     */

    public static long getLastSessionFirstDayOfMonth() {
        LocalDate date = new Date(LAST_SESSION).toLocalDate();
        int day = date.getDayOfMonth();
        date = date.minusDays(day - 1);
        Calendar cal = new GregorianCalendar(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

        System.out.println(cal.getTime());

        return cal.getTimeInMillis();
    }

    /**
     *
     * @param newCurrentDate
     * @return
     */

    public static long setCurrentDate(long newCurrentDate) {
        CURRENT_DATE = newCurrentDate;
        System.out.println("Current date: " + CURRENT_DATE);
        return CURRENT_DATE;
    }

    /**
     *
     * @return
     */

    public static long setLastSession(long newLastSession) {
        LAST_SESSION = newLastSession;
        System.out.println("Last session: " + LAST_SESSION);
        return LAST_SESSION;
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
     * Returns the day offset from the end of the month, as input it takes the days of a month.
     *
     * @param maxDaysOfMonth total days of the current month
     * @param dayOfMonth day of month selected
     * @return integer of day offset between the end of month and the selected day
     */

    public static int getDayOffset(int maxDaysOfMonth, int dayOfMonth) {
        return maxDaysOfMonth - dayOfMonth;
    }

    /**
     * This method determines, depending on two longs, how many month shifts have occured between the two. This number
     * is then used by the top level caller to determine records to create of previous month's transactions.
     *
     * @return positive integer of month shifts between two dates, negative or zero if the start is before the end
     */

    public static int howManyMonthShifts() {
        int shifts = 0;

        LocalDate start = new Date(LAST_SESSION).toLocalDate();
        LocalDate end = new Date(CURRENT_DATE).toLocalDate();

        int yearDiff = end.getYear() - start.getYear() > 0 ? 12 * (end.getYear() - start.getYear()) : 0;
        int monthDiff = end.getMonthValue() - start.getMonthValue();

        shifts = yearDiff + monthDiff;

        System.out.println("Month shifts: " + shifts);

        return shifts;
    }

    /**
     * Updates the current date every x seconds when this class is instantiated, defined by how this class is used.
     */

    @Override
    public void run() {
        Calendar cal = Calendar.getInstance();
        CURRENT_DATE = cal.getTimeInMillis();
    }
}
