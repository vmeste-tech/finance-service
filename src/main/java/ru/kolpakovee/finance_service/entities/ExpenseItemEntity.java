package ru.kolpakovee.finance_service.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "expenses_items")
public class ExpenseItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(updatable = false, nullable = false)
    private UUID apartmentId;

    private String name;

    private String description;
}
