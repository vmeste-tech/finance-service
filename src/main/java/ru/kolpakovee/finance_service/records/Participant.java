package ru.kolpakovee.finance_service.records;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Participant {
    private UUID userId;
    private String name;
    private double expense;
    private double fine;
    private double net;

    public Participant(UUID userId, String name, double expense, double fine) {
        this.userId = userId;
        this.name = name;
        this.expense = expense;
        this.fine = fine;
        this.net = 0.0;
    }
}
