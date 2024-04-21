package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.dto.BidDto;
import com.auctionsystem.auctionhouse.dto.CategoryDto;
import com.auctionsystem.auctionhouse.dto.ItemDto;
import com.auctionsystem.auctionhouse.dto.UserDto;
import com.auctionsystem.auctionhouse.entity.Category;
import com.auctionsystem.auctionhouse.entity.Item;
import com.auctionsystem.auctionhouse.entity.User;
import com.auctionsystem.auctionhouse.mapper.CategoryMapper;
import com.auctionsystem.auctionhouse.mapper.ItemMapper;
import com.auctionsystem.auctionhouse.mapper.UserMapper;
import com.auctionsystem.auctionhouse.repository.ItemRepository;
import com.auctionsystem.auctionhouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    private final BidService bidService;
    private final CategoryService categoryService;

    @Autowired
    public ItemService(ItemRepository itemRepository, ItemMapper itemMapper, UserService userService, BidService bidService, CategoryService categoryService) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.userService = userService;
        this.bidService = bidService;
        this.categoryService = categoryService;

    }

    @Transactional
    public ItemDto saveItem(ItemDto itemDto) {
        if (itemDto.getTitle() == null || itemDto.getTitle().isEmpty() || itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Title and description cannot be null or empty.");
        }
        if (itemDto.getEndTime() == null || itemDto.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("End time must be in the future.");
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
    public ItemDto updateItem(ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono przedmiotu z takim ID"));

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
        return itemMapper.toDto(updatedItem);


    }

    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public boolean isUserAuthorizedToUpdateItem(Long id) {

        Optional<ItemDto> itemDto = getItemById(id);
        if (itemDto.isEmpty()) {
            throw new IllegalArgumentException("Item not found");
        }

        Long sellerId = itemDto.get().getSellerId();

        return userService.isUserAuthorizedToUpdate(sellerId);

    }

    @Transactional
    public void endAuction(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Przedmiot o id " + id + " nie istnieje"));
        if(!item.getStatus().equals("active")) {
            throw new IllegalArgumentException("Aukcja jest już zakończona");
        }
        Optional<BidDto> winnerBid = bidService.getWinnerBidByItemId(id);
        if (winnerBid.isEmpty()) {
            item.setStatus("expired");
        }
        else {
            item.setWinner(userService.getUserEntityById(winnerBid.get().getBidderId()).orElseThrow(() -> new IllegalArgumentException("Użytkownik nie istnieje")));
            item.setStatus("waiting for payment");
        }
    }
}
