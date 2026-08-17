package com.rohithdiddi.inventoryservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventory")
public class InventoryItem {

    @Id
    private String id;

    private String productSku;
    private Integer availableQuantity;

    public InventoryItem() {
    }

    public InventoryItem(String productSku, Integer availableQuantity) {
        this.productSku = productSku;
        this.availableQuantity = availableQuantity;
    }

    public String getId() {
        return id;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}
