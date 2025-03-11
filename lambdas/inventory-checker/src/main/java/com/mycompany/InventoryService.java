package com.mycompany.lambdas.inventorychecker.service;

import com.mycompany.lambdas.inventorychecker.model.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    public Inventory processInventory(Inventory inventory) {
        logger.info("Processing inventory for product: {}", inventory.getProductId());

        // Validate the inventory
        validateInventory(inventory);

        // Check if reorder is needed
        boolean reorderNeeded = checkIfReorderNeeded(inventory);

        // Set metadata
        inventory.getMetadata().setSource("inventory-checker");
        inventory.getMetadata().setEventType(reorderNeeded ? "INVENTORY_REORDER_NEEDED" : "INVENTORY_UPDATED");

        logger.info("Inventory processed successfully for product: {}", inventory.getProductId());
        return inventory;
    }

    private void validateInventory(Inventory inventory) {
        if (inventory.getProductId() == null || inventory.getProductId().isEmpty()) {
            throw new IllegalArgumentException("Inventory must have a product ID");
        }

        if (inventory.getWarehouseId() == null || inventory.getWarehouseId().isEmpty()) {
            throw new IllegalArgumentException("Inventory must have a warehouse ID");
        }

        if (inventory.getQuantityAvailable() < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative");
        }

        if (inventory.getQuantityReserved() < 0) {
            throw new IllegalArgumentException("Reserved quantity cannot be negative");
        }

        // Additional validation logic can be added here
    }

    private boolean checkIfReorderNeeded(Inventory inventory) {
        // Simple reorder logic - can be made more sophisticated
        return inventory.getQuantityAvailable() < 10;
    }
}