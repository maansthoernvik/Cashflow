package testing;

import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

/**
 * Created by MTs on 06/09/16.
 *
 *
 */

public class DateTest {
    private static final String connectionURL = "jdbc:sqlite:chillbills_master";   // Perma-linked DB URL.
    private static String insertDate = "INSERT INTO Date (Date) VALUES (date('now'));";
    private static String selectDate = "SELECT Date from Date WHERE ID = 2;";
    private static SQLiteConfig config;

    public static void main(String[] args) {
        config = new SQLiteConfig();
        config.enforceForeignKeys(true);

        Calendar cal = new GregorianCalendar(1999, 11, 20);
        cal.setTimeInMillis(0);

        System.out.println(cal.getTime());

        Scanner input = new Scanner(System.in);
        System.out.println("Please enter: ");
        String inp = input.next();
        System.out.println(inp);

        System.out.println("Type 10.");
        if (input.nextInt() == 10) {
            System.out.println("Match!");
        }

        System.out.println("Type cock");
        if (input.next().equals("cock")) {
            System.out.println("Match!");
        }
    }

//    public static void insertDate() {
//        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
//             PreparedStatement ps = conn.prepareStatement(insertDate)) {
//
//            ps.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static Date selectDate() {
//        Date result = null;
//
//        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
//             PreparedStatement ps = conn.prepareStatement(selectDate);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                result = stringToDate(rs.getString("Date"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}
