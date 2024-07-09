package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.BidDto;
import com.auctionsystem.auctionhouse.dtos.ItemDto;
import com.auctionsystem.auctionhouse.entities.Bid;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.mappers.BidMapper;
import com.auctionsystem.auctionhouse.repositories.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BidServiceUnitTests {

    @Mock
    BidMapper bidMapper;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BidService bidService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveBid() {
        // Given
        BidDto bidDto = createBidDto(1L,1L,1L);
        Bid bid = createBid(1L,1L,1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setCurrentPrice(50.0);
        itemDto.setStatus("aktywna");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Authentication authentication = new TestingAuthenticationToken("testuser", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(itemService.getItemById(anyLong())).thenReturn(Optional.of(itemDto));
        when(userService.getUserEntityByUsername(anyString())).thenReturn(Optional.of(user));
        when(bidRepository.save(any(Bid.class))).thenReturn(bid);
        when(bidMapper.toEntity(bidDto)).thenReturn(bid);
        when(bidMapper.toDto(bid)).thenReturn(bidDto);

        // When
        BidDto result = bidService.saveBid(bidDto);

        // Then
        assertNotNull(result);
        assertEquals(bidDto.getBidAmount(), result.getBidAmount());
        verify(itemService, times(1)).updateCurrentPrice(any(ItemDto.class));
        verify(bidRepository, times(1)).save(any(Bid.class));

    }

    @Test
    public void testGetBidById() {
        // Given
        Bid bid = createBid(1L, 1L, 1L);
        BidDto expectedBidDto = createBidDto(1L, 1L, 1L);

        when(bidRepository.findById(bid.getId())).thenReturn(Optional.of(bid));
        when(bidMapper.toDto(bid)).thenReturn(expectedBidDto);

        // When
        Optional<BidDto> result = bidService.getBidById(bid.getId());

        // Then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(expectedBidDto, result.get());
        verify(bidRepository, times(1)).findById(bid.getId());
        verify(bidMapper, times(1)).toDto(bid);
    }

    @Test
    public void testGetAllBids() {
        // Given
        Bid bid1 = createBid(1L, 1L, 1L);
        Bid bid2 = createBid(2L, 2L, 2L);
        List<Bid> bidList = Arrays.asList(bid1, bid2);

        BidDto bidDto1 = createBidDto(1L, 1L, 1L);
        BidDto bidDto2 = createBidDto(2L, 2L, 2L);
        List<BidDto> bidDtoList = Arrays.asList(bidDto1, bidDto2);

        when(bidRepository.findAll()).thenReturn(bidList);
        when(bidMapper.toDto(bid1)).thenReturn(bidDto1);
        when(bidMapper.toDto(bid2)).thenReturn(bidDto2);

        // When
        List<BidDto> result = bidService.getAllBids();

        // Then
        assertNotNull(result);
        assertEquals(bidDtoList.size(), result.size());
        assertTrue(result.containsAll(bidDtoList));
        verify(bidRepository, times(1)).findAll();
        verify(bidMapper, times(1)).toDto(bid1);
        verify(bidMapper, times(1)).toDto(bid2);
    }

    @Test
    public void testDeleteBid() {
        // Given
        BidDto bidDto = createBidDto(1L, 1L, 1L);

        doNothing().when(bidRepository).deleteById(bidDto.getId());

        // When
        bidService.deleteBid(bidDto.getId());

        // Then
        verify(bidRepository, times(1)).deleteById(bidDto.getId());
    }

    public BidDto createBidDto(Long id, Long itemId, Long bidderId) {
        BidDto bidDto = new BidDto();
        bidDto.setId(id);
        bidDto.setBidAmount(100.0);
        bidDto.setItemId(itemId);
        bidDto.setBidderId(bidderId);
        return bidDto;
    }

    public Bid createBid(Long id, Long itemId, Long bidderId) {
        Bid bid = new Bid();
        bid.setId(id);
        bid.setBidAmount(100.0);
        return bid;
    }
}
