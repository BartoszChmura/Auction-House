package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.ItemDto;
import com.auctionsystem.auctionhouse.entities.Bid;
import com.auctionsystem.auctionhouse.entities.Item;
import com.auctionsystem.auctionhouse.entities.Payment;
import com.auctionsystem.auctionhouse.mappers.ItemMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @Mock
    ItemService itemService;

    @Mock
    ItemMapper itemMapper;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitiatePayment() {
        //TODO
    }

    @Test
    public void testFinishPayment() {
        // Given
        Payment payment = new Payment();
        Bid bid = new Bid();
        Item item = new Item();
        item.setId(1L);
        bid.setItem(item);
        payment.setBid(bid);

        ItemDto itemDto = new ItemDto();
        itemDto.setStatus("sprzedano");

        given(itemService.getItemEntityById(anyLong())).willReturn(Optional.of(item));
        given(itemMapper.toDto(any(Item.class))).willReturn(itemDto);

        // When
        paymentService.finishPayment(payment);

        // Then
        ArgumentCaptor<ItemDto> itemDtoCaptor = ArgumentCaptor.forClass(ItemDto.class);
        verify(itemService, times(1)).updateItem(itemDtoCaptor.capture());
        ItemDto updatedItemDto = itemDtoCaptor.getValue();
        assertEquals("sprzedano", updatedItemDto.getStatus());
        verify(itemMapper).toDto(any(Item.class));
    }



    public Payment createPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(100.0);
        payment.setPaymentStatus("CREATED");
        payment.setTransactionId("123456");
        return payment;
    }


}
