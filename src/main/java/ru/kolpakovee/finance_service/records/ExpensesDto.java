package ru.kolpakovee.finance_service.records;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExpensesDto(
        UUID id,
        UUID userId,
        double amount,
        LocalDateTime createdDate
) {
}
