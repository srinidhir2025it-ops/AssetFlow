
package com.assetflow.repository;

import com.assetflow.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ExitClearanceRepository {

    public List<Object[]> getAllClearances() throws SQLException {

        List<Object[]> clearances = new ArrayList<>();

        String sql =
            "SELECT ec.clearance_id, e.employee_code, " +
            "e.employee_name, ec.exit_date, ec.assets_returned, " +
            "ec.data_handover, ec.access_revoked, " +
            "ec.clearance_status " +
            "FROM exit_clearance ec " +
            "JOIN employees e ON ec.employee_id = e.employee_id " +
            "ORDER BY ec.clearance_id DESC";

        try (Connection connection =
                 DatabaseConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                clearances.add(new Object[] {
                    result.getInt("clearance_id"),
                    result.getString("employee_code"),
                    result.getString("employee_name"),
                    result.getDate("exit_date"),
                    result.getBoolean("assets_returned") ? "Yes" : "No",
                    result.getBoolean("data_handover") ? "Yes" : "No",
                    result.getBoolean("access_revoked") ? "Yes" : "No",
                    result.getString("clearance_status")
                });
            }
        }

        return clearances;
    }

    public boolean saveClearance(
        int employeeId,
        Date exitDate,
        boolean assetsReturned,
        boolean dataHandover,
        boolean accessRevoked
    ) throws SQLException {

        String status;

        if (assetsReturned && dataHandover && accessRevoked) {
            status = "Completed";
        } else {
            status = "Pending";
        }

        String sql =
            "INSERT INTO exit_clearance " +
            "(employee_id, exit_date, assets_returned, " +
            "data_handover, access_revoked, clearance_status) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection =
                 DatabaseConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql)) {

            statement.setInt(1, employeeId);
            statement.setDate(2, exitDate);
            statement.setBoolean(3, assetsReturned);
            statement.setBoolean(4, dataHandover);
            statement.setBoolean(5, accessRevoked);
            statement.setString(6, status);

            return statement.executeUpdate() > 0;
        }
    }
}
