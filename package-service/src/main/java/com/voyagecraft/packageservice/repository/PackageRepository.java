package com.voyagecraft.packageservice.repository;

import com.voyagecraft.packageservice.entity.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<TravelPackage, Long> {
}
