package ru.kolpakovee.finance_service.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kolpakovee.finance_service.records.ApartmentInfo;
import ru.kolpakovee.finance_service.records.UserDto;

@FeignClient(name = "${integration.services.user-service.name}",
        url = "${integration.services.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/api/v1/apartments/by-user")
    ApartmentInfo getApartmentByToken();

    @GetMapping("/api/v1/users/me")
    UserDto getUserInfo();
}
