package com.voyagecraft.packageservice.controller;

import com.voyagecraft.packageservice.entity.TravelPackage;
import com.voyagecraft.packageservice.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
public class PackageController {

    @Autowired
    private PackageRepository repository;

    @PostMapping
    public TravelPackage addPackage(@RequestBody TravelPackage travelPackage) {
        return repository.save(travelPackage);
    }

    @GetMapping
    public List<TravelPackage> getAllPackages() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public TravelPackage getPackageById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Package not found"));
    }

    @PutMapping("/{id}/reduce-capacity")
    public TravelPackage reduceCapacity(@PathVariable Long id) {
        TravelPackage pkg = repository.findById(id).orElseThrow(() -> new RuntimeException("Package not found"));
        if (pkg.getCapacity() > 0) {
            pkg.setCapacity(pkg.getCapacity() - 1);
            return repository.save(pkg);
        } else {
            throw new RuntimeException("Package is fully booked");
        }
    }
}
