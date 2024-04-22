package com.auctionsystem.auctionhouse.controller;

import com.auctionsystem.auctionhouse.dto.ItemDto;
import com.auctionsystem.auctionhouse.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;



    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestBody ItemDto itemDto) {
            ItemDto savedItem = itemService.saveItem(itemDto);
            return ResponseEntity.ok(savedItem);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemDto>> getAllItems() {
        List<ItemDto> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        Optional<ItemDto> itemDto = itemService.getItemById(id);
        if (itemDto.isPresent()) {
            return ResponseEntity.ok(itemDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Przedmiot o id " + id + " nie istnieje");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto) {
        if (!itemService.isUserAuthorizedToUpdateItem(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nie masz uprawnień do aktualizacji czyjegoś przedmiotu");
        }
        itemDto.setId(id);
        ItemDto updatedItem = itemService.updateItem(itemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        Optional<ItemDto> existingItem = itemService.getItemById(id);
        if (existingItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Przedmiot o id " + id + " nie istnieje");
        }
        if (!itemService.isUserAuthorizedToUpdateItem(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nie masz uprawnień do usunięcia czyjegoś przedmiotu");
        }
        itemService.deleteItem(id);
        return ResponseEntity.ok("Przedmiot o id " + id + " został usunięty");
    }

}
