
package com.assetflow.repository;

import com.assetflow.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardRepository {

    public int getTotalEmployees() throws SQLException {
        return getCount("SELECT COUNT(*) FROM employees");
    }

    public int getTotalAssets() throws SQLException {
        return getCount("SELECT COUNT(*) FROM assets");
    }

    public int getAvailableAssets() throws SQLException {
        return getCount(
            "SELECT COUNT(*) FROM assets WHERE asset_status = 'Available'"
        );
    }

    public int getAllocatedAssets() throws SQLException {
        return getCount(
            "SELECT COUNT(*) FROM assets WHERE asset_status = 'Allocated'"
        );
    }

    private int getCount(String sql) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return result.getInt(1);
            }

            return 0;
        }
    }
}
