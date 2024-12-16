package com.smarthomes.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

import org.json.JSONArray;
import org.json.JSONObject;

import com.smarthomes.models.Order;

@WebServlet("/manage_orders")
public class SalesPersonOrders extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ORDER_FILE_PATH = "/path/to/orders.ser";  // Change to your path

    private static List<Order> orders = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        // Load existing orders from file when the servlet initializes
        File orderFile = new File(ORDER_FILE_PATH);
        if (orderFile.exists()) {
            orders = loadOrdersFromFile();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);

        // Convert all orders to JSON and return them
        JSONArray ordersArray = new JSONArray();
        
        for (Order order : orders) {
            System.out.println(order.getOrderId());
            JSONObject orderJson = new JSONObject();
            orderJson.put("orderId", order.getOrderId());
            orderJson.put("username", order.getUsername());
            orderJson.put("firstName", order.getFirstName());
            orderJson.put("lastName", order.getLastName());
            orderJson.put("email", order.getEmail());
            orderJson.put("phone", order.getPhone());
            orderJson.put("address", order.getAddress());
            orderJson.put("deliveryDate", order.getDeliveryDate().toString());
            ordersArray.put(orderJson);
        }
        response.setContentType("application/json");

        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.print(ordersArray.toString());
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsHeaders(response);

        String orderId = request.getParameter("orderId"); // Read orderId from URL query parameters

        try {
            // Find and remove the order with the given orderId
            boolean orderDeleted = orders.removeIf(order -> order.getOrderId().equals(orderId));

            if (orderDeleted) {
                saveOrdersToFile(); // Save the updated order list
                response.setStatus(HttpServletResponse.SC_OK);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("message", "Order deleted successfully");
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(jsonResponse.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("error", "Order not found");
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(jsonResponse.toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("error", "Failed to delete order.");
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toString());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Set CORS headers
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
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