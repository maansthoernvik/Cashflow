package controller;

import model.Expense;
import model.Loan;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by MTs on 07/08/16.
 *
 *
 */

public class SQLiteConnection {

    private final String connectionURL = "jdbc:sqlite:chillbills_master";

    private SQLiteConfig config;

    public SQLiteConnection() {
        config = new SQLiteConfig();
        config.enforceForeignKeys(true);
    }

    public ArrayList<Loan> fetchLoans(String query, String user) {
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createPreparedStatement(conn, query, user);
             ResultSet rs = ps.executeQuery()) {
            ArrayList<Loan> result = new ArrayList<>();

            while(rs.next()) {
                result.add(new Loan(rs.getString("Name"), rs.getInt("Amount"), rs.getDouble("InterestRate"),
                        rs.getDouble("AmortizationRate"), rs.getInt("AmortizationAmount"),
                        new Date((long) rs.getInt("NextPayment")), new Date((long) rs.getInt("BoundTo"))));
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Expense> fetchExpenses(String query, String user) {
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createPreparedStatement(conn, query, user);
             ResultSet rs = ps.executeQuery()) {
            ArrayList<Expense> result = new ArrayList<>();

            while(rs.next()) {
                result.add(new Expense(rs.getString("Name"), rs.getInt("Amount"),
                        new Date((long) rs.getInt("EndDate"))));
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PreparedStatement createPreparedStatement(Connection conn, String query, String user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, user);

        return ps;
    }

}
