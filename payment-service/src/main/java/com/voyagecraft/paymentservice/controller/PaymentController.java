package com.voyagecraft.paymentservice.controller;

import com.voyagecraft.paymentservice.entity.Payment;
import com.voyagecraft.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository repository;

    @PostMapping("/process")
    public Payment processPayment(@RequestBody Payment payment) {
        // Dummy processing logic
        payment.setStatus("SUCCESS");
        return repository.save(payment);
    }
}
