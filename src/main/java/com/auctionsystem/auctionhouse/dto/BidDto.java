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
public class BidDto {

    private Long id;

    private Long itemId;

    private Long bidderId;

    private Double bidAmount;

    private LocalDateTime bidTime;
}
