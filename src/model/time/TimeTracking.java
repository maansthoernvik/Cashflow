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
     * Returns the value of LAST_SESSION, a variable containing information about when the app was last used, prior to
     * the current session CURRENT_DATE.
     *
     * @return LAST_SESSION variable
     */

    public static long getLastSession() {
        return LAST_SESSION;
    }

    /**
     * Returns the LAST_SESSION modified to the first day of the month of the last session.
     *
     * @return LAST_SESSION - current days of month.
     */

    public static long getLastSessionFirstDayOfMonth() {
        LocalDate date = new Date(LAST_SESSION).toLocalDate();
        int day = date.getDayOfMonth();
        date = date.minusDays(day - 1);
        // The hour of day is set to 01:00:00 since this is central european time, epoch is counted from Greenwich (+0)
        // which is one hour behind.
        Calendar cal = new GregorianCalendar(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), 1, 0, 0);

        System.out.println("First day of last session month is: " +  cal.getTime());

        return cal.getTimeInMillis();
    }

    /**
     * Return LAST_SESSION modified so that the last session's day is the last of the month.
     *
     * @return LAST_SESSION + days up until the last day of month.
     */

    public static long getLastSessionLastDayOfMonth() {
        LocalDate date = new Date(LAST_SESSION).toLocalDate();

        Calendar cal = new GregorianCalendar(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), 1, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        System.out.println("Last  day of last session month is: " +  cal.getTime());

        return cal.getTimeInMillis();
    }

    /**
     * Setter for CURRENT_DATE.
     *
     * @param newCurrentDate new long date
     * @return new CURRENT_DATE
     */

    public static long setCurrentDate(long newCurrentDate) {
        CURRENT_DATE = newCurrentDate;
        System.out.println("Current date: " + CURRENT_DATE);
        return CURRENT_DATE;
    }

    /**
     * Setter for LAST_SESSION.
     *
     * @param newLastSession new long date
     * @return new LAST_SESSION
     */

    public static long setLastSession(long newLastSession) {
        LAST_SESSION = newLastSession;
        System.out.println("Last session: " + LAST_SESSION);
        return LAST_SESSION;
    }

    /**
     * Adds one month to a date represented as a long.
     *
     * @param input long to be increased
     * @param dayOffset int representing current dayOffset of object calling function. If < 0, no dayOffset exists and a
     * new one will be created.
     */

    public static long addOneMonth(long input, int dayOffset) {
        LocalDate date = new Date(input).toLocalDate();

        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // Input dayOffset < 0 means, no dayOffset exists so make your own.
        if (dayOffset < 0) {
            Calendar cal = new GregorianCalendar(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

            dayOffset = getDayOffset(cal.getActualMaximum(Calendar.DAY_OF_MONTH), day);
        }

        // Depending on what month it is, there will be different payment days.
        switch (month) {
            case 1:
                month = 2;

                if (isLeapYear(year) && dayOffset < 2) {
                    day = 29;
                } else if (dayOffset < 3) {
                    day = 28;
                } else {
                    day = 28 - (dayOffset - 3);
                }

                break;

            case 2:
                month = 3;

                day = 31 - dayOffset;

                break;

            case 3:
                month = 4;

                if (dayOffset < 1) {
                    day = 30;
                } else {
                    day = 30 - (dayOffset - 1); // How does this work?...
                }

                /* Months that have less than 31 days get 1 deducted from their dayOffsets.
                 * For example:
                 * Loan is saved on the 30th of January, dayOffset in hence 1 (31 - 1).
                 * February, day is set to either 29th or 28th depending on leap years.
                 * March comes, day is set to march total days - dayOffset (31 - 1).
                 * April comes, day is set to april total days - (dayOffset - 1) = 30 - (1 - 1) = 30.
                 *
                 * Another example:
                 * Loan is saved march 25th, dayoffset = 6
                 * april comes, day is set to 30 - (6 - 1) = 25
                 * may comes, day is set to 31 - 6 = 25
                 * june comes, day is set to 30 - (6 - 1) = 25
                 * ... and so on
                 */

                break;

            case 4:
                month = 5;

                day = 31 - dayOffset;

                break;

            case 5:
                month = 6;

                if (dayOffset < 1) {
                    day = 30;
                } else {
                    day = 30 - (dayOffset - 1);
                }

                break;

            case 6:
                month = 7;

                day = 31 - dayOffset;

                break;

            case 7:
                month = 8;

                day = 31 - dayOffset;

                break;

            case 8:
                month = 9;

                if (dayOffset < 1) {
                    day = 30;
                } else {
                    day = 30 - (dayOffset - 1);
                }

                break;

            case 9:
                month = 10;

                day = 31 - dayOffset;

                break;

            case 10:
                month = 11;

                if (dayOffset < 1) {
                    day = 30;
                } else {
                    day = 30 - (dayOffset - 1);
                }

                break;

            case 11:
                month = 12;

                day = 31 - dayOffset;

                break;

            case 12:
                year += 1;
                month = 1;

                day = 31 - dayOffset;

                break;
        }
        Calendar cal = new GregorianCalendar(year, month - 1, day);
        return cal.getTimeInMillis();
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
        if (maxDaysOfMonth < 31) {
            if (maxDaysOfMonth == 30) {
                return maxDaysOfMonth - (dayOfMonth - 1);
            } else if (maxDaysOfMonth == 29) {
                return maxDaysOfMonth - (dayOfMonth - 2);
            } else if (maxDaysOfMonth == 28) {
                return maxDaysOfMonth - (dayOfMonth - 3);
            }
        }
        return maxDaysOfMonth - dayOfMonth;
    }

    /**
     * This method determines, depending on two longs, how many month shifts have occured between the two. This number
     * is then used by the top level caller to determine records to create of previous month's transactions.
     *
     * @return positive integer of month shifts between two dates, negative or zero if the start is before the end
     */

    public static int howManyMonthShifts() {
        int shifts;

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
