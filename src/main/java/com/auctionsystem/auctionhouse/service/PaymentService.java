package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.dto.*;
import com.auctionsystem.auctionhouse.entity.Bid;
import com.auctionsystem.auctionhouse.entity.Payment;
import com.auctionsystem.auctionhouse.mapper.PaymentMapper;
import com.auctionsystem.auctionhouse.repository.PaymentRepository;
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
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, BidService bidService, ItemService itemService, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.bidService = bidService;
        this.itemService = itemService;
        this.paymentMapper = paymentMapper;
    }

    private final String clientId = "478097";
    private final String clientSecret = "bd1fe53f63f17029cef465dafb0534c7";
    private final String notifyUrl = "https://ray-wired-mallard.ngrok-free.app/payment/notify";
    private final String paymentsUrl = "https://secure.snd.payu.com/api/v2_1/orders";
    private final String tokenUrl = "https://secure.snd.payu.com/pl/standard/user/oauth/authorize";
    private final ObjectMapper objectMapper = new ObjectMapper();


    public OAuthTokenResponse createOAuthToken() {
        log.info("Tworzenie tokenu");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        log.info("Odpowiedź z serwera: {}", response.getBody());

        try {
            return objectMapper.readValue(response.getBody(), OAuthTokenResponse.class);
        } catch (Exception e) {
            log.error("Nie udało się uzyskać tokenu", e);
            throw new RuntimeException("Nie udało się uzyskać tokenu", e);
        }
    }

    public PaymentResponse initiatePayment(PaymentRequest paymentRequest, Long winningBidId) {
        log.info("inicjowanie płatności");
        try {
            Optional<Bid> winningBid = bidService.getBidEntityById(winningBidId);
            if (winningBid.isEmpty()) {
                throw new EntityNotFoundException("Nie znaleziono wygranej licytacji");
            }

            PaymentRequest.Product product = new PaymentRequest.Product();
            product.setName(winningBid.get().getItem().getTitle());
            double bidAmountInZlotys = winningBid.get().getBidAmount();
            int bidAmountInPennies = (int) Math.round(bidAmountInZlotys * 100);
            product.setUnitPrice(bidAmountInPennies);
            product.setQuantity(1);
            product.setVirtual(true);

            paymentRequest.setProducts(Collections.singletonList(product));

            paymentRequest.setNotifyUrl(notifyUrl);
            paymentRequest.setCustomerIp("192.168.1.1");
            paymentRequest.setMerchantPosId("478097");
            paymentRequest.setCurrencyCode("PLN");
            paymentRequest.setTotalAmount(String.valueOf(bidAmountInPennies));

            OAuthTokenResponse tokenResponse = createOAuthToken();
            log.info("Token OAuth utworzony: {}", tokenResponse.getAccessToken());


            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(tokenResponse.getAccessToken());
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);

            log.info("Wysyłanie żądania płatności");
            ResponseEntity<PaymentResponse> response = restTemplate.postForEntity(paymentsUrl, request, PaymentResponse.class);
            log.info("Odpowiedź na żądanie płatności: {}", response.getBody());


            Payment payment = new Payment();
            payment.setBid(winningBid.get());
            payment.setAmount(winningBid.get().getBidAmount());
            payment.setPaymentStatus("CREATED");

            paymentRepository.save(payment);

            return response.getBody();
        } catch (Exception e) {
            log.error("Nie udało się zainicjować płatności", e);
            throw new RuntimeException("Nie udało się zainicjować płatności", e);
        }
    }
        public void updatePaymentStatus (PaymentNotification notification){
            log.info("Aktualizacja statusu płatności");
            Payment existingPayment = paymentRepository.findByTransactionId(notification.getOrderId());
            existingPayment.setPaymentStatus(notification.getStatus());
            paymentRepository.save(existingPayment);
        }
    }

