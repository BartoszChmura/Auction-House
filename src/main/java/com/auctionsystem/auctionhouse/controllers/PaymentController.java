package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.PaymentNotification;
import com.auctionsystem.auctionhouse.dtos.PaymentRequest;
import com.auctionsystem.auctionhouse.dtos.PaymentResponse;
import com.auctionsystem.auctionhouse.services.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/notify")
    public ResponseEntity<?> receivePaymentNotification(@RequestBody PaymentNotification paymentNotification) {
        try {
            paymentService.updatePaymentStatus(paymentNotification);
            return ResponseEntity.ok(paymentNotification);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nie udało się zaktualizować statusu płatności");
        }
    }

    @PostMapping("/pay/{winningBidId}")
    public ResponseEntity<?> payForItem (@RequestBody PaymentRequest paymentRequest, @PathVariable Long winningBidId) {
        try {
            PaymentResponse paymentResponse = paymentService.initiatePayment(paymentRequest, winningBidId);
            return ResponseEntity.ok(paymentResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
