package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.BidDto;
import com.auctionsystem.auctionhouse.dtos.ItemDto;
import com.auctionsystem.auctionhouse.entities.Category;
import com.auctionsystem.auctionhouse.entities.Item;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.mappers.ItemMapper;
import com.auctionsystem.auctionhouse.repositories.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final BidService bidService;
    private final CategoryService categoryService;

    @Autowired
    public ItemService(ItemRepository itemRepository, ItemMapper itemMapper, UserService userService, @Lazy BidService bidService, CategoryService categoryService) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.userService = userService;
        this.bidService = bidService;
        this.categoryService = categoryService;

    }

    @Transactional
    public ItemDto saveItem(ItemDto itemDto) {
        log.info("Saving item with id {}", itemDto.getId());
        if (itemDto.getTitle() == null || itemDto.getTitle().isEmpty() || itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Title and description cannot be null or empty");
        }
        if (itemDto.getEndTime() == null || itemDto.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("End time must be in the future");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User seller = userService.getUserEntityByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Category category = categoryService.getCategoryEntityById(itemDto.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Item item = itemMapper.toEntity(itemDto);
        item.setSeller(seller);
        item.setWinner(null);
        item.setCategory(category);
        item.setCurrentPrice(itemDto.getStartPrice());
        item.setStatus("active");

        Item savedItem = itemRepository.save(item);
        log.info("Saved item with id {}", savedItem.getId());

        return itemMapper.toDto(savedItem);
    }

    @Transactional
    public List<ItemDto> getAllItems() {
        log.info("Retrieving all items");
        List<Item> items = itemRepository.findAll();
        List<ItemDto> result = items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        log.info("Retrieved all items");

        return result;
    }

    @Transactional
    public List<Item> getAllItemsEntity() {
        log.info("Retrieving all item entities");
        List<Item> result = itemRepository.findAll();
        log.info("Retrieved all item entities");

        return result;
    }

    @Transactional
    public Optional<ItemDto> getItemById(Long id) {
        log.info("Retrieving item with id: {}", id);
        Optional<ItemDto> result = itemRepository.findById(id)
                .map(itemMapper::toDto);
        log.info("Retrieved item with id: {}", id);

        return result;
    }

    @Transactional
    public Optional<Item> getItemEntityById(Long id) {
        log.info("Retrieving item entity with id: {}", id);
        Optional<Item> result = itemRepository.findById(id);
        log.info("Retrieved item entity with id: {}", id);

        return result;
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDto) {
        log.info("Updating item: {}", itemDto);
        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Item with such ID not found"));

        if (itemDto.getTitle() != null && !itemDto.getTitle().isEmpty()) {
            existingItem.setTitle(itemDto.getTitle());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getEndTime() != null && itemDto.getEndTime().isAfter(LocalDateTime.now())) {
            existingItem.setEndTime(itemDto.getEndTime());
        }

        Item updatedItem = itemRepository.save(existingItem);
        log.info("Item updated successfully: {}", updatedItem);

        return itemMapper.toDto(updatedItem);
    }

    @Transactional
    public void deleteItem(Long id) {
        log.info("Deleting item with id: {}", id);
        itemRepository.deleteById(id);
        log.info("Item with id {} has been successfully deleted", id);
    }

    public boolean isUserAuthorizedToUpdateItem(Long id) {
        log.info("Checking if user is authorized to update item with id: {}", id);
        Optional<ItemDto> itemDto = getItemById(id);
        if (itemDto.isEmpty()) {
            throw new IllegalArgumentException("Item with id " + id + " does not exist");
        }

        Long sellerId = itemDto.get().getSellerId();
        boolean isAuthorized = userService.isUserAuthorizedToUpdate(sellerId);
        log.info("User {} is {} to update item with id: {}", sellerId, isAuthorized ? "authorized" : "not authorized", id);

        return isAuthorized;
    }

    @Transactional
    public ItemDto updateCurrentPrice(ItemDto itemDto) {
        log.info("Updating current price of item: {}", itemDto);
        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Item with such ID not found"));

        if (itemDto.getCurrentPrice() != null && itemDto.getCurrentPrice() > existingItem.getCurrentPrice()) {
            existingItem.setCurrentPrice(itemDto.getCurrentPrice());
        }

        Item updatedItem = itemRepository.save(existingItem);
        log.info("Current price of item updated successfully: {}", updatedItem);

        return itemMapper.toDto(updatedItem);
    }

    @Transactional
    public void endAuction(Long id) {
        log.info("Ending auction for item with id: {}", id);
        Item item = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item with id " + id + " does not exist"));
        if (!item.getStatus().equals("active")) {
            throw new IllegalArgumentException("Auction is already finished");
        }
        Optional<BidDto> winnerBid = bidService.getWinnerBidByItemId(id);
        if (winnerBid.isEmpty()) {
            item.setStatus("not sold");
        } else {
            item.setWinner(userService.getUserEntityById(winnerBid.get().getBidderId()).orElseThrow(() -> new IllegalArgumentException("User does not exist")));
            item.setStatus("awaiting payment");
        }
        itemRepository.save(item);
        log.info("Auction for item with id {} has been ended", id);
    }
}