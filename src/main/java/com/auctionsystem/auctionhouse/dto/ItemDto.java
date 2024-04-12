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
public class ItemDto {

    private Long id;

    private Long sellerId;

    private Long categoryId;

    private Long winnerId;

    private String title;

    private String description;

    private double startPrice;

    private double currentPrice;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status;
}
