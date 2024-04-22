package com.auctionsystem.auctionhouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentid")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bidid", referencedColumnName = "bidid")
    private Bid bid;

    private Double amount;

    private String paymentStatus;

    @CreationTimestamp
    private LocalDateTime paymentDate;

    private String transactionId;
}
