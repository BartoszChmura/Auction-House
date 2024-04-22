package com.auctionsystem.auctionhouse.controller;

import com.auctionsystem.auctionhouse.dto.PaymentNotification;
import com.auctionsystem.auctionhouse.dto.PaymentRequest;
import com.auctionsystem.auctionhouse.dto.PaymentResponse;
import com.auctionsystem.auctionhouse.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/notify")
    public ResponseEntity<?> receivePaymentNotification(@RequestBody PaymentNotification notification) {
        try {
            paymentService.updatePaymentStatus(notification);
            return ResponseEntity.ok(notification);
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
