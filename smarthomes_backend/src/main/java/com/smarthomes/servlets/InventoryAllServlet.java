package com.smarthomes.servlets;

import com.smarthomes.models.MySQLDataStoreUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/inventory/all")
public class InventoryAllServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

                setCorsHeaders(response); 
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Fetch the product data
            MySQLDataStoreUtilities dbUtility = new MySQLDataStoreUtilities();
            List<Map<String, Object>> products = dbUtility.getAllProductsWithQuantity();
            System.out.println("In inventory before");

            // Prepare JSON response
            JSONArray productArray = new JSONArray();

            for (Map<String, Object> product : products) {
                JSONObject productJson = new JSONObject();
                productJson.put("name", product.get("name"));
                productJson.put("price", product.get("price"));
                productJson.put("quantity", product.get("quantity"));
                productArray.put(productJson);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(productArray.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to fetch inventory data.\"}");
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