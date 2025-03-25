package ru.kolpakovee.finance_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kolpakovee.finance_service.records.PenaltyResponse;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "${integration.services.penalty-service.name}",
        url = "${integration.services.penalty-service.url}")
public interface PenaltyServiceClient {

    @GetMapping("/api/v1/penalties/{apartmentId}")
    List<PenaltyResponse> getApartmentPenalties(@PathVariable UUID apartmentId);
}
