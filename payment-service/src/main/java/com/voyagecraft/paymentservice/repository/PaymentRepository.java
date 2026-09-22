package com.voyagecraft.paymentservice.repository;

import com.voyagecraft.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
