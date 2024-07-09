package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.ItemDto;
import com.auctionsystem.auctionhouse.entities.Category;
import com.auctionsystem.auctionhouse.entities.Item;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.mappers.ItemMapper;
import com.auctionsystem.auctionhouse.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemServiceUnitTests {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveItem() {
        // Given
        ItemDto itemDto = createItemDto(1L);

        Item item = createItem(1L);

        User user = new User();
        user.setUsername("testuser");

        Category category = new Category();
        category.setId(1L);

        when(userService.getUserEntityByUsername("testuser")).thenReturn(Optional.of(user));
        when(categoryService.getCategoryEntityById(1L)).thenReturn(Optional.of(category));

        Authentication authentication = new TestingAuthenticationToken("testuser", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toEntity(any(ItemDto.class))).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        // When
        ItemDto result = itemService.saveItem(itemDto);

        // Then
        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getTitle(), result.getTitle());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getStartPrice(), result.getStartPrice());
        assertEquals(itemDto.getEndTime(), result.getEndTime());
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemMapper, times(1)).toDto(item);
    }

    @Test
    public void testGetItemByID() {
        // Given
        ItemDto itemDto = createItemDto(1L);

        Item item = createItem(1L);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        // When
        Optional<ItemDto> result = itemService.getItemById(item.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(itemDto.getId(), result.get().getId());
        assertEquals(itemDto.getTitle(), result.get().getTitle());
        assertEquals(itemDto.getDescription(), result.get().getDescription());
        assertEquals(itemDto.getStartPrice(), result.get().getStartPrice());
        assertEquals(itemDto.getEndTime(), result.get().getEndTime());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemMapper, times(1)).toDto(item);
    }

    @Test
    public void testGetAllItems() {
        // Given
        Item item1 = createItem(1L);
        Item item2 = createItem(2L);
        List<Item> itemList = Arrays.asList(item1, item2);

        ItemDto itemDto1 = createItemDto(1L);
        ItemDto itemDto2 = createItemDto(2L);
        List<ItemDto> itemDtoList = Arrays.asList(itemDto1, itemDto2);

        when(itemRepository.findAll()).thenReturn(itemList);
        when(itemMapper.toDto(item1)).thenReturn(itemDto1);
        when(itemMapper.toDto(item2)).thenReturn(itemDto2);

        // When
        List<ItemDto> result = itemService.getAllItems();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(itemDto1));
        assertTrue(result.contains(itemDto2));
        verify(itemRepository, times(1)).findAll();
        verify(itemMapper, times(1)).toDto(item1);
        verify(itemMapper, times(1)).toDto(item2);
    }

    @Test
    public void testUpdateItem() {
        // Given
        ItemDto itemDto = createItemDto(1L);
        itemDto.setTitle("updatedTitle");
        itemDto.setDescription("updatedDescription");
        itemDto.setEndTime(LocalDateTime.now().plusDays(2));

        Item existingItem = createItem(1L);

        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);
        when(itemMapper.toDto(existingItem)).thenReturn(itemDto);

        // When
        ItemDto result = itemService.updateItem(itemDto);

        // Then
        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getTitle(), result.getTitle());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getEndTime(), result.getEndTime());
        verify(itemRepository, times(1)).findById(itemDto.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemMapper, times(1)).toDto(existingItem);
    }

    @Test
    public void testDeleteItem() {
        // Given
        Item item = createItem(1L);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(item.getId());

        // When
        itemService.deleteItem(item.getId());

        // Then
        verify(itemRepository, times(1)).deleteById(item.getId());
    }

    public ItemDto createItemDto(Long id) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setTitle("testItem");
        itemDto.setDescription("testDescription");
        itemDto.setStartPrice(100.0);
        itemDto.setEndTime(LocalDateTime.now().plusDays(1));
        itemDto.setCategoryId(1L);
        return itemDto;
    }

    public Item createItem(Long id) {
        Item item = new Item();
        item.setId(id);
        item.setTitle("testItem");
        item.setDescription("testDescription");
        item.setStartPrice(100.0);
        item.setEndTime(LocalDateTime.now().plusDays(1));
        return item;
    }

}
