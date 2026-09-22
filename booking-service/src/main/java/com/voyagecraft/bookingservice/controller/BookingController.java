package com.voyagecraft.bookingservice.controller;

import com.voyagecraft.bookingservice.client.PackageClient;
import com.voyagecraft.bookingservice.client.PaymentClient;
import com.voyagecraft.bookingservice.entity.Reservation;
import com.voyagecraft.bookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository repository;
    
    @Autowired
    private PaymentClient paymentClient;
    
    @Autowired
    private PackageClient packageClient;

    @PostMapping
    public Reservation bookPackage(@RequestBody Reservation reservation) {
        reservation.setStatus("PENDING");
        Reservation savedReservation = repository.save(reservation);
        
        try {
            // Process payment
            Map<String, Object> paymentReq = new HashMap<>();
            paymentReq.put("bookingId", savedReservation.getId());
            paymentReq.put("amount", 100.0); // Dummy amount
            
            Map<String, Object> paymentRes = paymentClient.processPayment(paymentReq);
            
            if ("SUCCESS".equals(paymentRes.get("status"))) {
                // Update package capacity
                packageClient.reduceCapacity(reservation.getPackageId());
                
                savedReservation.setStatus("CONFIRMED");
                return repository.save(savedReservation);
            } else {
                savedReservation.setStatus("FAILED");
                return repository.save(savedReservation);
            }
        } catch (Exception e) {
            savedReservation.setStatus("FAILED");
            return repository.save(savedReservation);
        }
    }

    @GetMapping
    public java.util.List<Reservation> getAllBookings() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Reservation getBookingById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }
}
