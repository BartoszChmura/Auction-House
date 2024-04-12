package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.entity.Payment;
import com.auctionsystem.auctionhouse.repository.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    @Transactional
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }
    @Transactional
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    @Transactional
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    @Transactional
    public Payment updatePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
    @Transactional
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
