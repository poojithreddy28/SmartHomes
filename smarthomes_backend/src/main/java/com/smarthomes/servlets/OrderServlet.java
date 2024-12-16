package com.smarthomes.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.sql.Connection;


import org.json.JSONArray;
import org.json.JSONObject;

import com.smarthomes.models.Cart;
import com.smarthomes.models.MySQLDataStoreUtilities;
import com.smarthomes.models.Order;

@WebServlet("/place_order")
public class OrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String CART_FILE_PATH = "/Users/sruthisanjana/Desktop/EWA/smarthomes_backend/cart_data.ser"; // Path to cart data
    private static final String ORDER_FILE_PATH = "/Users/sruthisanjana/Desktop/EWA/smarthomes_backend/orders.ser";   // Path to order data
    
    private static HashMap<String, HashMap<String, Cart>> userCarts = new HashMap<>();
    private static List<Order> orders = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        // Load cart data from file when the servlet initializes
        File cartFile = new File(CART_FILE_PATH);
        if (cartFile.exists()) {
            userCarts = loadCartsFromFile();
        }

        // Load existing orders from file if any
        File orderFile = new File(ORDER_FILE_PATH);
        if (orderFile.exists()) {
            orders = loadOrdersFromFile();
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);
        String action = request.getParameter("action");

        if ("get_store_data".equals(action)) {
            System.out.println("In  get store data");
            fetchStoreData(response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid action");
        }
    }

    private void fetchStoreData(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
            String query = "SELECT store_id, street, city, state, zip_code FROM Stores";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            JSONArray storesArray = new JSONArray();

            while (rs.next()) {
                JSONObject storeObj = new JSONObject();
                storeObj.put("store_id", rs.getInt("store_id"));
                storeObj.put("address", rs.getString("street"));
                storeObj.put("city", rs.getString("city"));
                storeObj.put("state", rs.getString("state"));
                storeObj.put("zip_code", rs.getString("zip_code"));
                storesArray.put(storeObj);
            }

            response.getWriter().write(storesArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error fetching store data\"}");
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        String requestData = sb.toString();
    
        try {
            JSONObject json = new JSONObject(requestData);
            String username = json.getString("username");
    
            // Extracting checkout details
            String firstName = json.getString("firstName");
            String lastName = json.getString("lastName");
            String phone = json.getString("phone");
            String email = json.getString("email");
            String address = json.getString("address");
            String city = json.getString("city");
            String state = json.getString("state");
            String postalCode = json.getString("postalCode");
            String cardNumber = json.getString("cardNumber");
            String shippingMethod = json.getString("shippingMethod");
            String storeLocation = null;
        int storeId = 0;
        String storeAddress = null;

        // If shipping method is "InStore Pickup", fetch store details
        if ("InStore Pickup".equals(shippingMethod)) {
            storeLocation = json.getString("storeLocation");  // Assume storeLocation is passed in the JSON
            storeId = new MySQLDataStoreUtilities().getStoreDetailsByLocation(storeLocation);
            storeAddress = storeLocation;  // Or get from the database if needed
        }
            System.out.println(cardNumber + address + username + storeLocation);
    
            // Check if the cart exists for the user
            if (userCarts.containsKey(username)) {
                HashMap<String, Cart> userCart = userCarts.get(username);
                double totalSales = calculateTotalSales(userCart);

                System.out.println(username);

                
                // Get user ID by email
                int userId = new MySQLDataStoreUtilities().getUserIdByName(username);
                String customerName = firstName + " " + lastName;
                String customerAddress = address + ", " + city + ", " + state + " - " + postalCode;
    
                // Get current time for purchase_date
                LocalDateTime purchaseDate = LocalDateTime.now();
    
                // Calculate ship_date and delivery_date based on purchase_date
                LocalDateTime shipDate = purchaseDate.plusDays(7);  // 7 days later
                LocalDateTime deliveryDate = purchaseDate.plusDays(14);  // 14 days later
    
                // Place order in the database
                boolean success = new MySQLDataStoreUtilities().placeOrder(
                    userId,
                    customerName,
                    customerAddress,
                    cardNumber,
                    totalSales,
                    shippingMethod,
                    storeId,
                    storeAddress,
                    userCart,
                    Timestamp.valueOf(purchaseDate),
                    Timestamp.valueOf(shipDate),
                    Timestamp.valueOf(deliveryDate)  // Include the calculated dates
                );
    
                if (success) {
                    userCarts.remove(username);
                    saveCartsToFile();
    
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("message", "Order placed successfully.");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().print(jsonResponse.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Failed to place order.");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Cart not found for user.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error occurred while placing order.");
        }
    }
    
    private double calculateTotalSales(HashMap<String, Cart> userCart) {
        return userCart.values().stream().mapToDouble(cart -> cart.getQuantity() * cart.getProductPrice()).sum();
    }









    // @Override
    // protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //     // Set CORS headers if necessary
    //     setCorsHeaders(response);
        
    //     StringBuilder sb = new StringBuilder();
    //     String line;
    //     while ((line = request.getReader().readLine()) != null) {
    //         sb.append(line);
    //     }
    //     String requestData = sb.toString();
        
    //     try {
    //         // Parse incoming JSON data
    //         JSONObject json = new JSONObject(requestData);
    //         String username = json.getString("username");
    
    //         // Extract checkout details
    //         String firstName = json.getString("firstName");
    //         String lastName = json.getString("lastName");
    //         String phone = json.getString("phone");
    //         String email = json.getString("email");
    //         String address = json.getString("address");
    //         String city = json.getString("city");
    //         String state = json.getString("state");
    //         String postalCode = json.getString("postalCode");
    //         String cardNumber = json.getString("cardNumber");
    //         String expiry = json.getString("expiry");
    //         String cvv = json.getString("cvv");
    //         String shippingMethod = json.getString("shippingMethod");
    //         String storeLocation = json.has("storeLocation") ? json.getString("storeLocation") : null;
    
    //         // Retrieve the cart for the given user
    //         if (userCarts.containsKey(username)) {
    //             HashMap<String, Cart> userCart = userCarts.get(username);
                
    //             // Create a new Order object with delivery date set to 2 weeks from now
    //             Order newOrder = new Order(username, firstName, lastName, phone, email, address, city, state, postalCode, cardNumber, expiry, cvv, shippingMethod, storeLocation, userCart);
    
    //             // Add the new order to the list
    //             orders.add(newOrder);
                
    //             // Save updated order data to file
    //             saveOrdersToFile();
    
    //             // Clear the user's cart after placing the order
    //             userCarts.remove(username);
    //             saveCartsToFile();
    
    //             // Respond with success message including delivery date
    //             JSONObject jsonResponse = new JSONObject();
    //             jsonResponse.put("message", "Order placed successfully.");
    //             jsonResponse.put("orderId", newOrder.getOrderId());
    //             jsonResponse.put("deliveryDate", newOrder.getDeliveryDate().toString());  // Include delivery date
    //             response.setStatus(HttpServletResponse.SC_OK);
    //             response.setContentType("application/json");
    //             PrintWriter out = response.getWriter();
    //             out.print(jsonResponse.toString());
    //         } else {
    //             // If cart not found, send error
    //             response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    //             JSONObject jsonResponse = new JSONObject();
    //             jsonResponse.put("error", "Cart not found for user.");
    //             response.setContentType("application/json");
    //             PrintWriter out = response.getWriter();
    //             out.print(jsonResponse.toString());
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    //         JSONObject jsonResponse = new JSONObject();
    //         jsonResponse.put("error", "Failed to place order.");
    //         response.setContentType("application/json");
    //         PrintWriter out = response.getWriter();
    //         out.print(jsonResponse.toString());
    //     }
    // }
    

    // Load carts from file
    @SuppressWarnings("unchecked")
    private static HashMap<String, HashMap<String, Cart>> loadCartsFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CART_FILE_PATH))) {
            return (HashMap<String, HashMap<String, Cart>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Save carts to file
    private static void saveCartsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CART_FILE_PATH))) {
            oos.writeObject(userCarts);
            System.out.println("Cart data written to file.");  // Debug: Ensure cart data is written to file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load orders from file
    @SuppressWarnings("unchecked")
    private static List<Order> loadOrdersFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ORDER_FILE_PATH))) {
            return (List<Order>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Save orders to file
    private static void saveOrdersToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDER_FILE_PATH))) {
            oos.writeObject(orders);
            System.out.println("Order data written to file.");  // Debug: Ensure order data is written to file
        } catch (IOException e) {
            e.printStackTrace();
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
