package ru.kolpakovee.finance_service.records;

import lombok.Builder;
import ru.kolpakovee.finance_service.enums.DebtStatus;

@Builder
public record DebtDto(
        String debtor,
        String creditor,
        double amount,
        DebtStatus status,
        int period
) {
}
