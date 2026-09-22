package com.voyagecraft.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {
    @PostMapping("/payments/process")
    Map<String, Object> processPayment(@RequestBody Map<String, Object> payment);
}
