package com.auctionsystem.auctionhouse.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bidid")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "itemid", referencedColumnName = "itemid")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "bidderid", referencedColumnName = "userid")
    private User bidder;

    @OneToMany(mappedBy = "bid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    private Double bidAmount;

    @CreationTimestamp
    private LocalDateTime bidTime;
}
