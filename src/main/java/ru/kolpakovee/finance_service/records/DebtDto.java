package ru.kolpakovee.finance_service.records;

import lombok.Builder;
import ru.kolpakovee.finance_service.enums.DebtStatus;

import java.util.UUID;

@Builder
public record DebtDto(
        UUID id,
        String debtor,
        String creditor,
        double amount,
        DebtStatus status,
        int period
) {
}
