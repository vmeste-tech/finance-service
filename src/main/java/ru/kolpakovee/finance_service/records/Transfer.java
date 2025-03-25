package ru.kolpakovee.finance_service.records;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Transfer {
    private Participant debtor;
    private Participant creditor;
    private double amount;

    @Override
    public String toString() {
        return debtor + " -> " + creditor + ": " + String.format("%.2f", amount);
    }
}
