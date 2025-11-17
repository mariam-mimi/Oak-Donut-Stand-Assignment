/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import core.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for OD_Order table (Oak Donuts orders).
 */
public class DonutOrderDAO implements DAO<DonutOrder> {

    @Override
    public Optional<DonutOrder> get(int id) {
        DB db = DB.getInstance();
        try {
            String sql = "SELECT * FROM OD_Order WHERE Transaction_ID = ?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            DonutOrder order = null;
            if (rs.next()) {
                order = mapRow(rs);
            }
            rs.close();
            stmt.close();
            return Optional.ofNullable(order);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<DonutOrder> getAll() {
        DB db = DB.getInstance();
        List<DonutOrder> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM OD_Order ORDER BY Transaction_ID";
            ResultSet rs = db.executeQuery(sql);
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public void insert(DonutOrder order) {
        DB db = DB.getInstance();
        try {
            String sql = "INSERT INTO OD_Order (Order_DateTime, Items_Details, Subtotal, Tax_Amount, Total_Amount) "
                    + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(order.getOrderDateTime()));
            stmt.setString(2, order.getItemsDetails());
            stmt.setDouble(3, order.getSubtotal());
            stmt.setDouble(4, order.getTaxAmount());
            stmt.setDouble(5, order.getTotalAmount());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                order.setTransactionId(id);
            }
            keys.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(DonutOrder order) {
        DB db = DB.getInstance();
        try {
            String sql = "UPDATE OD_Order SET Order_DateTime=?, Items_Details=?, Subtotal=?, "
                    + "Tax_Amount=?, Total_Amount=? WHERE Transaction_ID=?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(order.getOrderDateTime()));
            stmt.setString(2, order.getItemsDetails());
            stmt.setDouble(3, order.getSubtotal());
            stmt.setDouble(4, order.getTaxAmount());
            stmt.setDouble(5, order.getTotalAmount());
            stmt.setInt(6, order.getTransactionId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void delete(DonutOrder order) {
        DB db = DB.getInstance();
        try {
            String sql = "DELETE FROM OD_Order WHERE Transaction_ID=?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, order.getTransactionId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private DonutOrder mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("Transaction_ID");
        Timestamp ts = rs.getTimestamp("Order_DateTime");
        String orderDateTime = ts.toString();
        String itemsDetails = rs.getString("Items_Details");
        double subtotal = rs.getDouble("Subtotal");
        double taxAmount = rs.getDouble("Tax_Amount");
        double totalAmount = rs.getDouble("Total_Amount");
        return new DonutOrder(id, orderDateTime, itemsDetails, subtotal, taxAmount, totalAmount);
    }
}
