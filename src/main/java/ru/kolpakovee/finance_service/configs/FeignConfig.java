package ru.kolpakovee.finance_service.configs;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kolpakovee.finance_service.interceptors.JwtRequestInterceptor;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return new JwtRequestInterceptor();
    }
}
