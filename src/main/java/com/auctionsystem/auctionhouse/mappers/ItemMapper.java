package com.auctionsystem.auctionhouse.mappers;

import com.auctionsystem.auctionhouse.dtos.ItemDto;
import com.auctionsystem.auctionhouse.entities.Category;
import com.auctionsystem.auctionhouse.entities.Item;
import com.auctionsystem.auctionhouse.entities.User;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setSellerId(item.getSeller().getId());
        itemDto.setCategoryId(item.getCategory().getId());
        if (item.getWinner() != null) {
            itemDto.setWinnerId(item.getWinner().getId());
        }
        itemDto.setTitle(item.getTitle());
        itemDto.setDescription(item.getDescription());
        itemDto.setStartPrice(item.getStartPrice());
        itemDto.setCurrentPrice(item.getCurrentPrice());
        itemDto.setStartTime(item.getStartTime());
        itemDto.setEndTime(item.getEndTime());
        itemDto.setCreatedAt(item.getCreatedAt());
        itemDto.setUpdatedAt(item.getUpdatedAt());
        itemDto.setStatus(item.getStatus());
        return itemDto;
    }

    public Item toEntity(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        User seller = new User();
        seller.setId(itemDto.getSellerId());
        item.setSeller(seller);
        Category category = new Category();
        category.setId(itemDto.getCategoryId());
        item.setCategory(category);
        if (itemDto.getWinnerId() != null) {
            User winner = new User();
            winner.setId(itemDto.getWinnerId());
            item.setWinner(winner);
        }
        item.setTitle(itemDto.getTitle());
        item.setDescription(itemDto.getDescription());
        item.setStartPrice(itemDto.getStartPrice());
        item.setCurrentPrice(itemDto.getCurrentPrice());
        item.setStartTime(itemDto.getStartTime());
        item.setEndTime(itemDto.getEndTime());
        item.setCreatedAt(itemDto.getCreatedAt());
        item.setUpdatedAt(itemDto.getUpdatedAt());
        item.setStatus(itemDto.getStatus());
        return item;
    }
}