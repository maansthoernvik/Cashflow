package testing;

import model.time.TimeTracking;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Tester {

    public static void main(String[] args) {
        Calendar cal = new GregorianCalendar(2015, 0, 1);
        System.out.println(cal.getTime());
        Long shit = cal.getTimeInMillis();

        System.out.println("Starting long: " + shit);
        shit = TimeTracking.addOneMonth(shit, -1);
        System.out.println("Plus 1  month: " + shit);

        LocalDate date = new Date(shit).toLocalDate();
        cal = new GregorianCalendar(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        System.out.println(cal.getTime());

        // 2015-01-01
        // 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9
        // BING! OK. 20 shifts

        // 1970-01-01
        // 12 * 46 + 2, 3, 4, 5, 6, 7, 8, 9
        // BING! OK. 560 shifts from epoch

        // 2014-10-02
        // 12 + 11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9
        // BING! OK. 23 shifts

        //TimeTracking.setLastSession(0);
        //TimeTracking.setLastSession(cal.getTimeInMillis());
        //TimeTracking.setCurrentDate(Calendar.getInstance().getTimeInMillis());
        //TimeTracking.howManyMonthShifts();

        //TimeTracking.getLastSessionFirstDayOfMonth();
    }
}