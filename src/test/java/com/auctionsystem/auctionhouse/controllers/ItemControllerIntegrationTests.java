package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.ItemDto;
import com.auctionsystem.auctionhouse.entities.Category;
import com.auctionsystem.auctionhouse.entities.Item;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.repositories.CategoryRepository;
import com.auctionsystem.auctionhouse.repositories.ItemRepository;
import com.auctionsystem.auctionhouse.repositories.UserRepository;
import com.auctionsystem.auctionhouse.services.JwtService;
import com.auctionsystem.auctionhouse.services.JwtUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;


import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class ItemControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private String jwtToken;

    @BeforeEach
    public void setup() {
        userRepository.save(createUser(1L, "testuser1"));
        userRepository.save(createUser(2L, "testuser2"));

        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("testCategory");
        categoryRepository.save(category);


        itemRepository.save(createItem(1L, "testItem", "testDescription", 1L));
        itemRepository.save(createItem(2L, "testItem2", "testDescription2", 1L));
        itemRepository.save(createItem(3L, "testItem3", "testDescription3", 2L));

        // Granting authentication to testuser1
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("testuser1");
        jwtToken = jwtService.generateToken(userDetails);
    }

    @Test
    public void testSaveItem_OK() throws Exception {
        ItemDto itemDto = createItemDto(4L, 1L, "testItem", "testDescription");

        mockMvc.perform(post("/item/save")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("testItem"))
                .andExpect(jsonPath("$.description").value("testDescription"))
                .andExpect(jsonPath("$.startPrice").value(100.0))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.sellerId").value(1));
    }

    @Test
    public void testGetAllItems_OK() throws Exception {
        mockMvc.perform(get("/item/all")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]id").value(1))
                .andExpect(jsonPath("$[1]id").value(2))
                .andExpect(jsonPath("$[2]id").value(3));
    }

    @Test
    public void testGetItemById_OK() throws Exception {
        mockMvc.perform(get("/item/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("testItem"))
                .andExpect(jsonPath("$.description").value("testDescription"))
                .andExpect(jsonPath("$.startPrice").value(100.0))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.sellerId").value(1));
    }

    @Test
    public void testGetItemById_NotFound() throws Exception {
        mockMvc.perform(get("/item/10")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateItem_OK() throws Exception {
        ItemDto itemDto = createItemDto(1L, 1L, "updatedTitle", "updatedDescription");

        mockMvc.perform(put("/item/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("updatedTitle"))
                .andExpect(jsonPath("$.description").value("updatedDescription"))
                .andExpect(jsonPath("$.startPrice").value(100.0))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.sellerId").value(1));
    }

    @Test
    public void testUpdateItem_Unauthorized() throws Exception {
        ItemDto itemDto = createItemDto(3L, 2L, "updatedTitle", "updatedDescription");

        mockMvc.perform(put("/item/3")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateItem_NotFound() throws Exception {
        ItemDto itemDto = createItemDto(10L, 1L, "updatedTitle", "updatedDescription");

        mockMvc.perform(put("/item/10")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteItem_OK() throws Exception {
        mockMvc.perform(delete("/item/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteItem_Unauthorized() throws Exception {
        mockMvc.perform(delete("/item/3")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteItem_NotFound() throws Exception {
        mockMvc.perform(delete("/item/10")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }


    public ItemDto createItemDto(Long id, Long sellerId, String title, String description) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setTitle(title);
        itemDto.setDescription(description);
        itemDto.setStartPrice(100.0);
        itemDto.setEndTime(LocalDateTime.now().plusDays(1));
        itemDto.setCategoryId(1L);
        itemDto.setSellerId(sellerId);
        return itemDto;
    }

    public Item createItem(Long id, String title, String description, Long sellerId) {
        Item item = new Item();
        item.setId(id);
        item.setTitle(title);
        item.setDescription(description);
        item.setStartPrice(100.0);
        item.setEndTime(LocalDateTime.now().plusDays(1));
        item.setCategory(categoryRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("Nie kategori użytkownika o id 1")));
        item.setSeller(userRepository.findById(sellerId).orElseThrow(() -> new NoSuchElementException("Nie znaleziono użytkownika o id " + sellerId)));
        return item;
    }

    public User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setEmail("testuser@mail.com");
        return user;
    }
}
