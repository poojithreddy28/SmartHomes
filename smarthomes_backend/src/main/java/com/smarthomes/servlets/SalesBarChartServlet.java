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

@WebServlet("/salesreport/salesBarChart")
public class SalesBarChartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Fetch total sales data from the database
            List<Product> soldProducts = MySQLDataStoreUtilities.getAllProductsSold();

            // Prepare JSON response
            JSONArray chartDataArray = new JSONArray();
            for (Product product : soldProducts) {
                JSONArray productData = new JSONArray();
                productData.put(product.getName());
                productData.put(product.getTotalSales());
                chartDataArray.put(productData);
            }

            

            // Send response
            JSONObject responseJson = new JSONObject();
            responseJson.put("chartData", chartDataArray);
            response.getWriter().write(responseJson.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unable to fetch sales chart data.\"}");
        }
    }
}