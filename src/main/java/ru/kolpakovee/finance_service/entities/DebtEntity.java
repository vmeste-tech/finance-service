package ru.kolpakovee.finance_service.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.kolpakovee.finance_service.enums.DebtStatus;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "debts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"debtor_id", "creditor_id", "period"})
)
public class DebtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "debtor_id", nullable = false)
    private UUID debtorId;

    @Column(name = "creditor_id", nullable = false)
    private UUID creditorId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DebtStatus status;

    /**
     * Поле period хранит период в формате YYYYMM (например, 202401).
     */
    @Column(nullable = false)
    private Integer period;
}

