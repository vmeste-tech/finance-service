package ru.kolpakovee.finance_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kolpakovee.finance_service.entities.DebtEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface DebtsRepository extends JpaRepository<DebtEntity, UUID> {

    List<DebtEntity> findAllByDebtorIdAndPeriod(UUID debtorId, int period);

    List<DebtEntity> findAllByDebtorIdInAndPeriod(Set<UUID> debtorIds, int period);

    void deleteAllByDebtorIdIsInAndPeriod(Set<UUID> debtorIds, int period);
}
