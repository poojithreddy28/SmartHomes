
package com.smarthomes.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

 // Handle preflight CORS request (for the OPTIONS method)
 @Override
 protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
     setCorsHeaders(response);
     response.setStatus(HttpServletResponse.SC_OK);
 }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        setCorsHeaders(response);  // Set CORS headers

                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate(); // This destroys the session
                }
        
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
        
                // Send a JSON response indicating successful logout
                out.print("{\"message\":\"Successfully logged out.\"}");
                out.close();
    }
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
