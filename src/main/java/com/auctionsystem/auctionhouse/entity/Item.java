package com.auctionsystem.auctionhouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itemid")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sellerid", referencedColumnName = "userid")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "categoryid", referencedColumnName = "categoryid")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "winnerid", referencedColumnName = "userid")
    private User winner;

    private String title;

    private String description;

    private double startPrice;

    private double currentPrice;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String status;

}
