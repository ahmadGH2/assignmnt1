package com.CashWithdrawal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AtmWithdrawal {

    private final Map<Integer, Integer> denominationCounts;

    public AtmWithdrawal(Map<Integer, Integer> denominationCounts) {
        this.denominationCounts = new ConcurrentHashMap<>(denominationCounts);
    }

    public synchronized boolean withdraw(int amount) {
        if (amount <= 0) return false;
        Map<Integer, Integer> tempDenominationCounts = new ConcurrentHashMap<>(denominationCounts);
        Map<Integer, Integer> withdrawnDenominations = new HashMap<>();
        AtomicInteger remainingAmount = new AtomicInteger(amount);

        List<Integer> denominations = new ArrayList<>(tempDenominationCounts.keySet());
        denominations.sort(Comparator.reverseOrder());

        for (int denomination : denominations) {
            int count = tempDenominationCounts.getOrDefault(denomination, 0);
            int neededNotes = remainingAmount.get() / denomination;
            int notesToDispense = Math.min(count, neededNotes);
            if (notesToDispense > 0) {
                withdrawnDenominations.put(denomination, notesToDispense);
                remainingAmount.addAndGet(-notesToDispense * denomination);
                tempDenominationCounts.put(denomination, count - notesToDispense);
            }
        }

        if (remainingAmount.get() == 0) {
            withdrawnDenominations.forEach((denomination, count) -> {
                int updatedCount = denominationCounts.get(denomination) - count;
                denominationCounts.put(denomination, updatedCount);
            });
            return true;
        } else {
            return false;
        }
    }

    public Map<Integer, Integer> getDenominationCounts() {
        return Collections.unmodifiableMap(denominationCounts);
    }
}




