package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.dto.PaymentDto;
import com.auctionsystem.auctionhouse.entity.Payment;
import com.auctionsystem.auctionhouse.mapper.PaymentMapper;
import com.auctionsystem.auctionhouse.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public PaymentDto savePayment(Payment payment) {
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }

    @Transactional
    public Optional<PaymentDto> getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDto);
    }

    @Transactional
    public List<PaymentDto> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentDto updatePayment(Payment payment) {
        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(updatedPayment);
    }

    @Transactional
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
