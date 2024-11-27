package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.PaymentNotification;
import com.auctionsystem.auctionhouse.dtos.PaymentRequest;
import com.auctionsystem.auctionhouse.dtos.PaymentResponse;
import com.auctionsystem.auctionhouse.services.PaymentService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payment")
@Tag(name = "Payment", description = "Endpoints for payment processing")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/notify")
    @Hidden
    public ResponseEntity<?> receivePaymentNotification(@RequestBody PaymentNotification paymentNotification) {
        try {
            paymentService.updatePaymentStatus(paymentNotification);
            return ResponseEntity.ok(paymentNotification);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update payment status");
        }
    }

    @PostMapping("/pay/{winningBidId}")
    @Operation(summary = "Pay for an item", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> payForItem(@RequestBody PaymentRequest paymentRequest, @PathVariable Long winningBidId) {
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
