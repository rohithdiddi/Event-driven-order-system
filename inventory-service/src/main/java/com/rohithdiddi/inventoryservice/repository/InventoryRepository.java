package com.rohithdiddi.inventoryservice.repository;

import com.rohithdiddi.inventoryservice.model.InventoryItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InventoryRepository extends MongoRepository<InventoryItem, String> {
    Optional<InventoryItem> findByProductSku(String productSku);
}
