package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.entity.Item;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerService {

    private final ItemService itemService;

    public SchedulerService(ItemService itemService) {
        this.itemService = itemService;
    }

    @Scheduled(fixedRate = 60000)
    public void checkItemStatuses() {
        System.out.println("Sprawdzanie statusów przedmiotów...");
        List<Item> items = itemService.getAllItemsEntity();
        for (Item item : items) {
            if (item.getEndTime().isBefore(java.time.LocalDateTime.now()) && item.getStatus().equals("aktywna")) {
                itemService.endAuction(item.getId());
            }
        }
    }
}
