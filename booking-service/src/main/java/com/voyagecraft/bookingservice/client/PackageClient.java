package com.voyagecraft.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.Map;

@FeignClient(name = "PACKAGE-SERVICE")
public interface PackageClient {
    @PutMapping("/packages/{id}/reduce-capacity")
    Map<String, Object> reduceCapacity(@PathVariable("id") Long id);
}
