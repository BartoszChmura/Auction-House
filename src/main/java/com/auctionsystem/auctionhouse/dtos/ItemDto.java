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
public class ItemDto {

    @Schema(description = "Unique identifier of the item, generated automatically", readOnly = true)
    private Long id;

    @Schema(description = "Unique identifier of the seller, managed by app", readOnly = true)
    private Long sellerId;

    private Long categoryId;

    @Schema(description = "Unique identifier of the winner, managed by app", readOnly = true)
    private Long winnerId;

    private String title;

    private String description;

    private Double startPrice;

    @Schema(description = "Current price of an item", readOnly = true)
    private Double currentPrice;

    @Schema(description = "Creation time of the auction, generated automatically", readOnly = true)
    private LocalDateTime startTime;

    @Schema(description = "End time of the auction")
    private LocalDateTime endTime;

    @Schema(description = "Creation time of the item, generated automatically", readOnly = true)
    private LocalDateTime createdAt;

    @Schema(description = "Update time of the item, generated automatically", readOnly = true)
    private LocalDateTime updatedAt;

    @Schema(description = "Status of the item, managed by app", readOnly = true)
    private String status;
}