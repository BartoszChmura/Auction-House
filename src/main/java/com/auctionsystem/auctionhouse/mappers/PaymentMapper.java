package com.auctionsystem.auctionhouse.mappers;

import com.auctionsystem.auctionhouse.dtos.PaymentDto;
import com.auctionsystem.auctionhouse.entities.Bid;
import com.auctionsystem.auctionhouse.entities.Payment;
import org.springframework.stereotype.Component;

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