package ru.kolpakovee.finance_service.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "expenses")
public class ExpensesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(updatable = false, nullable = false)
    private UUID userId;

    @Column(updatable = false, nullable = false)
    private UUID apartmentId;

    private double amount;

    private String description;

    private LocalDateTime createdDate;
}
