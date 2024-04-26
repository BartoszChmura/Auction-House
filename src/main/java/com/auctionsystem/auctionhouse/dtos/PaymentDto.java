package com.auctionsystem.auctionhouse.dtos;

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

    private Double amount;

    private LocalDateTime paymentDate;

    private String paymentStatus;

    private String transactionId;
}
