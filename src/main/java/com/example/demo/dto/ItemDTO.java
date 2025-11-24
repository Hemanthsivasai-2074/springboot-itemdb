package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ItemDTO {
    private String itemName;
    private String itemCost;
    private Integer itemQuantity;
    private String itemPack;
    private Integer itemContent;
    private Integer itemDimensions;
    private String itemOriginLocation;
    private Boolean itemShip;
    private String itemCompany;
    private LocalDateTime itemManufacturingDateTime;
    private LocalDate itemExpiryDate;

    // ✅ Add getters and setters manually
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemCost() { return itemCost; }
    public void setItemCost(String itemCost) { this.itemCost = itemCost; }

    public Integer getItemQuantity() { return itemQuantity; }
    public void setItemQuantity(Integer itemQuantity) { this.itemQuantity = itemQuantity; }

    public String getItemPack() { return itemPack; }
    public void setItemPack(String itemPack) { this.itemPack = itemPack; }

    public Integer getItemContent() { return itemContent; }
    public void setItemContent(Integer itemContent) { this.itemContent = itemContent; }

    public Integer getItemDimensions() { return itemDimensions; }
    public void setItemDimensions(Integer itemDimensions) { this.itemDimensions = itemDimensions; }

    public String getItemOriginLocation() { return itemOriginLocation; }
    public void setItemOriginLocation(String itemOriginLocation) { this.itemOriginLocation = itemOriginLocation; }

    public Boolean getItemShip() { return itemShip; }
    public void setItemShip(Boolean itemShip) { this.itemShip = itemShip; }

    public String getItemCompany() { return itemCompany; }
    public void setItemCompany(String itemCompany) { this.itemCompany = itemCompany; }

    public LocalDateTime getItemManufacturingDateTime() { return itemManufacturingDateTime; }
    public void setItemManufacturingDateTime(LocalDateTime itemManufacturingDateTime) { this.itemManufacturingDateTime = itemManufacturingDateTime; }

    public LocalDate getItemExpiryDate() { return itemExpiryDate; }
    public void setItemExpiryDate(LocalDate itemExpiryDate) { this.itemExpiryDate = itemExpiryDate; }
}