package com.chat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");

        if (username == null || username.trim().isEmpty()) {
            resp.sendRedirect("login.jsp?error=1");
            return;
        }

        String sql = "SELECT username FROM users WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    req.getSession(true).setAttribute("username", rs.getString("username"));
                    resp.sendRedirect("chat.jsp");
                } else {
                    resp.sendRedirect("login.jsp?error=1");
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database connection/login error: " + e.getMessage(), e);
        }
    }
}
