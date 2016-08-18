package controller;

import model.Expense;
import model.Loan;

import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by MTs on 07/08/16.
 *
 * Handles selects, inserts, updated, deletions and more to the Chill bills database. All queries and shit are done with
 * try with resource statements, release of resources is automatic.
 */

public class SQLiteConnection {

    private final String connectionURL = "jdbc:sqlite:chillbills_master";   // Perma-linked DB URL.

    private SQLiteConfig config;

    /**
     * Default constructor for preparing the SQLiteConnection.
     */

    public SQLiteConnection() {
        config = new SQLiteConfig();
        config.enforceForeignKeys(true);    // Important for using foreign keys, otherwise they will not work.
    }

    // *                                                        *
    // *                  SELECT STATEMENTS!                    *
    // *                                                        *

    /**
     * Queries the DB for all loans of the user sent as a parameter.
     *
     * @param query for loans
     * @param user current user
     * @return array list with all loans of the specified user
     */

    public ArrayList<Loan> fetchLoans(String query, String user) {
        // Convert config to properties for use with the connection object.
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createSelectPreparedStatement(conn, query, user);   // Separate method for creating
             ResultSet rs = ps.executeQuery()) {                                        // the prepared statement.
            ArrayList<Loan> result = new ArrayList<>();

            while(rs.next()) {
                result.add(new Loan(rs.getInt("id"), rs.getString("Name"), rs.getInt("Amount"),
                        rs.getDouble("InterestRate"), rs.getDouble("AmortizationRate"), rs.getLong("NextPayment"),
                        rs.getLong("BoundTo")));
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Queries the DB for all expenses of the user sent as a parameter.
     *
     * @param query for expenses
     * @param user current user
     * @return array list with all expenses of the specified user
     */

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

    /**
     * Prepares a statement by assigning the parameter user to the PreparedStatement.
     *
     * @param conn connection object
     * @param query for either loans or other items
     * @param user current user
     * @return prepared query statement
     * @throws SQLException thrown
     */

    private PreparedStatement createSelectPreparedStatement(Connection conn, String query, String user)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, user);

        return ps;
    }

    // *                                                        *
    // *                 LOAN STATEMENTS!                       *
    // *                                                        *

    /**
     * Inserts a new loan into the DB.
     *
     * @param loan to be inserted
     * @param user current user
     * @return true if successful
     */

    public boolean insertLoan(Loan loan, String user) {
        String insert = "INSERT INTO Loans (User, Name, Amount, InterestRate, AmortizationRate, " +
                "NextPayment, BoundTo) VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createInsertLoanPreparedStatement(conn, insert, loan, user)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Prepares an insert loan PreparedStatement.
     *
     * @param conn connection
     * @param insert statement
     * @param loan to be inserted
     * @param user current user
     * @return prepared insert statement
     * @throws SQLException thrown
     */

    private PreparedStatement createInsertLoanPreparedStatement(Connection conn, String insert, Loan loan, String user)
            throws SQLException {
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

    /**
     * Updates an already inserted loan in the DB.
     *
     * @param loan to be updated
     * @return true if successful
     */

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

    /**
     * Prepares an update loan PreparedStatement.
     *
     * @param conn connection
     * @param update statement
     * @param loan to be updated
     * @return prepared update statement
     * @throws SQLException thrown
     */

    private PreparedStatement createUpdateLoanPreparedStatement(Connection conn, String update, Loan loan)
            throws SQLException {
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

    /**
     * Deletes an already inserted loan from the DB.
     *
     * @param loan to be deleted
     * @return true if successful
     */

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

    /**
     * Prepares a delete loan PreparedStatement.
     *
     * @param conn connection
     * @param delete statement
     * @param loan to be deleted
     * @return prepared delete statement
     * @throws SQLException thrown
     */

    private PreparedStatement createDeleteLoanPreparedStatement(Connection conn, String delete, Loan loan)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(delete);
        ps.setInt(1, loan.getId());

        return ps;
    }

    // *                                                        *
    // *                 EXPENSE STATEMENTS!                    *
    // *                                                        *

    /**
     * Inserts a new expense into the DB.
     *
     * @param expense to insert
     * @param user current user
     * @return true if successful
     */

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

    /**
     * Prepares an insert expense statement.
     *
     * @param conn connection
     * @param insert statement
     * @param expense to be inserted
     * @param user current user
     * @return prepared insert statement
     * @throws SQLException thrown
     */

    private PreparedStatement createInsertExpensePreparedStatement(Connection conn, String insert, Expense expense,
                                                                   String user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setString(1, user);
        ps.setString(2, expense.getName());
        ps.setInt(3, expense.getAmount());
        ps.setLong(4, expense.getEndDate());

        return ps;
    }

    /**
     * Updates an already inserted expense in the DB.
     *
     * @param expense to be updated
     * @return true if successful
     */

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

    /**
     * Prepares an update expense statement.
     *
     * @param conn connection
     * @param update statement
     * @param expense to be updated
     * @return prepared update statement
     * @throws SQLException thrown
     */

    private PreparedStatement createUpdateExpensePreparedStatement(Connection conn, String update, Expense expense) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(update);
        ps.setString(1, expense.getName());
        ps.setInt(2, expense.getAmount());
        ps.setLong(3, expense.getEndDate());
        ps.setInt(4, expense.getId());

        return ps;
    }

    /**
     * Deletes an existing expense from the DB.
     *
     * @param expense to be deleted
     * @return true if successful
     */

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

    /**
     * Prepares a delete expense statement.
     *
     * @param conn connection
     * @param delete statement
     * @param expense to be deleted
     * @return prepared delete statement
     * @throws SQLException thrown
     */

    private PreparedStatement createDeleteExpensePreparedStatement(Connection conn, String delete, Expense expense) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(delete);
        ps.setInt(1, expense.getId());

        return ps;
    }
}
