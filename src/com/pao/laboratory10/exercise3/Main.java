package com.pao.laboratory10.exercise3;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // 0. Pregătirea datelor: Minim 10 tranzacții, minim 3 luni diferite, ambele tipuri
        List<Tranzactie> tranzactii = Arrays.asList(
                new Tranzactie(1, 1500.00, "2024-01-15", TipTranzactie.CREDIT, "RO01INGB"),
                new Tranzactie(2, 250.50, "2024-01-20", TipTranzactie.DEBIT, "RO01INGB"),
                new Tranzactie(3, 3400.00, "2024-01-28", TipTranzactie.CREDIT, "RO02BTRL"),
                new Tranzactie(4, 120.00, "2024-02-05", TipTranzactie.DEBIT, "RO01INGB"),
                new Tranzactie(5, 450.00, "2024-02-14", TipTranzactie.DEBIT, "RO03BCR"),
                new Tranzactie(6, 2100.00, "2024-02-25", TipTranzactie.CREDIT, "RO02BTRL"),
                new Tranzactie(7, 50.00, "2024-03-02", TipTranzactie.DEBIT, "RO01INGB"),
                new Tranzactie(8, 890.00, "2024-03-10", TipTranzactie.CREDIT, "RO04BRD"),
                new Tranzactie(9, 300.00, "2024-03-15", TipTranzactie.DEBIT, "RO03BCR"),
                new Tranzactie(10, 5000.00, "2024-03-22", TipTranzactie.CREDIT, "RO01INGB")
        );

        // 1. filter(tip == CREDIT)
        System.out.println("--- 1. Lista tuturor tranzactiilor CREDIT ---");
        tranzactii.stream()
                .filter(t -> t.getTip() == TipTranzactie.CREDIT)
                .forEach(System.out::println);

        // 2. mapToDouble(suma).sum()
        System.out.println("\n--- 2. Total procesat ---");
        double total = tranzactii.stream()
                .mapToDouble(Tranzactie::getSuma)
                .sum();
        System.out.printf(Locale.US, "Total procesat: %.2f RON\n", total);

        // 3. Collectors.groupingBy(luna, summingDouble(suma))
        System.out.println("\n--- 3. Total per luna ---");
        Map<String, Double> totalPerLuna = tranzactii.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getData().substring(0, 7), // Extragem luna "yyyy-MM"
                        TreeMap::new, // Folosim TreeMap pentru a le avea sortate cronologic
                        Collectors.summingDouble(Tranzactie::getSuma)
                ));
        totalPerLuna.forEach((luna, suma) ->
                System.out.printf(Locale.US, "%s: %.2f RON\n", luna, suma));

        // 4. sorted(comparingDouble.reversed()).limit(3)
        System.out.println("\n--- 4. Top 3 tranzactii ---");
        tranzactii.stream()
                .sorted(Comparator.comparingDouble(Tranzactie::getSuma).reversed())
                .limit(3)
                .forEach(System.out::println);

        // 5. map(contSursa).distinct().collect(toList())
        System.out.println("\n--- 5. Conturi sursa unice ---");
        List<String> conturiUnice = tranzactii.stream()
                .map(Tranzactie::getContSursa)
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Conturi sursa unice: " + conturiUnice);

        // 6. mapToDouble(suma).average()
        System.out.println("\n--- 6. Suma medie ---");
        double media = tranzactii.stream()
                .mapToDouble(Tranzactie::getSuma)
                .average()
                .orElse(0.0);
        System.out.printf(Locale.US, "Suma medie: %.2f RON\n", media);

        // 7. Collectors.groupingBy(luna) cu format extras
        System.out.println("\n--- 7. Extrase de cont lunare ---");
        Map<String, List<Tranzactie>> extrase = tranzactii.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getData().substring(0, 7),
                        TreeMap::new,
                        Collectors.toList()
                ));

        extrase.forEach((luna, lista) -> {
            double sumaLuna = lista.stream().mapToDouble(Tranzactie::getSuma).sum();
            System.out.printf(Locale.US, "EXTRAS DE CONT - %s: %d tranzactii, total: %.2f RON\n",
                    luna, lista.size(), sumaLuna);
        });
    }
}