package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.ItemDto;
import com.auctionsystem.auctionhouse.services.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/item")
@Tag(name = "Item", description = "Endpoints for managing items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/save")
    @Operation(summary = "Add a new item", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> saveItem(@RequestBody ItemDto itemDto) {
        ItemDto savedItem = itemService.saveItem(itemDto);
        return ResponseEntity.ok(savedItem);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all items", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ItemDto>> getAllItems() {
        List<ItemDto> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an item by id", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        Optional<ItemDto> itemDto = itemService.getItemById(id);
        if (itemDto.isPresent()) {
            return ResponseEntity.ok(itemDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item with id " + id + " does not exist");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an item", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto) {
        Optional<ItemDto> existingItem = itemService.getItemById(id);
        if (existingItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item with id " + id + " does not exist");
        }
        if (!itemService.isUserAuthorizedToUpdateItem(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to update someone else's item");
        }
        itemDto.setId(id);
        ItemDto updatedItem = itemService.updateItem(itemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an item", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        Optional<ItemDto> existingItem = itemService.getItemById(id);
        if (existingItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item with id " + id + " does not exist");
        }
        if (!itemService.isUserAuthorizedToUpdateItem(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete someone else's item");
        }
        itemService.deleteItem(id);
        return ResponseEntity.ok("Item with id " + id + " has been deleted");
    }
}
