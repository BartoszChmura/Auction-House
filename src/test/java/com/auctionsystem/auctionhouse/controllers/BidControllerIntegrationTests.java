package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.BidDto;
import com.auctionsystem.auctionhouse.entities.Bid;
import com.auctionsystem.auctionhouse.entities.Category;
import com.auctionsystem.auctionhouse.entities.Item;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.repositories.BidRepository;
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
public class BidControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        bidRepository.save(createBid(1L, 110.0));

        // Granting authentication to testuser1
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("testuser1");
        jwtToken = jwtService.generateToken(userDetails);
    }

    @Test
    public void testCreateBid_OK() throws Exception {
        BidDto bidDto = createBidDto(2L, 200.0);
        mockMvc.perform(post("/bid")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bidDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.bidAmount").value(200.0))
                .andExpect(jsonPath("$.bidderId").value(1))
                .andExpect(jsonPath("$.itemId").value(1));
    }

    @Test
    public void testGetBidById_OK() throws Exception {
        mockMvc.perform(get("/bid/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bidAmount").value(110.0))
                .andExpect(jsonPath("$.bidderId").value(1))
                .andExpect(jsonPath("$.itemId").value(1));
    }

    @Test
    public void testGetBidById_NotFound() throws Exception {
        mockMvc.perform(get("/bid/100")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllBids_OK() throws Exception {
        mockMvc.perform(get("/bid/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bidAmount").value(110.0))
                .andExpect(jsonPath("$[0].bidderId").value(1))
                .andExpect(jsonPath("$[0].itemId").value(1));
    }

    @Test
    public void testGetBidsByItemId_OK() throws Exception {
        mockMvc.perform(get("/bid/item/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bidAmount").value(110.0))
                .andExpect(jsonPath("$[0].bidderId").value(1))
                .andExpect(jsonPath("$[0].itemId").value(1));
    }

    @Test
    public void testGetWinnerBidByItemId_OK() throws Exception {
        Item item = itemRepository.save(createItem(3L, "testItem3", "testDescription3", 1L));
        item.setWinner(userRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("Bid with id 1 does not exist")));
        mockMvc.perform(get("/bid/winner/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bidAmount").value(110.0))
                .andExpect(jsonPath("$.bidderId").value(1))
                .andExpect(jsonPath("$.itemId").value(1));
    }

    @Test
    public void testGetWinnerByItemId_NotFound() throws Exception {
        mockMvc.perform(get("/bid/winner/100")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteBid_OK() throws Exception {
        mockMvc.perform(delete("/bid/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteBid_NotFound() throws Exception {
        mockMvc.perform(delete("/bid/100")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteBid_NotAuthorized() throws Exception {
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("testuser2");
        String jwtToken2 = jwtService.generateToken(userDetails);
        mockMvc.perform(delete("/bid/1")
                        .header("Authorization", "Bearer " + jwtToken2))
                .andExpect(status().isUnauthorized());
    }


    public BidDto createBidDto(Long id, double bidAmount) {
        BidDto bidDto = new BidDto();
        bidDto.setBidAmount(bidAmount);
        bidDto.setId(id);
        bidDto.setItemId(1L);
        bidDto.setBidderId(1L);
        return bidDto;
    }

    public Bid createBid(Long id, double bidAmount) {
        Bid bid = new Bid();
        bid.setId(id);
        bid.setBidAmount(bidAmount);
        bid.setItem(itemRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("Item with id 1 does not exist")));
        bid.setBidder(userRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("User with id 1 does not exist")));
        return bid;
    }

    public Item createItem(Long id, String title, String description, Long sellerId) {
        Item item = new Item();
        item.setId(id);
        item.setTitle(title);
        item.setDescription(description);
        item.setStartPrice(100.0);
        item.setCurrentPrice(100.0);
        item.setEndTime(LocalDateTime.now().plusDays(1));
        item.setStatus("active");
        item.setCategory(categoryRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("Category with id 1 does not exist")));
        item.setSeller(userRepository.findById(sellerId).orElseThrow(() -> new NoSuchElementException("User with id " + sellerId + " does not exist")));
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