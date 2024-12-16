package com.smarthomes.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.smarthomes.models.MySQLDataStoreUtilities;
import com.smarthomes.models.Product;

@WebServlet("/product")
public class ViewProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MySQLDataStoreUtilities dbUtility = new MySQLDataStoreUtilities(); // For database operations

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    // GET request to retrieve product by ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(response);

        try {
            // Retrieve the productId from the request parameter
            String productIdStr = request.getParameter("productId");

            // Check if productId is provided
            if (productIdStr == null || productIdStr.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Product ID is required.");
                return;
            }

            int productId = Integer.parseInt(productIdStr);

            // Fetch the product from the database by ID
            Product product = dbUtility.getProductById(productId);

            if (product != null) {
                // Convert product data to JSON and return it
                JSONObject productJSON = new JSONObject();
                productJSON.put("id", product.getId());
                productJSON.put("name", product.getName());
                productJSON.put("price", product.getPrice());
                productJSON.put("category", product.getCategory());
                productJSON.put("description", product.getDescription());
                productJSON.put("imagePath", product.getImage_path());

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(productJSON.toString());
            } else {
                // Product not found
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Product not found.");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error occurred while retrieving the product.");
            e.printStackTrace();
        }
    }
}