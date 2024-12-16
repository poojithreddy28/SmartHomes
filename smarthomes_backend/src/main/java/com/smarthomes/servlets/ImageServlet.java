package com.smarthomes.servlets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/images/*")
public class ImageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get the image filename from the URL path
        String filename = request.getPathInfo().substring(1); // Remove the leading "/"
        File file = new File("/Users/sruthisanjana/Desktop/EWA/Sep25_OrderLeft", filename);
        if (file.exists()) {
            response.setContentType("image/jpeg");
            Files.copy(file.toPath(), response.getOutputStream());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}