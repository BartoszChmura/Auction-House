package com.auctionsystem.auctionhouse.mapper;

import com.auctionsystem.auctionhouse.dto.PaymentDto;
import com.auctionsystem.auctionhouse.entity.Bid;
import com.auctionsystem.auctionhouse.entity.Payment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(payment.getId());
        paymentDto.setBidId(payment.getBid().getId());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setPaymentDate(payment.getPaymentDate());
        paymentDto.setPaymentStatus(payment.getPaymentStatus());
        paymentDto.setTransactionId(payment.getTransactionId());
        return paymentDto;
    }

    public Payment toEntity(PaymentDto paymentDto) {
        Payment payment = new Payment();
        payment.setId(paymentDto.getId());
        Bid bid = new Bid();
        bid.setId(paymentDto.getBidId());
        payment.setBid(bid);
        payment.setAmount(paymentDto.getAmount());
        payment.setPaymentDate(paymentDto.getPaymentDate());
        payment.setPaymentStatus(paymentDto.getPaymentStatus());
        payment.setTransactionId(paymentDto.getTransactionId());
        return payment;
    }
}