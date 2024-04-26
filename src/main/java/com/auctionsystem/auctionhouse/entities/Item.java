package com.auctionsystem.auctionhouse.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
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

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids;

    private String title;

    private String description;

    private Double startPrice;

    private Double currentPrice;

    @CreationTimestamp
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String status;

}
