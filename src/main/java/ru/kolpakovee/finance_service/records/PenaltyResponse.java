package ru.kolpakovee.finance_service.records;

import lombok.Builder;
import ru.kolpakovee.finance_service.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PenaltyResponse(
        UUID id,
        UserInfoDto user,
        double fineAmount,
        LocalDateTime assignedDate,
        PaymentStatus status
) {
}
