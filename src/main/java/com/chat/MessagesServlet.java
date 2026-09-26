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

public class MessagesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String currentUser = (String) session.getAttribute("username");
        String receiver = req.getParameter("receiver");
        if (receiver == null || receiver.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Receiver is required");
            return;
        }

        String sql = "SELECT id, sender, receiver, message, "
                + "DATE_FORMAT(message_time, '%Y-%m-%d %H:%i:%s') AS formatted_time "
                + "FROM messages "
                + "WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) "
                + "ORDER BY message_time ASC, id ASC";

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currentUser);
            statement.setString(2, receiver.trim());
            statement.setString(3, receiver.trim());
            statement.setString(4, currentUser);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (!first) json.append(',');
                    json.append('{')
                            .append("\"sender\":\"").append(jsonEscape(rs.getString("sender"))).append("\",")
                            .append("\"receiver\":\"").append(jsonEscape(rs.getString("receiver"))).append("\",")
                            .append("\"message\":\"").append(jsonEscape(rs.getString("message"))).append("\",")
                            .append("\"time\":\"").append(jsonEscape(rs.getString("formatted_time"))).append("\"}");
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
