
package com.assetflow.repository;

import com.assetflow.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AllocationRepository {

    public boolean allocateAsset(int assetId, int employeeId)
            throws SQLException {

        String checkSql =
            "SELECT asset_status FROM assets WHERE asset_id = ?";

        String insertSql =
            "INSERT INTO allocations " +
            "(asset_id, employee_id, allocation_date, allocation_status) " +
            "VALUES (?, ?, CURDATE(), 'Allocated')";

        String updateSql =
            "UPDATE assets SET asset_status = 'Allocated' " +
            "WHERE asset_id = ?";

        try (Connection connection =
                 DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                try (PreparedStatement check =
                         connection.prepareStatement(checkSql)) {

                    check.setInt(1, assetId);

                    try (ResultSet result = check.executeQuery()) {

                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }

                        if (!"Available".equalsIgnoreCase(
                                result.getString("asset_status"))) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement insert =
                         connection.prepareStatement(insertSql)) {

                    insert.setInt(1, assetId);
                    insert.setInt(2, employeeId);
                    insert.executeUpdate();
                }

                try (PreparedStatement update =
                         connection.prepareStatement(updateSql)) {

                    update.setInt(1, assetId);
                    update.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<Object[]> getAllAllocations()
            throws SQLException {

        List<Object[]> allocations = new ArrayList<>();

        String sql =
            "SELECT al.allocation_id, a.asset_code, a.asset_name, " +
            "e.employee_code, e.employee_name, al.allocation_date, " +
            "al.return_date, al.allocation_status " +
            "FROM allocations al " +
            "JOIN assets a ON al.asset_id = a.asset_id " +
            "JOIN employees e ON al.employee_id = e.employee_id " +
            "ORDER BY al.allocation_id DESC";

        try (Connection connection =
                 DatabaseConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                allocations.add(new Object[] {
                    result.getInt("allocation_id"),
                    result.getString("asset_code"),
                    result.getString("asset_name"),
                    result.getString("employee_code"),
                    result.getString("employee_name"),
                    result.getDate("allocation_date"),
                    result.getDate("return_date"),
                    result.getString("allocation_status")
                });
            }
        }

        return allocations;
    }
}
