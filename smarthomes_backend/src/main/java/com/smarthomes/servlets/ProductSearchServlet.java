package com.smarthomes.servlets;

import com.smarthomes.models.AjaxUtility;
import com.smarthomes.models.Product;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.List;

@WebServlet("/products/search")
public class ProductSearchServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String searchQuery = request.getParameter("query");
        if (searchQuery == null || searchQuery.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing search query.");
            return;
        }

        List<Product> matchingProducts = new AjaxUtility().searchProductsByName(searchQuery);

        JSONArray productsArray = new JSONArray();
        for (Product product : matchingProducts) {
            JSONObject productJson = new JSONObject();
            productJson.put("name", product.getName());
            productJson.put("id", product.getId());
            productJson.put("price", product.getPrice());
            productsArray.put(productJson);
        }
        response.setContentType("application/json");
        response.getWriter().print(productsArray.toString());
    }
}
