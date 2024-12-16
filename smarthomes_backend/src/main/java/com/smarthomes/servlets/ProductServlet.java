package com.smarthomes.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;

import com.smarthomes.models.MySQLDataStoreUtilities;
import com.smarthomes.models.Product;

@WebServlet("/products")
@MultipartConfig // Enables file uploads
public class ProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MySQLDataStoreUtilities dbUtility = new MySQLDataStoreUtilities(); // For database operations

    private static final String IMAGE_UPLOAD_DIR = "/Users/sruthisanjana/Desktop/EWA/smarthomes/public/images/All/";

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

    // GET request to retrieve products by category
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(response);

        // Fetch products by category or all products
        String category = request.getParameter("category");
        List<Product> productList;

        if (category == null || category.equals("All Products")) {
            productList = dbUtility.getAllProducts();  // Fetch all products from the database
        } else {
            productList = dbUtility.getProductsByCategory(category);  // Fetch products by category
        }

        // Convert product data to JSON and return it
        JSONArray productArray = new JSONArray();
        for (Product product : productList) {
            JSONObject productJSON = new JSONObject();
            productJSON.put("id", product.getId());
            productJSON.put("name", product.getName());
            productJSON.put("price", product.getPrice());
            productJSON.put("category", product.getCategory());
            productJSON.put("description", product.getDescription());
            productJSON.put("imagePath", product.getImage_path()); // Include image path
            productArray.put(productJSON);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(productArray.toString());
    }

    // POST request to add a new product
    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    setCorsHeaders(response);

    if (!ServletFileUpload.isMultipartContent(request)) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("Form must be multipart/form-data.");
        return;
    }

    DiskFileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload(factory);

    String name = null;
    double price = 0;
    String category = null;
    String description = null;
    String imagePath = null;
    double discount = 0;
    int warranty = 0;
    double manufacturerRebate = 0;
    int quantity = 0; // New field to capture quantity

    try {
        List<FileItem> formItems = upload.parseRequest(request);

        for (FileItem item : formItems) {
            if (item.isFormField()) {
                String fieldName = item.getFieldName();
                String fieldValue = item.getString();

                switch (fieldName) {
                    case "name":
                        name = fieldValue;
                        break;
                    case "price":
                        price = Double.parseDouble(fieldValue);
                        break;
                    case "category":
                        category = fieldValue;
                        break;
                    case "description":
                        description = fieldValue;
                        break;
                    case "discount":
                        discount = Double.parseDouble(fieldValue);
                        break;
                    case "warranty":
                        warranty = Integer.parseInt(fieldValue);
                        break;
                    case "manufacturerRebate":
                        manufacturerRebate = Double.parseDouble(fieldValue);
                        break;
                    case "quantity": // Handling quantity field
                        quantity = Integer.parseInt(fieldValue);
                        break;
                }
            } else {
                String fileName = new File(item.getName()).getName();
                if (fileName != null && !fileName.isEmpty()) {
                    imagePath = IMAGE_UPLOAD_DIR + fileName;
                    File storeFile = new File(imagePath);
                    item.write(storeFile);
                }
            }
        }

        // Pass quantity in addition to the existing parameters
        boolean success = dbUtility.addProduct(name, price, category, description, imagePath, discount, warranty, manufacturerRebate, quantity);

        if (success) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("Product added successfully.");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to add product.");
        }

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("Error occurred while adding product.");
        e.printStackTrace();
    }
}

    @Override
protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    setCorsHeaders(response);

    try {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        String requestData = sb.toString();
        JSONObject json = new JSONObject(requestData);

        // Check if the ID is being passed and is not null
        if (!json.has("id") || json.get("id").equals(JSONObject.NULL)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid product ID.");
            return;
        }

        int id = json.getInt("id");

        // Delete the product from the database
        boolean success = dbUtility.deleteProduct(id);

        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Product deleted successfully.");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to delete product.");
        }

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("Error occurred while deleting product.");
        e.printStackTrace();
    }
}
}