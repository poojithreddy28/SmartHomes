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

@WebServlet("/inventory/sale")
public class ProductsOnSaleServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        List<Product> productsOnSale = new MySQLDataStoreUtilities().getProductsOnSale();

        // Create a JSON array for the products
        JSONArray productsArray = new JSONArray();
        for (Product product : productsOnSale) {
            JSONObject productJson = new JSONObject();
            productJson.put("name", product.getName());
            productJson.put("price", product.getPrice());
            productJson.put("discount", product.getDiscount());
            productJson.put("quantity", product.getQuantity());
            productsArray.put(productJson);
        }

        // Send the JSON response
        response.setContentType("application/json");
        response.getWriter().print(productsArray.toString());
    }
}