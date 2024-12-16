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
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/salesreport/dailyTransactions")
public class DailySalesTransactionsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Fetch daily sales transactions from the database
            Map<String, Double> dailySales = MySQLDataStoreUtilities.getDailySalesTransactions();

            // Prepare JSON response
            JSONArray dailySalesArray = new JSONArray();
            for (Map.Entry<String, Double> entry : dailySales.entrySet()) {
                JSONObject salesData = new JSONObject();
                salesData.put("date", entry.getKey());
                salesData.put("totalSales", entry.getValue());
                dailySalesArray.put(salesData);
            }

            // Send response
            JSONObject responseJson = new JSONObject();
            responseJson.put("dailySales", dailySalesArray);
            response.getWriter().write(responseJson.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unable to fetch daily sales transactions data.\"}");
        }
    }
}