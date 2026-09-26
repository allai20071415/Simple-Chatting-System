package com.chat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String currentUser = (String) session.getAttribute("username");
        String sql = "SELECT username FROM users WHERE username <> ? ORDER BY username";
        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currentUser);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (!first) json.append(',');
                    json.append('"').append(jsonEscape(rs.getString("username"))).append('"');
                    first = false;
                }
            }
            json.append(']');
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().print(json);
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    private String jsonEscape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\t", "\\t");
    }
}
