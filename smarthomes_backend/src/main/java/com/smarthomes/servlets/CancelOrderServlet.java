package com.smarthomes.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.smarthomes.models.MySQLDataStoreUtilities;
import com.smarthomes.models.Order;

@WebServlet("/cancel_order")
public class CancelOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ORDER_FILE_PATH = "/Users/sruthisanjana/Desktop/EWA/smarthomes_backend/orders.ser"; // Update with your path
    
    private static List<Order> orders = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        File orderFile = new File(ORDER_FILE_PATH);
        if (orderFile.exists()) {
            orders = loadOrdersFromFile();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);

        // Read the request body
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();

        try {
            // Parse the JSON request body
            JSONObject json = new JSONObject(requestBody);
            int orderId = json.getInt("orderId");  // Assuming orderId is an integer

            // Delete the order from the database
            boolean orderDeleted = MySQLDataStoreUtilities.deleteOrderFromDatabase(orderId);

            if (orderDeleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();
                out.print(new JSONObject().put("message", "Order canceled successfully").toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print(new JSONObject().put("error", "Order not found").toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", "Failed to cancel order").toString());
            e.printStackTrace();
        }}

    // Load and save orders (helper methods)
    @SuppressWarnings("unchecked")
    private List<Order> loadOrdersFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ORDER_FILE_PATH))) {
            return (List<Order>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveOrdersToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDER_FILE_PATH))) {
            oos.writeObject(orders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CORS headers
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}