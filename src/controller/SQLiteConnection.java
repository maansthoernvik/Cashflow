package controller;

import model.objects.*;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


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
     * @param id current user's id
     * @return array list with all loans of the specified user
     */

    public ArrayList<Loan> fetchLoans(String query, int id) {
        ArrayList<Loan> result = new ArrayList<>();
        // Convert config to properties for use with the connection object.
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createSelectPreparedStatement(conn, query, id);   // Separate method for creating
             ResultSet rs = ps.executeQuery()) {                                        // the prepared statement.

            while (rs.next()) {
                result.add(new Loan(rs.getInt("LoanID"), rs.getString("Name"), rs.getInt("Amount"),
                        rs.getDouble("InterestRate"), rs.getInt("AmortizationAmount"), rs.getLong("NextPayment"),
                        rs.getInt("DayOffset"), rs.getLong("BoundTo")));
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
     * @param id current user's id
     * @return array list with all expenses of the specified user
     */

    public ArrayList<Expense> fetchExpenses(String query, int id) {
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createSelectPreparedStatement(conn, query, id);
             ResultSet rs = ps.executeQuery()) {
            ArrayList<Expense> result = new ArrayList<>();

            while (rs.next()) {
                result.add(new Expense(rs.getInt("ExpenseID"), rs.getString("Name"), rs.getInt("Amount"),
                        rs.getLong("EndDate")));
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Queries the DB for a user's rent entry.
     *
     * @param query for rent
     * @param id of current user
     * @return user's rent if one exists
     */

    public Rent fetchRent(String query, int id) {
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createSelectPreparedStatement(conn, query, id);
             ResultSet rs = ps.executeQuery()) {
            Rent result = null;

            while (rs.next()) {
                result = new Rent(rs.getInt("RentID"), rs.getInt("Amount"));
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Queries the DB for a user's food entry.
     *
     * @param query for food
     * @param id of current user
     * @return user's food if one exists
     */

    public Food fetchFood(String query, int id) {
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createSelectPreparedStatement(conn, query, id);
             ResultSet rs = ps.executeQuery()) {
            Food result = null;

            while (rs.next()) {
                result = new Food(rs.getInt("FoodID"), rs.getInt("Amount"));
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
     * @param id current user's id
     * @return prepared query statement
     * @throws SQLException thrown
     */

    private PreparedStatement createSelectPreparedStatement(Connection conn, String query, int id)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);

        return ps;
    }

    /**
     * Creates a hashmap of all historical records of previous months transactions. Uses string identifiers for the
     * different types of records.
     *
     * @param id of the currently signed in user
     * @return a hashmap of all records
     */

    public HashMap<String, ArrayList<Record>> fetchRecords(int id) {
        HashMap<String, ArrayList<Record>> result = new HashMap<>();

        String query = "SELECT * FROM LoanRecords WHERE UserID = ?;";
        result.put("LoanRecords", fetchRecord(query, id));

        query = "SELECT * FROM ExpenseRecords WHERE UserID = ?;";
        result.put("ExpenseRecords", fetchRecord(query, id));

        query = "SELECT * FROM RentRecords WHERE UserID = ?;";
        result.put("RentRecords", fetchRecord(query, id));

        query = "SELECT * FROM FoodRecords WHERE UserID = ?;";
        result.put("FoodRecords", fetchRecord(query, id));

        return result;
    }

    /**
     * Fetches all of the loan historical records.
     *
     * @param query to fetch record
     * @param id of currently logged in user
     * @return array list of records
     */

    public ArrayList<Record> fetchRecord(String query, int id) {
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createFetchRecordsPreparedStatement(conn, query, id);
             ResultSet rs = ps.executeQuery()) {
            ArrayList<Record> result = new ArrayList<>();

            while (rs.next()) {
                result.add(new Record(rs.getInt("Amount"), rs.getLong("Date")));
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Prepares parameters for a fetch records prepared statement.
     *
     * @param conn connection to DB
     * @param query to be prepared with parameters
     * @param id of user logged in
     * @return PreparedStatement object prepared with parameters
     */

    public PreparedStatement createFetchRecordsPreparedStatement(Connection conn, String query, int id) throws
            SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);

        return ps;
    }

    /**
     * Attempts to get a user from the database, if one is found that matches the given user name and password.
     *
     * @param query for user
     * @param user to check for
     * @param password to check for
     * @return user gotten from DB
     */

    public User fetchUser(String query, String user, String password) {
        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createSelectUserPreparedStatement(conn, query, user, password);
             ResultSet rs = ps.executeQuery()) {

            return new User(rs.getInt("UserID"), rs.getString("Username"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Prepares a select user query statement that can be sent to the DB.
     *
     * @param conn connection object
     * @param query for user
     * @param user to check for
     * @param password to check for
     * @return prepared statement to query for user
     * @throws SQLException if an error is raised
     */

    private PreparedStatement createSelectUserPreparedStatement(Connection conn, String query, String user,
                                                                String password) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, user);
        ps.setString(2, password);

        return ps;
    }

    // *                                                        *
    // *                 RENT STATEMENTS!                       *
    // *                                                        *

    /**
     * Inserts user's rent into the DB if the user has not already provided one.
     *
     * @param rent to be inserted
     * @param id of current user
     * @return true if successful
     */

    public boolean insertRent(Rent rent, int id) {
        String insert = "INSERT INTO Rent (Amount, UserID) VALUES (?, ?);";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createInsertRentPreparedStatement(conn, insert, rent, id)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Prepares an SQL statement to be executed.
     *
     * @param conn connection object
     * @param insert statement
     * @param rent object to be inserted
     * @param id of user
     * @return a prepared insert rent statement
     * @throws SQLException if an error is raised
     */

    private PreparedStatement createInsertRentPreparedStatement(Connection conn, String insert, Rent rent, int id)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setInt(1, rent.getAmount());
        ps.setInt(2, id);

        return ps;
    }

    /**
     * Updates the rent of a user.
     *
     * @param rent to be updated
     * @param id of the current user
     * @return true if update DB successful
     */

    public boolean updateRent(Rent rent, int id) {
        String update = "UPDATE Rent SET Amount = ? WHERE UserID = ?;";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createUpdateRentPreparedStatement(conn, update, rent, id)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Prepares a statement to send to the DB to update a user's rent.
     *
     * @param conn connection object
     * @param update statement
     * @param rent to be updated
     * @param id of the current user
     * @return a prepared statement to update rent
     * @throws SQLException if an error is raised
     */

    private PreparedStatement createUpdateRentPreparedStatement(Connection conn, String update, Rent rent, int id)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(update);
        ps.setInt(1, rent.getAmount());
        ps.setInt(2, id);

        return ps;
    }

    // *                                                        *
    // *                 FOOD STATEMENTS!                       *
    // *                                                        *

    /**
     * Inserts a food cost entry into the DB for the current user.
     *
     * @param food to be inserted
     * @param id of the current user
     * @return true if successful insert
     */

    public boolean insertFood(Food food, int id) {
        String insert = "INSERT INTO Food (Amount, UserID) VALUES (?, ?);";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createInsertFoodPreparedStatement(conn, insert, food, id)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Prepares a preparedStatement for inserting food.
     *
     * @param conn connection object
     * @param insert statement for food
     * @param food object to be inserted
     * @param id of the current user
     * @return a prepared insert statement for food
     */

    private PreparedStatement createInsertFoodPreparedStatement(Connection conn, String insert, Food food, int id)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setInt(1, food.getAmount());
        ps.setInt(2, id);

        return ps;
    }

    /**
     * Updates the food specified for the current user.
     *
     * @param food to be updated
     * @param id of the current user
     * @return true if successful update
     */

    public boolean updateFood(Food food, int id) {
        String update = "UPDATE Food SET Amount = ? WHERE UserID = ?;";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createUpdateFoodPreparedStatement(conn, update, food, id)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Prepares an update food preparedStatement.
     *
     * @param conn connection object
     * @param update statement for food
     * @param food to be updated
     * @param id current user
     * @return prepared update food statement
     * @throws SQLException if an error is raised
     */

    private PreparedStatement createUpdateFoodPreparedStatement(Connection conn, String update, Food food, int id)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(update);
        ps.setInt(1, food.getAmount());
        ps.setInt(2, id);

        return ps;
    }

    // *                                                        *
    // *                 LOAN STATEMENTS!                       *
    // *                                                        *

    /**
     * Inserts a new loan into the DB.
     *
     * @param loan to be inserted
     * @param id current user's id
     * @return true if successful
     */

    public boolean insertLoan(Loan loan, int id) {
        String insert = "INSERT INTO Loans (Name, Amount, InterestRate, AmortizationAmount, " +
                "NextPayment, DayOffset, BoundTo, UserID) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createInsertLoanPreparedStatement(conn, insert, loan, id)) {
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
     * @param id current user's id
     * @return prepared insert statement
     * @throws SQLException thrown
     */

    private PreparedStatement createInsertLoanPreparedStatement(Connection conn, String insert, Loan loan, int id)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setString(1, loan.getName());
        ps.setInt(2, loan.getAmount());
        ps.setDouble(3, loan.getInterestRate());
        ps.setInt(4, loan.getAmortizationAmount());
        ps.setLong(5, loan.getNextPayment());
        ps.setInt(6, loan.getDayOffset());
        ps.setLong(7, loan.getBoundTo());
        ps.setInt(8, id);

        return ps;
    }

    /**
     * Updates an already inserted loan in the DB.
     *
     * @param loan to be updated
     * @return true if successful
     */

    public boolean updateLoan(Loan loan) {
        String update = "UPDATE Loans SET Name = ?, Amount = ?, InterestRate = ?, AmortizationAmount = ?, " +
                "NextPayment = ?, DayOffset = ?, BoundTo = ? WHERE LoanID = ?;";

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
        ps.setInt(4, loan.getAmortizationAmount());
        ps.setLong(5, loan.getNextPayment());
        ps.setInt(6, loan.getDayOffset());
        ps.setLong(7, loan.getBoundTo());
        ps.setInt(8, loan.getId());

        return ps;
    }

    /**
     * Deletes an already inserted loan from the DB.
     *
     * @param loan to be deleted
     * @return true if successful
     */

    public boolean deleteLoan(Loan loan) {
        String delete = "DELETE FROM Loans WHERE LoanID = ?;";

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
     * @param id current user's id
     * @return true if successful
     */

    public boolean insertExpense(Expense expense, int id) {
        String insert = "INSERT INTO Expenses (Name, Amount, EndDate, UserID) VALUES (?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createInsertExpensePreparedStatement(conn, insert, expense, id)) {
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
     * @param id current user's id
     * @return prepared insert statement
     * @throws SQLException thrown
     */

    private PreparedStatement createInsertExpensePreparedStatement(Connection conn, String insert, Expense expense,
                                                                   int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setString(1, expense.getName());
        ps.setInt(2, expense.getAmount());
        ps.setLong(3, expense.getEndDate());
        ps.setInt(4, id);

        return ps;
    }

    /**
     * Updates an already inserted expense in the DB.
     *
     * @param expense to be updated
     * @return true if successful
     */

    public boolean updateExpense(Expense expense) {
        String update = "UPDATE Expenses SET Name = ?, Amount = ?, EndDate = ? WHERE ExpenseID = ?";

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

    private PreparedStatement createUpdateExpensePreparedStatement(Connection conn, String update, Expense expense)
            throws SQLException {
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
        String delete = "DELETE FROM Expenses WHERE ExpenseID = ?;";

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

    private PreparedStatement createDeleteExpensePreparedStatement(Connection conn, String delete, Expense expense)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(delete);
        ps.setInt(1, expense.getId());

        return ps;
    }

    // *                                                        *
    // *                 RECORD STATEMENTS!                     *
    // *                                                        *

    /**
     * Inserts a new record into the DB.
     *
     * @param record to be inserted
     * @param identifier of the target table
     * @param id of the current user
     * @return true if successful insert
     */

    public boolean insertRecord(Record record, String identifier, int id) {
        String insert = "INSERT INTO " + identifier + " (Amount, Date, UserID) VALUES (?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(connectionURL, config.toProperties());
             PreparedStatement ps = createInsertRecordPreparedStatement(conn, insert, record, id)) {
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Prepares a statement for inserting a record.
     *
     * @param conn connection object
     * @param insert sql statement
     * @param record to be inserted
     * @param id of the current user
     * @return PreparedStatement object to be executed
     * @throws SQLException in case of error
     */

    private PreparedStatement createInsertRecordPreparedStatement(Connection conn, String insert, Record record, int id)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setInt(1, record.getAmount());
        ps.setLong(2, record.getDate());
        ps.setInt(3, id);

        return ps;
    }
}
