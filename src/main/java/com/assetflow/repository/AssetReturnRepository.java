
package com.assetflow.repository;

import com.assetflow.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AssetReturnRepository {

    public boolean returnAsset(
            int allocationId,
            String returnCondition,
            String remarks) throws SQLException {

        String checkSql =
            "SELECT asset_id, allocation_status FROM allocations " +
            "WHERE allocation_id = ?";

        String returnSql =
            "INSERT INTO asset_returns " +
            "(allocation_id, actual_return_date, return_condition, remarks) " +
            "VALUES (?, ?, ?, ?)";

        String allocationSql =
            "UPDATE allocations SET allocation_status = 'Returned', " +
            "return_date = ? WHERE allocation_id = ?";

        String assetSql =
            "UPDATE assets SET asset_status = ? WHERE asset_id = ?";

        try (Connection connection =
                 DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int assetId;

                try (PreparedStatement check =
                         connection.prepareStatement(checkSql)) {

                    check.setInt(1, allocationId);

                    try (ResultSet result = check.executeQuery()) {
                        if (!result.next() ||
                            !"Allocated".equalsIgnoreCase(
                                result.getString("allocation_status"))) {

                            connection.rollback();
                            return false;
                        }

                        assetId = result.getInt("asset_id");
                    }
                }

                Date today = new Date(System.currentTimeMillis());

                try (PreparedStatement statement =
                         connection.prepareStatement(returnSql)) {

                    statement.setInt(1, allocationId);
                    statement.setDate(2, today);
                    statement.setString(3, returnCondition);
                    statement.setString(4, remarks);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement =
                         connection.prepareStatement(allocationSql)) {

                    statement.setDate(1, today);
                    statement.setInt(2, allocationId);
                    statement.executeUpdate();
                }

                String newAssetStatus =
                    "Damaged".equalsIgnoreCase(returnCondition)
                    ? "Maintenance" : "Available";

                try (PreparedStatement statement =
                         connection.prepareStatement(assetSql)) {

                    statement.setString(1, newAssetStatus);
                    statement.setInt(2, assetId);
                    statement.executeUpdate();
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

    public List<Object[]> getAllReturns() throws SQLException {

        List<Object[]> returns = new ArrayList<>();

        String sql =
            "SELECT r.return_id, r.allocation_id, " +
            "a.asset_code, a.asset_name, " +
            "e.employee_code, e.employee_name, " +
            "r.actual_return_date, r.return_condition, r.remarks " +
            "FROM asset_returns r " +
            "JOIN allocations al ON r.allocation_id = al.allocation_id " +
            "JOIN assets a ON al.asset_id = a.asset_id " +
            "JOIN employees e ON al.employee_id = e.employee_id " +
            "ORDER BY r.return_id DESC";

        try (Connection connection =
                 DatabaseConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                returns.add(new Object[] {
                    result.getInt("return_id"),
                    result.getInt("allocation_id"),
                    result.getString("asset_code"),
                    result.getString("asset_name"),
                    result.getString("employee_code"),
                    result.getString("employee_name"),
                    result.getDate("actual_return_date"),
                    result.getString("return_condition"),
                    result.getString("remarks")
                });
            }
        }

        return returns;
    }
}
