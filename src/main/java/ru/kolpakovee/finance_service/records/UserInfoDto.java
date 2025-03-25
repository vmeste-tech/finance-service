package ru.kolpakovee.finance_service.records;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserInfoDto(
        UUID id,
        String name,
        String lastname,
        String photoUrl,
        String type,
        LocalDateTime joinedAt,
        String status
) {
}
