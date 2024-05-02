package com.CashWithdrawal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class AtmWithdrawalTest {
    private AtmWithdrawal atm;

    @BeforeEach
    public void setUp() {
        Map<Integer, Integer> denominationCounts = new HashMap<>();
        denominationCounts.put(100, 10);
        denominationCounts.put(50, 10);
        denominationCounts.put(20, 10);
        denominationCounts.put(10, 10);
        atm = new AtmWithdrawal(denominationCounts);
    }

    @Test
    public void testWithdrawSuccess() {
        assertTrue(atm.withdraw(180));
    }

    @Test
    public void testWithdrawFailure() {
        assertFalse(atm.withdraw(-50));
        assertFalse(atm.withdraw(5));
        assertFalse(atm.withdraw(1000));
    }

    @Test
    public void testParallelWithdrawals() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                assertTrue(atm.withdraw(150));
            });
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(10 - 5, atm.getDenominationCounts().get(100)); // Assuming withdrawal successful, reduce note count
    }
}
