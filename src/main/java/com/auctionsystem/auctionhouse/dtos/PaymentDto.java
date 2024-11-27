package com.auctionsystem.auctionhouse.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    @Schema(description = "Unique identifier of the payment, generated automatically", readOnly = true)
    private Long id;

    private Long bidId;

    private Double amount;

    @Schema(description = "Payment date of the payment, generated automatically", readOnly = true)
    private LocalDateTime paymentDate;

    private String paymentStatus;

    private String transactionId;
}