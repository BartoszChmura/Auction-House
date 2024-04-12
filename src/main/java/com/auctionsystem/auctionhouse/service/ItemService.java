package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.dto.ItemDto;
import com.auctionsystem.auctionhouse.entity.Item;
import com.auctionsystem.auctionhouse.mapper.ItemMapper;
import com.auctionsystem.auctionhouse.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemService(ItemRepository itemRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Transactional
    public ItemDto saveItem(Item item) {
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    @Transactional
    public List<ItemDto> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<ItemDto> getItemById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toDto);
    }

    @Transactional
    public ItemDto updateItem(Item item) {
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toDto(updatedItem);
    }

    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
