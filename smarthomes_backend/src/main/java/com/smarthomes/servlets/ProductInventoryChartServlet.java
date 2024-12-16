package com.smarthomes.servlets;

import com.smarthomes.models.MySQLDataStoreUtilities;
import com.smarthomes.models.Product;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/inventory/chart")
public class ProductInventoryChartServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set the response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            // Fetch all products from the database
            List<Product> productList = new MySQLDataStoreUtilities().getAllProductsWithQuantityChart();

            // Create a JSON array to store product names and quantities
            JSONArray productArray = new JSONArray();

            for (Product product : productList) {
                JSONObject productJson = new JSONObject();
                productJson.put("name", product.getName());
                productJson.put("quantity", product.getQuantity());
                productArray.put(productJson);
            }

            System.out.println("in bar chart servlet");

            // Return the JSON response with the product names and quantities
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("products", productArray);
            out.print(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Failed to fetch product inventory data.\"}");
        }
    }
}