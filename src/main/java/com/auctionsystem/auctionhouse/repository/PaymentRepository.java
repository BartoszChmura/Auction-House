package com.auctionsystem.auctionhouse.repository;

import com.auctionsystem.auctionhouse.dto.PaymentDto;
import com.auctionsystem.auctionhouse.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Payment findByTransactionId(String transactionId);
}
