package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.PaymentNotification;
import com.auctionsystem.auctionhouse.dtos.PaymentRequest;
import com.auctionsystem.auctionhouse.entities.*;
import com.auctionsystem.auctionhouse.repositories.*;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class PaymentControllerIntegrationTests {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private String jwtToken;

    @BeforeEach
    public void setup() {
        userRepository.save(createUser(1L, "testuser1"));
        userRepository.save(createUser(2L, "testuser2"));

        categoryRepository.save(createCategory(1L, "Electronics"));

        itemRepository.save(createItem(1L, "testItem", "test", 2L));

        bidRepository.save(createBid(1L, 200.0));

        // Granting authentication to testuser1
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("testuser1");
        jwtToken = jwtService.generateToken(userDetails);
    }


    @Test
    public void testPayForItem_OK() throws Exception {
        PaymentRequest paymentRequest = createPaymentRequest();

        mockMvc.perform(post("/payment/pay/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPaymentNotification_OK() throws Exception {
        PaymentNotification paymentNotification = createPaymentNotification();
        paymentRepository.save(createPayment(1L));

        mockMvc.perform(post("/payment/notify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentNotification)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPaymentNotification_InternalServerError() throws Exception {
        PaymentNotification paymentNotification = createPaymentNotification();

        mockMvc.perform(post("/payment/notify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentNotification)))
                .andExpect(status().isInternalServerError());
    }

    public Bid createBid(Long id, double bidAmount) {
        Bid bid = new Bid();
        bid.setId(id);
        bid.setBidAmount(bidAmount);
        bid.setItem(itemRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("Item with id 1 does not exist")));
        bid.setBidder(userRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("User with id 1 does not exist")));
        return bid;
    }

    public PaymentRequest createPaymentRequest() {
        PaymentRequest paymentRequest = new PaymentRequest();

        paymentRequest.setContinueUrl("http://example.com/continue");
        paymentRequest.setNotifyUrl("http://example.com/notify");
        paymentRequest.setCustomerIp("192.168.1.1");
        paymentRequest.setMerchantPosId("123456");
        paymentRequest.setDescription("Payment");
        paymentRequest.setCurrencyCode("USD");
        paymentRequest.setTotalAmount("100.00");

        PaymentRequest.Product product = new PaymentRequest.Product();
        product.setName("Item");
        product.setUnitPrice(100);
        product.setQuantity(1);
        product.setVirtual(false);

        List<PaymentRequest.Product> products = new ArrayList<>();
        products.add(product);

        paymentRequest.setProducts(products);

        PaymentRequest.Buyer buyer = new PaymentRequest.Buyer();
        buyer.setEmail("buyer@example.com");
        buyer.setPhone("123-456-7890");
        buyer.setFirstName("John");
        buyer.setLastName("Doe");

        paymentRequest.setBuyer(buyer);
        return paymentRequest;
    }

    public PaymentNotification createPaymentNotification() {
        PaymentNotification paymentNotification = new PaymentNotification();

        PaymentNotification.Order order = new PaymentNotification.Order();
        order.setOrderId("1");
        order.setStatus("COMPLETED");

        paymentNotification.setOrder(order);

        return paymentNotification;
    }

    public Item createItem(Long id, String title, String description, Long sellerId) {
        Item item = new Item();
        item.setId(id);
        item.setTitle(title);
        item.setDescription(description);
        item.setStartPrice(100.0);
        item.setCurrentPrice(100.0);
        item.setEndTime(LocalDateTime.now().plusDays(1));
        item.setStatus("awaiting payment");
        item.setWinner(userRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("User with id 1 does not exist")));
        item.setCategory(categoryRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("Category with id 1 does not exist")));
        item.setSeller(userRepository.findById(sellerId).orElseThrow(() -> new NoSuchElementException("User with id " + sellerId + " does not exist")));
        return item;
    }

    public User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPasswordHash("password");
        user.setEmail("testuser@mail.com");
        return user;
    }

    public Payment createPayment(Long id) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setBid(bidRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("Bid with id 1 does not exist")));
        payment.setAmount(200.0);
        payment.setPaymentStatus("CREATED");
        payment.setTransactionId("1");
        return payment;
    }

    public Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setCategoryName(name);
        return category;
    }
}