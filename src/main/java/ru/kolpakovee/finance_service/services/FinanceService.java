package ru.kolpakovee.finance_service.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.finance_service.clients.PenaltyServiceClient;
import ru.kolpakovee.finance_service.clients.UserServiceClient;
import ru.kolpakovee.finance_service.entities.DebtEntity;
import ru.kolpakovee.finance_service.entities.ExpensesEntity;
import ru.kolpakovee.finance_service.enums.DebtStatus;
import ru.kolpakovee.finance_service.records.*;
import ru.kolpakovee.finance_service.repositories.DebtsRepository;
import ru.kolpakovee.finance_service.repositories.ExpensesRepository;
import ru.kolpakovee.finance_service.utiles.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final ExpensesRepository expensesRepository;
    private final DebtsRepository debtsRepository;

    private final PenaltyServiceClient penaltyServiceClient;
    private final UserServiceClient userServiceClient;

    @Transactional
    public List<DebtDto> getUserDebts(UUID apartmentId, int period) {
        PeriodRange range = DateTimeUtils.getPeriodRange(period);
        List<UserInfoDto> users = userServiceClient.getApartmentByToken().users();

        // Построим карту: userId -> имя пользователя
        Map<UUID, String> userNames = users.stream()
                .collect(Collectors.toMap(UserInfoDto::id, UserInfoDto::name));

        if (LocalDateTime.now().isAfter(range.end())) {
            List<DebtEntity> debts = debtsRepository.findAllByDebtorIdInAndPeriod(userNames.keySet(), period);

            if (!debts.isEmpty()) {
                return debts.stream()
                        .map(d -> DebtDto.builder()
                                .period(d.getPeriod())
                                .status(d.getStatus())
                                .debtor(userNames.get(d.getDebtorId()))
                                .creditor(userNames.get(d.getCreditorId()))
                                .amount(d.getAmount())
                                .build())
                        .toList();
            }
        }

        debtsRepository.deleteAllByDebtorIdInAndPeriod(userNames.keySet(), period);

        // Получаем все расходы для квартиры за текущий месяц
        List<ExpensesEntity> expenses = expensesRepository
                .findByApartmentIdAndPeriod(apartmentId, range.start(), range.end());

        // получить все штрафы за текущий месяц
        List<PenaltyResponse> penalties = penaltyServiceClient.getApartmentPenalties(apartmentId)
                .stream()
                .filter(p -> p.assignedDate().isAfter(range.start()) && p.assignedDate().isBefore(range.end()))
                .toList();

        Map<UUID, Participant> participantMap = users.stream()
                .collect(Collectors.toMap(UserInfoDto::id, u ->
                        new Participant(u.id(), u.name(), 0.0, 0.0)));

        // Обновляем данные по расходам
        for (ExpensesEntity expense : expenses) {
            UUID userId = expense.getUserId();
            Participant participant = participantMap.get(userId);
            participant.setExpense(participant.getExpense() + expense.getAmount());
        }

        // Обновляем данные по штрафам
        for (PenaltyResponse penalty : penalties) {
            UUID userId = penalty.user().id();
            Participant participant = participantMap.get(userId);
            participant.setFine(participant.getFine() + penalty.fineAmount());
        }

        // Итоговый список участников для расчёта долгов:
        List<Participant> participants = new ArrayList<>(participantMap.values());

        List<Transfer> transfers = DebtCalculator.calculateDebts(participants);

        List<DebtEntity> entities = transfers.stream()
                .map(t -> {
                    DebtEntity entity = new DebtEntity();
                    entity.setAmount(t.getAmount());
                    entity.setStatus(DebtStatus.UNPAID);
                    entity.setPeriod(period);
                    entity.setDebtorId(t.getDebtor().getUserId());
                    entity.setCreditorId(t.getCreditor().getUserId());
                    return entity;
                }).toList();

        debtsRepository.saveAll(entities);

        return transfers.stream()
                .map(t -> DebtDto.builder()
                        .debtor(t.getDebtor().getName())
                        .creditor(t.getCreditor().getName())
                        .amount(t.getAmount())
                        .status(DebtStatus.UNPAID)
                        .period(period)
                        .build())
                .toList();
    }

    public List<UserFinanceDto> getUserFinances(UUID apartmentId, int period) {
        throw new UnsupportedOperationException();
    }

    public DebtDto payDebt(UUID debtId) {
        throw new UnsupportedOperationException();
    }
}
