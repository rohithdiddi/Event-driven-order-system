package com.rohithdiddi.inventoryservice;

import com.rohithdiddi.inventoryservice.model.InventoryItem;
import com.rohithdiddi.inventoryservice.repository.InventoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @PostMapping
    public ResponseEntity<InventoryItem> seed(@RequestBody InventoryItem item) {
        return ResponseEntity.ok(inventoryRepository.save(item));
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> listAll() {
        return ResponseEntity.ok(inventoryRepository.findAll());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("inventory-service is up");
    }
}
