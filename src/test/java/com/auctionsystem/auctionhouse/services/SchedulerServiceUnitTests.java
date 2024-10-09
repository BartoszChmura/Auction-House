package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.entities.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class SchedulerServiceUnitTests {

    @Mock
    ItemService itemService;
    @InjectMocks
    SchedulerService schedulerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckItemStatuses() {
        Item activeItem = new Item();
        activeItem.setId(1L);
        activeItem.setStatus("active");
        activeItem.setEndTime(LocalDateTime.now().minusMinutes(1));

        Item inactiveItem = new Item();
        inactiveItem.setId(2L);
        inactiveItem.setStatus("not sold");
        inactiveItem.setEndTime(LocalDateTime.now().minusMinutes(1));

        List<Item> items = Arrays.asList(activeItem, inactiveItem);

        when(itemService.getAllItemsEntity()).thenReturn(items);

        schedulerService.checkItemStatuses();

        verify(itemService, times(1)).endAuction(activeItem.getId());
        verify(itemService, times(0)).endAuction(inactiveItem.getId());
    }
}