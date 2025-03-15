package ru.kolpakovee.finance_service.records;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserInfoDto(
        String id,
        String name,
        String lastname,
        String photoUrl,
        String type,
        LocalDateTime joinedAt,
        String status
) {
}
