
package com.assetflow.repository;

import com.assetflow.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogRepository {

    public boolean logAction(String description) throws SQLException {

        String sql =
            "INSERT INTO activity_logs (action_description) VALUES (?)";

        try (Connection connection =
                 DatabaseConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql)) {

            statement.setString(1, description);

            return statement.executeUpdate() > 0;
        }
    }

    public List<Object[]> getAllLogs() throws SQLException {

        List<Object[]> logs = new ArrayList<>();

        String sql =
            "SELECT log_id, action_description, action_date " +
            "FROM activity_logs " +
            "ORDER BY action_date DESC, log_id DESC";

        try (Connection connection =
                 DatabaseConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                logs.add(new Object[] {
                    result.getInt("log_id"),
                    result.getString("action_description"),
                    result.getTimestamp("action_date")
                });
            }
        }

        return logs;
    }
}
