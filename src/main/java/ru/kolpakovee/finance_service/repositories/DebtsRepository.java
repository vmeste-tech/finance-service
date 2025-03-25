package ru.kolpakovee.finance_service.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kolpakovee.finance_service.entities.DebtEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface DebtsRepository extends JpaRepository<DebtEntity, UUID> {

    List<DebtEntity> findAllByDebtorIdInAndPeriod(Set<UUID> debtorIds, int period);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("delete from DebtEntity d where d.debtorId in :debtorIds and d.period = :period")
    void deleteAllByDebtorIdInAndPeriod(@Param("debtorIds") Set<UUID> debtorIds, @Param("period") int period);
}
