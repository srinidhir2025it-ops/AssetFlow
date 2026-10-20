
package com.assetflow.model;

public class Asset {

    private int assetId;
    private String assetCode;
    private String assetName;
    private String assetType;
    private String serialNumber;
    private String purchaseDate;
    private String assetCondition;
    private String assetStatus;

    public Asset(int assetId, String assetCode, String assetName,
                 String assetType, String serialNumber,
                 String purchaseDate, String assetCondition,
                 String assetStatus) {

        this.assetId = assetId;
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.assetType = assetType;
        this.serialNumber = serialNumber;
        this.purchaseDate = purchaseDate;
        this.assetCondition = assetCondition;
        this.assetStatus = assetStatus;
    }

    public int getAssetId() {
        return assetId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getAssetCondition() {
        return assetCondition;
    }

    public String getAssetStatus() {
        return assetStatus;
    }
}

