package com.voyagecraft.bookingservice.repository;

import com.voyagecraft.bookingservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Reservation, Long> {
}
