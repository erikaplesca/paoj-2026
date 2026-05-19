package com.pao.laboratory11.exercise3;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 1. Pregătirea datelor (incluzând un tie-breaker pentru suma de 3400.00)
        List<Transaction> data = Arrays.asList(
                new Transaction(1, new BigDecimal("1500.00"), LocalDate.of(2024, 1, 15), "RO", "WEB"),
                new Transaction(2, new BigDecimal("250.50"), LocalDate.of(2024, 1, 20), "UK", "APP"),
                new Transaction(3, new BigDecimal("3400.00"), LocalDate.of(2024, 1, 28), "RO", "POS"),
                new Transaction(4, new BigDecimal("120.00"), LocalDate.of(2024, 2, 5), "US", "WEB"),
                new Transaction(5, new BigDecimal("450.00"), LocalDate.of(2024, 2, 14), "UK", "APP"),
                new Transaction(6, new BigDecimal("3400.00"), LocalDate.of(2024, 2, 25), "IT", "POS"),
                new Transaction(7, new BigDecimal("50.00"), LocalDate.of(2024, 3, 2), "RO", "WEB"),
                new Transaction(8, new BigDecimal("890.00"), LocalDate.of(2024, 3, 10), "FR", "APP")
        );

        // 2. Colectarea - un singur stream trece prin date
        Snapshot snap = data.stream().collect(CustomCollectors.toSnapshot(3));

        System.out.println("Snapshot creat cu succes! Total procesat: " + snap.getTotalAmount() + " RON\n");

        // 3. Interogare 1: Top 3 tranzacții
        System.out.println("--- 1. top tranzactii ---");
        snap.getTopTransactions().forEach(System.out::println);

        // 4. Interogare 2: Distribuția pe țări (sortat descrescător după volum)
        System.out.println("\n--- 2. numar tranzactii per tara (descrescator) ---");
        snap.getCountByCountry().entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

        // 5. Interogare 3: Canale (sortate alfabetic)
        System.out.println("\n--- 3. tranzactii per vanal (alfabetic) ---");
        snap.getCountByChannel().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

        // 6. Test Imutabilitate
        try {
            snap.getTopTransactions().add(new Transaction(99, BigDecimal.TEN, LocalDate.now(), "XX", "YY"));
        } catch (UnsupportedOperationException e) {
            System.out.println("\n(Dovada imutabilitatii: Excepție declanșată la încercarea de a adăuga în lista read-only, exact cum trebuie!)");
        }
    }
}