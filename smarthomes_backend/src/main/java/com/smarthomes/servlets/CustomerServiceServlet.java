package com.smarthomes.servlets;

import java.io.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONObject;

import com.smarthomes.models.MySQLDataStoreUtilities;
import com.smarthomes.models.Ticket;

@WebServlet("/CustomerServiceServlet")
@MultipartConfig
public class CustomerServiceServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "/Users/sruthisanjana/Desktop/EWA/Sep25_OrderLeft/Archive 2/smarthomes/public/";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_API_KEY = "openai-api-key";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        JSONObject jsonResponse = new JSONObject(); // Initialize jsonResponse at the start

        try {
            if (!ServletFileUpload.isMultipartContent(request)) {
                jsonResponse.put("error", "Form must have enctype=multipart/form-data.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(jsonResponse.toString());
                return;
            }

            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> formItems = upload.parseRequest(request);

            String username = null, orderId = null, query = null, imagePath = null;
            File uploadedFile = null;
            // String Path = UPLOAD_DIRECTORY + fileName
            // String ticketId = generateTicketId();

            for (FileItem item : formItems) {
                if (item.isFormField()) {
                    switch (item.getFieldName()) {
                        case "username":
                            username = item.getString();
                            break;
                        case "orderId":
                            orderId = item.getString();
                            break;
                        case "query":
                            query = item.getString();
                            break;
                    }
                } else if ("file".equals(item.getFieldName())) {
                    String fileName = new File(item.getName()).getName();
                    uploadedFile = new File(UPLOAD_DIRECTORY + fileName);
                    item.write(uploadedFile);
                    imagePath = uploadedFile.getAbsolutePath();
                    
                }
            }

            if (username == null || orderId == null || query == null || imagePath == null || uploadedFile == null) {
                jsonResponse.put("error", "Missing required form fields.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(jsonResponse.toString());
                return;
            }

            // Convert the image to Base64
            String base64Image = convertImageToBase64(uploadedFile);
            //jsonResponse.put("base64Image", base64Image); // Log base64 image in the response for debugging

            // Analyze the image with OpenAI
            String decision = analyzeImageWithOpenAI(base64Image);
            jsonResponse.put("decision", decision);

            // Save ticket to the database
            String ticketID = saveToDatabase(orderId, username, query, decision, imagePath);
jsonResponse.put("message", "Ticket submitted successfully.");
jsonResponse.put("ticketId", ticketID);
            

            response.setContentType("application/json");
            response.getWriter().print(jsonResponse.toString());

        } catch (Exception e) {
            jsonResponse.put("error", "Exception: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().print(jsonResponse.toString());
            e.printStackTrace();
        }
    }

    private String convertImageToBase64(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private String analyzeImageWithOpenAI(String base64Image) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(OPENAI_API_URL);
            httpPost.setHeader("Authorization", "Bearer " + OPENAI_API_KEY);
            httpPost.setHeader("Content-Type", "application/json");
    
            JSONObject json = new JSONObject();
            json.put("model", "gpt-4o-mini");
    
            // Construct the message with both text instruction and image data
            json.put("messages", List.of(
                    new JSONObject().put("role", "user").put("content", List.of(
                            new JSONObject().put("type", "text").put("text",
                                    "You are an expert decision maker for customer service. Based on the provided image, respond with one of the following decisions only, without extra information: " +
                                            "Refund Order (if the product or its packaging is heavily damaged, completely broken, or unusable, such as missing essential parts or beyond repair), " +
                                            "Replace Order (if the product or its packaging is damaged but the product appears functional, with minor cracks, scratches, or non-essential parts damaged, or a small opening in the package), " +
                                            "or Escalate to Human Agent (if the product or package appears undamaged, or if the issue is unclear, ambiguous, or complex and needs further investigation)."),
                            new JSONObject().put("type", "image_url").put("image_url", new JSONObject()
                                    .put("url", "data:image/jpeg;base64," + base64Image)
                                    .put("detail", "low"))
                    ))
            ));
    
            StringEntity entity = new StringEntity(json.toString());
            httpPost.setEntity(entity);
    
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity());
                System.out.println("OpenAI Response: " + responseString); // Log OpenAI response for debugging
                JSONObject responseJson = new JSONObject(responseString);
                return responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            } catch (ParseException e) {
                e.printStackTrace();
                return "Error parsing OpenAI response";
            }
        }
    }

    private String saveToDatabase(String orderId, String username, String query, String response, String imagePath) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthomes", "poojith", "poojith");
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO tickets (order_id, username, query, response, created_at, imagePath) VALUES (?, ?, ?, ?, NOW(), ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
    
            // Set parameters in the correct order
            stmt.setString(1, orderId);
            stmt.setString(2, username);
            stmt.setString(3, query);
            stmt.setString(4, response);   // Agent's response
            stmt.setString(5, imagePath);  // Path to image file
    
            // Execute update and check for errors
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ticket failed, no rows affected.");
            }
    
            // Retrieve the generated ticket ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getString(1);  // Return the generated ticket ID
                } else {
                    throw new SQLException("Creating ticket failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            e.printStackTrace();
            return null;  // Return null in case of database error
        }
    }

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    String ticketId = request.getParameter("ticketId");
    JSONObject jsonResponse = new JSONObject();

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthomes", "poojith", "poojith");
         PreparedStatement stmt = connection.prepareStatement("SELECT query, response, order_id, imagePath FROM tickets WHERE id = ?")) {

        stmt.setString(1, ticketId);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                jsonResponse.put("query", rs.getString("query"));
                jsonResponse.put("response", rs.getString("response"));
                jsonResponse.put("order_id",rs.getString("order_id"));
                // Directly add imagePath to JSON response
                jsonResponse.put("imagePath", rs.getString("imagePath"));

                response.setContentType("application/json");
                response.getWriter().print(jsonResponse.toString());
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
    }
}
    
}