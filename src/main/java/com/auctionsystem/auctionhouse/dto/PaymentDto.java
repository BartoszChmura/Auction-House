package com.auctionsystem.auctionhouse.dto;

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

    private Long id;

    private Long bidId;

    private double amount;

    private LocalDateTime paymentDate;

    private String paymentStatus;

    private String transactionId;
}
