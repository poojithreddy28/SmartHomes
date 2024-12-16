package com.smarthomes.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.smarthomes.models.MySQLDataStoreUtilities;;

public class AjaxUtility {
    public List<Product> searchProductsByName(String searchQuery) {
    List<Product> products = new ArrayList<>();
    String query = "SELECT * FROM products WHERE name LIKE ? LIMIT 10";  // Limit results for auto-complete

    try (Connection conn = MySQLDataStoreUtilities.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setString(1, "%" + searchQuery + "%");  // Allow partial matching
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Product product = new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("image_path"),
                rs.getDouble("discount"),
                rs.getInt("warranty"),
                rs.getDouble("manufacturer_rebate"),
                rs.getInt("quantity")
            );
            products.add(product);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}
}
