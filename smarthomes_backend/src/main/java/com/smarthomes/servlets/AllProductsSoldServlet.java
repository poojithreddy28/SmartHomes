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
import java.sql.SQLException;
import java.util.List;

@WebServlet("/salesreport/allProductsSold")
public class AllProductsSoldServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Fetch all products sold from the database
            List<Product> soldProducts = MySQLDataStoreUtilities.getAllProductsSold();

            // Prepare JSON response
            JSONArray productsArray = new JSONArray();
            for (Product product : soldProducts) {
                JSONObject productJson = new JSONObject();
                productJson.put("name", product.getName());
                productJson.put("price", product.getPrice());
                productJson.put("quantitySold", product.getQuantitySold());
                productJson.put("totalSales", product.getTotalSales());
                productsArray.put(productJson);
            }

            // Send response
            JSONObject responseJson = new JSONObject();
            responseJson.put("products", productsArray);
            response.getWriter().write(responseJson.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unable to fetch products sold data.\"}");
        }
    }
}