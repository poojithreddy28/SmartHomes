package com.smarthomes.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.smarthomes.models.Cart;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // HashMap to store cart information (key: username, value: user's cart items keyed by productName)
    private static HashMap<String, HashMap<String, Cart>> userCarts = new HashMap<>();
    private static final String CART_FILE_PATH = "/Users/sruthisanjana/Desktop/EWA/smarthomes_backend/cart_data.ser"; // Adjust the path

    @Override
    public void init() throws ServletException {
        // Load cart data from file when the servlet initializes
        File file = new File(CART_FILE_PATH);
        if (file.exists()) {
            userCarts = loadCartsFromFile();
        }
    }
    // Handle preflight CORS requests
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);

        // Read incoming request data (assume JSON format)
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        String requestData = sb.toString();

        try {
            // Parse incoming JSON request data
            JSONObject json = new JSONObject(requestData);
            String username = json.getString("username");
            String productName = json.getString("productName");
            int quantity = json.getInt("quantity");
            double productPrice = json.getDouble("productPrice"); // Ensure price is parsed correctly

            // Update the user's cart (using productName instead of productId)
            userCarts.putIfAbsent(username, new HashMap<>());
            HashMap<String, Cart> userCart = userCarts.get(username);

            // If the product already exists in the cart, update the quantity
            if (userCart.containsKey(productName)) {
                Cart existingCartItem = userCart.get(productName);
                existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            } else {
                // Add new product to the cart
                Cart cartItem = new Cart(productName, quantity, productPrice);
                userCart.put(productName, cartItem);
            }

            // Save updated cart data to file
            saveCartsToFile();

            // Calculate total items in the user's cart
            int totalItems = userCart.values().stream().mapToInt(Cart::getQuantity).sum();
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("message", "Cart updated successfully.");
            jsonResponse.put("cartItemCount", totalItems); // Send back total cart items

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toString());
        } catch (Exception e) {
            // Handle errors gracefully
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("error", "Failed to update cart.");
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);

        String username = request.getParameter("username");
        if (username != null && userCarts.containsKey(username)) {
            HashMap<String, Cart> userCart = userCarts.get(username);

            // Convert user's cart to JSON
            JSONArray cartArray = new JSONArray();
            for (Cart item : userCart.values()) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("productName", item.getProductName());
                itemJson.put("price", item.getProductPrice());
                itemJson.put("quantity", item.getQuantity());
                cartArray.put(itemJson);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(cartArray.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("error", "Cart not found.");
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toString());
        }
    }

    // New DELETE request handler
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);

        // Read incoming data from the request
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        String requestData = sb.toString();

        try {
            // Parse incoming JSON request data
            JSONObject json = new JSONObject(requestData);
            String username = json.getString("username");
            String productName = json.getString("productName");

            // Check if user and product exist in the cart
            if (userCarts.containsKey(username)) {
                HashMap<String, Cart> userCart = userCarts.get(username);

                if (userCart.containsKey(productName)) {
                    // Remove the product from the cart
                    userCart.remove(productName);

                    // Save updated cart data to file
                    saveCartsToFile();

                    // Respond with success message
                    response.setStatus(HttpServletResponse.SC_OK);
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("message", "Product removed successfully.");
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    out.print(jsonResponse.toString());
                } else {
                    // Product not found in cart
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("error", "Product not found in cart.");
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    out.print(jsonResponse.toString());
                }
            } else {
                // User cart not found
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("error", "User cart not found.");
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(jsonResponse.toString());
            }
        } catch (Exception e) {
            // Handle errors gracefully
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("error", "Failed to delete product from cart.");
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toString());
            e.printStackTrace();
        }
    }

    // Save cart data to file
    private static void saveCartsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CART_FILE_PATH))) {
            oos.writeObject(userCarts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load cart data from file
    @SuppressWarnings("unchecked")
    private static HashMap<String, HashMap<String, Cart>> loadCartsFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CART_FILE_PATH))) {
            return (HashMap<String, HashMap<String, Cart>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>(); // Return empty cart if file doesn't exist or can't be loaded
        }
    }

    // Set CORS headers
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    
}
