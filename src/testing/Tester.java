package testing;

import controller.SQLiteConnection;
import model.objects.Record;
import model.time.TimeTracking;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Tester {

    public static void main(String[] args) {

        SQLiteConnection sql = new SQLiteConnection();

        HashMap<String, ArrayList<Record>> res = sql.fetchRecords(1);

        System.out.println("Do loan records exist?    : " + res.containsKey("LoanRecords"));
        System.out.println("Do expense records exist? : " + res.containsKey("ExpenseRecords"));
        System.out.println("Do rent records exist?    : " + res.containsKey("RentRecords"));
        System.out.println("Do food records exist?    : " + res.containsKey("FoodRecords"));

        System.out.println("");

        Set<String> set = res.keySet();

        for (String s : set) {
            ArrayList<Record> arr = res.get(s);
            for (Record r : arr) {
                System.out.println(s + " : " + r.getAmount() + " / " + r.getDate());
            }
        }


//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:chillbills_master");
//             PreparedStatement ps = conn.prepareStatement("INSERT INTO RentRecords (Amount, Date, UserID) VALUES " +
//                     "(1000, 5094000000, 1);")) {
//            ps.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }


        //TimeTracking.setLastSession(Calendar.getInstance().getTimeInMillis());
        //LocalDate date = new Date(TimeTracking.getLastSessionFirstDayOfMonth()).toLocalDate();
        //GregorianCalendar cal = new GregorianCalendar(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        //System.out.println(cal.getTime());



//        Calendar cal = new GregorianCalendar(1970, 0, 1);
//        System.out.println(cal.getTimeInMillis());
//        System.out.println(cal.getTime());
//        cal = new GregorianCalendar(1970, 1, 1);
//        System.out.println(cal.getTimeInMillis());
//        System.out.println(cal.getTime());
//        cal = new GregorianCalendar(1970, 2, 1);
//        System.out.println(cal.getTimeInMillis());
//        System.out.println(cal.getTime());


        //System.out.println("Starting long: " + shit);
        //shit = TimeTracking.addOneMonth(shit, 30);
        //System.out.println("Plus 1  month: " + shit);

        //LocalDate date = new Date(shit).toLocalDate();
        //cal = new GregorianCalendar(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        //System.out.println(cal.getTime());

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