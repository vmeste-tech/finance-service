package ru.kolpakovee.finance_service.services;

import lombok.experimental.UtilityClass;
import ru.kolpakovee.finance_service.records.Participant;
import ru.kolpakovee.finance_service.records.Transfer;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class DebtCalculator {

    public List<Transfer> calculateDebts(List<Participant> participants) {
        int n = participants.size();
        double totalExpense = 0;
        double totalFine = 0;
        for (Participant p : participants) {
            totalExpense += p.getExpense();
            totalFine += p.getFine();
        }
        double avgExpense = totalExpense / n;

        // Рассчитываем net для каждого участника
        for (Participant p : participants) {
            double othersFine = totalFine - p.getFine();
            // Формула: (средняя затрата - личные затраты) + личные штрафы - (штрафы остальных / (n - 1))
            p.setNet((avgExpense - p.getExpense()) + p.getFine() - (othersFine / (n - 1)));
        }

        List<Participant> debtors = new ArrayList<>();
        List<Participant> creditors = new ArrayList<>();

        double epsilon = 1e-6;
        for (Participant p : participants) {
            if (p.getNet() > epsilon) {
                debtors.add(p);
            } else if (p.getNet() < -epsilon) {
                creditors.add(p);
            }
        }

        // Распределяем переводы
        List<Transfer> transfers = new ArrayList<>();
        int i = 0, j = 0;
        while (i < debtors.size() && j < creditors.size()) {
            Participant debtor = debtors.get(i);
            Participant creditor = creditors.get(j);

            // Определяем сумму перевода как минимум из оставшихся сумм
            double amount = Math.min(debtor.getNet(), -creditor.getNet());
            transfers.add(new Transfer(debtor, creditor, amount));

            // Обновляем net
            debtor.setNet(debtor.getNet() - amount);
            creditor.setNet(creditor.getNet() + amount); // creditor.getNet() отрицательный, поэтому прибавляем

            // Если долг погашен, переходим к следующему
            if (Math.abs(debtor.getNet()) < epsilon) {
                i++;
            }
            if (Math.abs(creditor.getNet()) < epsilon) {
                j++;
            }
        }

        return transfers;
    }
}

