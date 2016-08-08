package model.DateTime;

import java.util.Calendar;
import java.util.TimerTask;
import java.sql.Date;

/**
 * Created by MTs on 03/08/16.
 *
 *
 */

public class CurrentDate extends TimerTask{
    private static Date currentDate;

    public static Date getCurrentDate() {
        return currentDate;
    }

    @Override
    public void run() {
        Calendar cal = Calendar.getInstance();

        // Month + 1 due to counting starting at 0 for January and so on.
        currentDate = new Date(cal.getTimeInMillis());

        System.out.println(currentDate.toString());
    }
}
