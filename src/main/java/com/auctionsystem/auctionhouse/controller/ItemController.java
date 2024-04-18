package com.auctionsystem.auctionhouse.controller;

import com.auctionsystem.auctionhouse.dto.ItemDto;
import com.auctionsystem.auctionhouse.dto.UserDto;
import com.auctionsystem.auctionhouse.mapper.ItemMapper;
import com.auctionsystem.auctionhouse.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    private final ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @RequestMapping("/add")
    @PostMapping
    public ResponseEntity<?> addItem(@RequestBody ItemDto itemDto) {
            ItemDto savedItem = itemService.saveItem(itemDto);
            return ResponseEntity.ok(savedItem);
    }

    @RequestMapping("/all")
    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems() {
        List<ItemDto> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }
}
