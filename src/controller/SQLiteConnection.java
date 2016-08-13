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

    /**********************************************************
     *                                                        *
     *                  SELECT STATEMENTS!                    *
     *                                                        *
     *********************************************************/

    public ArrayList<Loan> fetchLoans(String query, String user) {
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createSelectPreparedStatement(conn, query, user);
             ResultSet rs = ps.executeQuery()) {
            ArrayList<Loan> result = new ArrayList<>();

            while(rs.next()) {
                result.add(new Loan(rs.getInt("id"), rs.getString("Name"), rs.getInt("Amount"), rs.getDouble("InterestRate"),
                        rs.getDouble("AmortizationRate"), rs.getLong("NextPayment"), rs.getLong("BoundTo")));
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Expense> fetchExpenses(String query, String user) {
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createSelectPreparedStatement(conn, query, user);
             ResultSet rs = ps.executeQuery()) {
            ArrayList<Expense> result = new ArrayList<>();

            while(rs.next()) {
                result.add(new Expense(rs.getInt("id"), rs.getString("Name"), rs.getInt("Amount"),
                        rs.getLong("EndDate")));
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PreparedStatement createSelectPreparedStatement(Connection conn, String query, String user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, user);

        return ps;
    }

    /**********************************************************
     *                                                        *
     *                 LOAN STATEMENTS!                       *
     *                                                        *
     *********************************************************/

    public boolean insertLoan(Loan loan, String user) {
        String insert = "INSERT INTO Loans (User, Name, Amount, InterestRate, AmortizationRate, AmortizationAmount, " +
                "NextPayment, BoundTo) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createInsertLoanPreparedStatement(conn, insert, loan, user)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PreparedStatement createInsertLoanPreparedStatement(Connection conn, String insert, Loan loan, String user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setString(1, user);
        ps.setString(2, loan.getName());
        ps.setInt(3, loan.getAmount());
        ps.setDouble(4, loan.getInterestRate());
        ps.setDouble(5, loan.getAmortizationRate());
        ps.setLong(6, loan.getNextPayment());
        ps.setLong(7, loan.getBoundTo());

        return ps;
    }

    public boolean updateLoan(Loan loan) {
        String update = "UPDATE Loans SET Name = ?, Amount = ?, InterestRate = ?, AmortizationRate = ?, " +
                "NextPayment = ?, BoundTo = ? WHERE Id = ?;";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createUpdateLoanPreparedStatement(conn, update, loan)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PreparedStatement createUpdateLoanPreparedStatement(Connection conn, String update, Loan loan) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(update);
        ps.setString(1, loan.getName());
        ps.setInt(2, loan.getAmount());
        ps.setDouble(3, loan.getInterestRate());
        ps.setDouble(4, loan.getAmortizationRate());
        ps.setLong(5, loan.getNextPayment());
        ps.setLong(6, loan.getBoundTo());
        ps.setInt(7, loan.getId());

        return ps;
    }

    public boolean deleteLoan(Loan loan) {
        String delete = "DELETE FROM Loans WHERE ID = ?;";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createDeleteLoanPreparedStatement(conn, delete, loan)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PreparedStatement createDeleteLoanPreparedStatement(Connection conn, String delete, Loan loan) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(delete);
        ps.setInt(1, loan.getId());

        return ps;
    }

    /**********************************************************
     *                                                        *
     *                 EXPENSE STATEMENTS!                    *
     *                                                        *
     *********************************************************/

    public boolean insertExpense(Expense expense, String user) {
        String insert = "INSERT INTO Expenses (User, Name, Amount, EndDate) VALUES (?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createInsertExpensePreparedStatement(conn, insert, expense, user)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PreparedStatement createInsertExpensePreparedStatement(Connection conn, String insert, Expense expense, String user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setString(1, user);
        ps.setString(2, expense.getName());
        ps.setInt(3, expense.getAmount());
        ps.setLong(4, expense.getEndDate());

        return ps;
    }

    public boolean updateExpense(Expense expense) {
        String update = "UPDATE Expenses SET Name = ?, Amount = ?, EndDate = ? WHERE ID = ?";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createUpdateExpensePreparedStatement(conn, update, expense)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PreparedStatement createUpdateExpensePreparedStatement(Connection conn, String update, Expense expense) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(update);
        ps.setString(1, expense.getName());
        ps.setInt(2, expense.getAmount());
        ps.setLong(3, expense.getEndDate());
        ps.setInt(4, expense.getId());

        return ps;
    }

    public boolean deleteExpense(Expense expense) {
        String delete = "DELETE FROM Expenses WHERE ID = ?;";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createDeleteExpensePreparedStatement(conn, delete, expense)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PreparedStatement createDeleteExpensePreparedStatement(Connection conn, String delete, Expense expense) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(delete);
        ps.setInt(1, expense.getId());

        return ps;
    }
}
