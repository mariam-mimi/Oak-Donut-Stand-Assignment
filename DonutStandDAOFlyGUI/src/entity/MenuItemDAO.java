
package entity;

import core.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for the OD_MenuItem table.
 */
public class MenuItemDAO implements DAO<MenuItem> {

    @Override
    public Optional<MenuItem> get(int id) {
        DB db = DB.getInstance();
        try {
            String sql = "SELECT * FROM OD_MenuItem WHERE MenuItem_ID = ?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            MenuItem item = null;
            if (rs.next()) {
                item = new MenuItem(
                        rs.getInt("MenuItem_ID"),
                        rs.getString("Item_Name"),
                        rs.getString("Category"),
                        rs.getDouble("Unit_Price")
                );
            }
            rs.close();
            stmt.close();
            return Optional.ofNullable(item);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<MenuItem> getAll() {
        DB db = DB.getInstance();
        List<MenuItem> items = new ArrayList<>();
        try {
            String sql = "SELECT * FROM OD_MenuItem ORDER BY MenuItem_ID";
            ResultSet rs = db.executeQuery(sql);
            while (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getInt("MenuItem_ID"),
                        rs.getString("Item_Name"),
                        rs.getString("Category"),
                        rs.getDouble("Unit_Price")
                );
                items.add(item);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return items;
    }

    @Override
    public void insert(MenuItem item) {
        DB db = DB.getInstance();
        try {
            String sql = "INSERT INTO OD_MenuItem (MenuItem_ID, Item_Name, Category, Unit_Price) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getCategory());
            stmt.setDouble(4, item.getUnitPrice());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(MenuItem item) {
        DB db = DB.getInstance();
        try {
            String sql = "UPDATE OD_MenuItem SET Item_Name=?, Category=?, Unit_Price=? WHERE MenuItem_ID=?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getCategory());
            stmt.setDouble(3, item.getUnitPrice());
            stmt.setInt(4, item.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void delete(MenuItem item) {
        DB db = DB.getInstance();
        try {
            String sql = "DELETE FROM OD_MenuItem WHERE MenuItem_ID=?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, item.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
