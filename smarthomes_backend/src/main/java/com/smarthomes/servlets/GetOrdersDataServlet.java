package com.smarthomes.servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.smarthomes.models.MySQLDataStoreUtilities;
import com.smarthomes.models.Order;

@WebServlet("/orders")
public class GetOrdersDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ORDER_FILE_PATH = "/Users/sruthisanjana/Desktop/EWA/smarthomes_backend/orders.ser";  // Path to order data

   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCorsHeaders(response);

        String username = request.getParameter("username");
        if (username == null || username.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("error", "Username is missing.");
            response.setContentType("application/json");
            response.getWriter().print(jsonResponse.toString());
            return;
        }

        try {
            // Fetch user_id by username
            int userId = new MySQLDataStoreUtilities().getUserIdByName(username);
            if (userId == -1) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("error", "User not found.");
                response.setContentType("application/json");
                response.getWriter().print(jsonResponse.toString());
                return;
            }

            // Fetch orders from database
            JSONArray ordersArray = fetchOrdersByUserId(userId);

            // Send the JSON response
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().print(ordersArray.toString());
           
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("error", "Failed to load orders.");
            response.setContentType("application/json");
            response.getWriter().print(jsonResponse.toString());
        }
    }

    // Fetch orders from the database
    private JSONArray fetchOrdersByUserId(int userId) throws SQLException {
        JSONArray ordersArray = new JSONArray();
        String orderSQL = "SELECT * FROM Orders WHERE user_id = ?";
        String orderItemsSQL = "SELECT * FROM Order_Items WHERE order_id = ?";

        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement psOrder = conn.prepareStatement(orderSQL)) {
            psOrder.setInt(1, userId);
            ResultSet rsOrder = psOrder.executeQuery();

            while (rsOrder.next()) {
                int orderId = rsOrder.getInt("order_id");
                String customerName = rsOrder.getString("customer_name");
                String customerAddress = rsOrder.getString("customer_address");
                String shippingMethod = rsOrder.getString("delivery_type");
                String storeAddress = rsOrder.getString("store_address");
                String deliveryDate = rsOrder.getString("delivery_date");
                double totalSales = rsOrder.getDouble("total_sales");

                // Create a JSON object for each order
                JSONObject orderJson = new JSONObject();
                orderJson.put("orderId", orderId);
                orderJson.put("customerName", customerName);
                orderJson.put("customerAddress", customerAddress);
                orderJson.put("shippingMethod", shippingMethod);
                orderJson.put("storeAddress", storeAddress);
                orderJson.put("deliveryDate", deliveryDate);
                orderJson.put("totalSales", totalSales);

                // Fetch the products for this order
                PreparedStatement psItems = conn.prepareStatement(orderItemsSQL);
                psItems.setInt(1, orderId);
                ResultSet rsItems = psItems.executeQuery();

                Map<String, JSONObject> productsMap = new HashMap<>();
                while (rsItems.next()) {
                    String productName = rsItems.getString("product_name");
                    String category = rsItems.getString("category");
                    int quantity = rsItems.getInt("quantity");
                    double price = rsItems.getDouble("price");
                    double discount = rsItems.getDouble("discount");

                    JSONObject productJson = new JSONObject();
                    productJson.put("category", category);
                    productJson.put("quantity", quantity);
                    productJson.put("productPrice", price);
                    productJson.put("discount", discount);

                    productsMap.put(productName, productJson);
                }

                // Add product details to the order
                JSONObject productsJson = new JSONObject(productsMap);
                orderJson.put("products", productsJson);

                // Add the order JSON to the orders array
                ordersArray.put(orderJson);

                // Close the result set and prepared statement for order items
                rsItems.close();
                psItems.close();
            }

            // Close the result set and prepared statement for orders
            rsOrder.close();
        }

        return ordersArray;
    }

    // Load orders from file
    @SuppressWarnings("unchecked")
    private List<Order> loadOrdersFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ORDER_FILE_PATH))) {
            return (List<Order>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Set CORS headers
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    // Handle preflight CORS requests
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
