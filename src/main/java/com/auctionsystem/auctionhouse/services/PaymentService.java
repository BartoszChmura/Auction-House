package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.*;
import com.auctionsystem.auctionhouse.entities.Bid;
import com.auctionsystem.auctionhouse.entities.Item;
import com.auctionsystem.auctionhouse.entities.Payment;
import com.auctionsystem.auctionhouse.mappers.ItemMapper;
import com.auctionsystem.auctionhouse.repositories.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.Optional;


@Slf4j
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BidService bidService;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, BidService bidService, ItemService itemService, ItemMapper itemMapper) {
        this.paymentRepository = paymentRepository;
        this.bidService = bidService;
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    private final String clientId = "478097";
    private final String clientSecret = "bd1fe53f63f17029cef465dafb0534c7";
    private final String continueUrl = "https://ray-wired-mallard.ngrok-free.app/items";
    private final String notifyUrl = "https://ray-wired-mallard.ngrok-free.app/payment/notify";
    private final String paymentsUrl = "https://secure.snd.payu.com/api/v2_1/orders";
    private final String tokenUrl = "https://secure.snd.payu.com/pl/standard/user/oauth/authorize";
    private final ObjectMapper objectMapper = new ObjectMapper();


    public OAuthTokenResponse createOAuthToken() {
        log.info("Creating OAuth token");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        try {
            OAuthTokenResponse tokenResponse = objectMapper.readValue(response.getBody(), OAuthTokenResponse.class);
            log.info("OAuth token created successfully");
            return tokenResponse;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get token", e);
        }
    }

    public PaymentResponse initiatePayment(PaymentRequest paymentRequest, Long winningBidId) {
        log.info("Initiating payment for winning bid with id: {}", winningBidId);
        try {
            Optional<Bid> winningBid = bidService.getBidEntityById(winningBidId);
            if (winningBid.isEmpty()) {
                throw new EntityNotFoundException("Winning bid not found");
            }

            Item item = winningBid.get().getItem();
            if (!item.getStatus().equals("awaiting payment")) {
                throw new IllegalArgumentException("Auction has not yet ended");
            }

            PaymentRequest.Product product = new PaymentRequest.Product();
            product.setName(winningBid.get().getItem().getTitle());
            double bidAmountInZlotys = winningBid.get().getBidAmount();
            int bidAmountInPennies = (int) Math.round(bidAmountInZlotys * 100);
            product.setUnitPrice(bidAmountInPennies);
            product.setQuantity(1);
            product.setVirtual(true);

            paymentRequest.setProducts(Collections.singletonList(product));
            paymentRequest.setContinueUrl(continueUrl);
            paymentRequest.setNotifyUrl(notifyUrl);
            paymentRequest.setCustomerIp("192.168.1.1");
            paymentRequest.setMerchantPosId("478097");
            paymentRequest.setCurrencyCode("PLN");
            paymentRequest.setTotalAmount(String.valueOf(bidAmountInPennies));

            OAuthTokenResponse tokenResponse = createOAuthToken();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(tokenResponse.getAccessToken());
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);
            ResponseEntity<PaymentResponse> response = restTemplate.postForEntity(paymentsUrl, request, PaymentResponse.class);

            Payment payment = new Payment();
            payment.setBid(winningBid.get());
            payment.setAmount(winningBid.get().getBidAmount());
            payment.setPaymentStatus("CREATED");
            payment.setTransactionId(response.getBody().getOrderId());

            paymentRepository.save(payment);

            log.info("Payment initiated successfully for winning bid with id: {}", winningBidId);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate payment", e);
        }
    }

    public void updatePaymentStatus(PaymentNotification notification) {
        log.info("Updating payment status for transaction with id: {}", notification.getOrder().getOrderId());
        Payment existingPayment = paymentRepository.findByTransactionId(notification.getOrder().getOrderId());
        if (existingPayment == null) {
            throw new EntityNotFoundException("Payment not found with transaction id " + notification.getOrder().getOrderId());
        }
        existingPayment.setPaymentStatus(notification.getOrder().getStatus());
        if (existingPayment.getPaymentStatus().equals("COMPLETED")) {
            finishPayment(existingPayment);
        }
        paymentRepository.save(existingPayment);
        log.info("Payment status updated successfully for transaction with id: {}", notification.getOrder().getOrderId());
    }

    public void finishPayment(Payment payment) {
        log.info("Finalizing payment for item with id: {}", payment.getBid().getItem().getId());
        Optional<Item> existingItem = itemService.getItemEntityById(payment.getBid().getItem().getId());
        if (existingItem.isEmpty()) {
            throw new EntityNotFoundException("Item not found");
        }
        existingItem.get().setStatus("sold");
        itemService.updateItem(itemMapper.toDto(existingItem.get()));
        log.info("Payment finalized successfully for item with id: {}", payment.getBid().getItem().getId());
    }
}