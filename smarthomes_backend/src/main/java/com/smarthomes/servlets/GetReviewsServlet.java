package com.smarthomes.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.smarthomes.models.MongoDBDataStoreUtilities;

@WebServlet("/get_reviews")
public class GetReviewsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setContentType("application/json");

        String productModelName = request.getParameter("productModelName");

        if (productModelName == null || productModelName.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Product model name is missing\"}");
            return;
        }

        try {
            JSONArray reviews = MongoDBDataStoreUtilities.getReviewsByProductName(productModelName);
            response.getWriter().write(reviews.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error fetching reviews\"}");
        }
    }
}