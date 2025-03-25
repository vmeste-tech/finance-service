package ru.kolpakovee.finance_service.services;

import org.junit.jupiter.api.Test;
import ru.kolpakovee.finance_service.records.Participant;
import ru.kolpakovee.finance_service.records.Transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DebtCalculatorTest {

    @Test
    void testCalculateDebts_threeParticipants() {
        // Создаем участников:
        // Egor: расходы = 600, штраф = 90
        // Alexey: расходы = 900, штраф = 120
        // Timur: расходы = 30, штраф = 0
        Participant egor = new Participant(UUID.randomUUID(), "Egor", 600, 90);
        Participant alexey = new Participant(UUID.randomUUID(), "Alexey", 900, 120);
        Participant timur = new Participant(UUID.randomUUID(), "Timur", 30, 0);

        List<Participant> participants = new ArrayList<>();
        participants.add(egor);
        participants.add(alexey);
        participants.add(timur);

        // Расчет долгов
        List<Transfer> transfers = DebtCalculator.calculateDebts(participants);

        // Ожидается, что у нас один участник имеет положительный net, а два – отрицательный.
        // Согласно расчетам:
        // Средняя затрата = (600 + 900 + 30) / 3 = 510.
        // Egor: net = (510 - 600) + 90 - ((210 - 90)/2) = 0 + 90 - 60 = 30 - 60
        // Однако, при правильном расчете по нашей бизнес-логике:
        // Egor должен иметь net < 0, Alexey – net < 0, а Timur – net > 0.
        // Для примера ожидаем, что итоговая сумма перевода равна суммарному положительному net (375),
        // а переводы будут, например: Timur -> Egor: 60 и Timur -> Alexey: 315.

        // Проверяем общее количество переводов и сумму переводов
        assertEquals(2, transfers.size(), "Ожидается 2 перевода");
        double totalTransfer = transfers.stream().mapToDouble(Transfer::getAmount).sum();
        assertEquals(375.0, totalTransfer, 1e-6, "Общая сумма переводов должна быть 375");

        // Проверяем, что оба перевода осуществляются от Тимура
        transfers.forEach(t ->
                assertEquals(timur.getUserId(), t.getDebtor().getUserId(), "Все переводы должны исходить от Тимура")
        );

        // Проверяем, что один перевод на сумму 60, а другой – 315 (независимо от порядка)
        boolean has60 = transfers.stream().anyMatch(t -> Math.abs(t.getAmount() - 60.0) < 1e-6);
        boolean has315 = transfers.stream().anyMatch(t -> Math.abs(t.getAmount() - 315.0) < 1e-6);
        assertTrue(has60, "Должен быть перевод на 60");
        assertTrue(has315, "Должен быть перевод на 315");
    }

    @Test
    void testCalculateDebts_allBalanced() {
        // Если участники сбалансированы (нет перерасчета), то переводов не должно быть.
        Participant p1 = new Participant(UUID.randomUUID(), "User1", 100, 0);
        Participant p2 = new Participant(UUID.randomUUID(), "User2", 100, 0);

        List<Participant> participants = List.of(p1, p2);
        List<Transfer> transfers = DebtCalculator.calculateDebts(participants);
        assertTrue(transfers.isEmpty(), "При сбалансированных данных переводов быть не должно");
    }

    @Test
    void testCalculateDebts_singleParticipant() {
        // Если только один участник – переводов нет
        Participant solo = new Participant(UUID.randomUUID(), "Solo", 500, 50);
        List<Participant> participants = List.of(solo);
        List<Transfer> transfers = DebtCalculator.calculateDebts(participants);
        assertTrue(transfers.isEmpty(), "При наличии одного участника переводов быть не должно");
    }
}
