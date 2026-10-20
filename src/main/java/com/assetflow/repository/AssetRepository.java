
package com.assetflow.repository;

import com.assetflow.DatabaseConnection;
import com.assetflow.model.Asset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssetRepository {

    public List<Asset> getAllAssets() throws SQLException {

        List<Asset> assets = new ArrayList<>();

        String sql = "SELECT * FROM assets ORDER BY asset_id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Asset asset = new Asset(
                    resultSet.getInt("asset_id"),
                    resultSet.getString("asset_code"),
                    resultSet.getString("asset_name"),
                    resultSet.getString("asset_type"),
                    resultSet.getString("serial_number"),
                    resultSet.getString("purchase_date"),
                    resultSet.getString("asset_condition"),
                    resultSet.getString("asset_status")
                );

                assets.add(asset);
            }
        }

        return assets;
    }
}
