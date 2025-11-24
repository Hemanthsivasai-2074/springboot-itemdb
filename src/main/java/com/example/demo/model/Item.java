package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Column(nullable = false)
    private String itemName;

    @Min(value = 1, message = "Item cost must be greater than zero")
    @Column(nullable = false)
    private Integer itemCost;

    @NotNull(message = "Item quantity is required")
    @Column(nullable = false)
    private Integer itemQuantity;

    @Pattern(regexp = "Y|N", message = "Item pack must be Y or N")
    @Column(nullable = false)
    private String itemPack;

    private Integer itemContent;

    @NotNull(message = "Item dimensions are required")
    @Column(nullable = false)
    private Integer itemDimensions;

    @NotBlank(message = "Item origin location is required")
    @Column(nullable = false)
    private String itemOriginLocation;

    @NotNull(message = "Item ship is required")
    @Column(nullable = false)
    private Boolean itemShip;

    @NotBlank(message = "Item company is required")
    @Column(nullable = false)
    private String itemCompany;

    @NotNull(message = "Manufacturing date & time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime itemManufacturingDateTime;

    @NotNull(message = "Expiry date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate itemExpiryDate;

    // ✅ Validation method
    @AssertTrue(message = "Item content must be provided if itemPack is Y")
    public boolean isItemContentValid() {
        return !"Y".equals(itemPack) || itemContent != null;
    }

    // ✅ No-arg constructor
    public Item() {}

    // ✅ All-arg constructor
    public Item(Long id, String itemName, @Min(value = 1, message = "Item cost must be greater than zero") int itemCost, Integer itemQuantity, String itemPack,
                Integer itemContent, Integer itemDimensions, String itemOriginLocation,
                Boolean itemShip, String itemCompany, LocalDateTime itemManufacturingDateTime,
                LocalDate itemExpiryDate) {
        this.id = id;
        this.itemName = itemName;
        this.itemCost = itemCost;
        this.itemQuantity = itemQuantity;
        this.itemPack = itemPack;
        this.itemContent = itemContent;
        this.itemDimensions = itemDimensions;
        this.itemOriginLocation = itemOriginLocation;
        this.itemShip = itemShip;
        this.itemCompany = itemCompany;
        this.itemManufacturingDateTime = itemManufacturingDateTime;
        this.itemExpiryDate = itemExpiryDate;
    }

    // ✅ Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getItemCost() {
        return itemCost;
    }

    public void setItemCost(Integer itemCost) {
        this.itemCost = itemCost;
    }

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