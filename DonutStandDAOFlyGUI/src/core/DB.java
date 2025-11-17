
package core;

import java.sql.*;

/**
 * Simple singleton wrapper around an embedded Derby database.
 * The database is created (if needed) on first use and the schema
 * for Oak Donuts OD is initialized here.
 */
public class DB {

    private static DB instance;
    private Connection connection;

    private DB() {
        try {
            // Load Derby embedded driver (requires derby jar on classpath at runtime)
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            // Creates or connects to local database "oakdonutsdb"
            connection = DriverManager.getConnection("jdbc:derby:oakdonutsdb;create=true");
            initSchema();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }
        return instance;
    }

    public PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    public int executeUpdate(String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeUpdate(sql);
    }

    /**
     * Create tables if they do not exist and seed the menu table.
     */
    private void initSchema() throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            stmt.executeUpdate(
                    "CREATE TABLE OD_MenuItem ("
                            + "MenuItem_ID INT NOT NULL PRIMARY KEY, "
                            + "Item_Name VARCHAR(50) NOT NULL, "
                            + "Category VARCHAR(20) NOT NULL, "
                            + "Unit_Price DECIMAL(6,2) NOT NULL)"
            );
        } catch (SQLException ex) {
            // X0Y32: Table/View already exists in Derby, safe to ignore
            if (!"X0Y32".equals(ex.getSQLState())) {
                throw ex;
            }
        }

        try {
            stmt.executeUpdate(
                    "CREATE TABLE OD_Order ("
                            + "Transaction_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY "
                            + "    (START WITH 1, INCREMENT BY 1), "
                            + "Order_DateTime TIMESTAMP NOT NULL, "
                            + "Items_Details VARCHAR(500) NOT NULL, "
                            + "Subtotal DECIMAL(7,2) NOT NULL, "
                            + "Tax_Amount DECIMAL(7,2) NOT NULL, "
                            + "Total_Amount DECIMAL(7,2) NOT NULL, "
                            + "PRIMARY KEY (Transaction_ID))"
            );
        } catch (SQLException ex) {
            if (!"X0Y32".equals(ex.getSQLState())) {
                throw ex;
            }
        }

        // Seed menu items if table is empty
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM OD_MenuItem");
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();
        if (count == 0) {
            stmt.executeUpdate(
                    "INSERT INTO OD_MenuItem (MenuItem_ID, Item_Name, Category, Unit_Price) VALUES " +
                            "(1, 'Glazed Donut',        'Donut', 1.49)," +
                            "(2, 'Chocolate Sprinkles', 'Donut', 1.79)," +
                            "(3, 'Boston Creme',        'Donut', 1.99)," +
                            "(4, 'House Coffee',        'Drink', 2.00)," +
                            "(5, 'Latte',               'Drink', 3.00)," +
                            "(6, 'Breakfast Sandwich',  'Food',  4.50)"
            );
        }
        stmt.close();
    }
}
