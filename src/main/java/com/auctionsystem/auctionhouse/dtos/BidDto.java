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
public class BidDto {

    @Schema(description = "Unique identifier of the bid, generated automatically", readOnly = true)
    private Long id;

    private Long itemId;

    @Schema(description = "Unique identifier of the bidder, managed by app", readOnly = true)
    private Long bidderId;

    private Double bidAmount;

    @Schema(description = "Creation time of the bid, generated automatically", readOnly = true)
    private LocalDateTime bidTime;
}