package com.auctionsystem.auctionhouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bidid")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "itemid", referencedColumnName = "itemid")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private User bidder;

    private double bidAmount;

    private LocalDateTime bidTime;
}
