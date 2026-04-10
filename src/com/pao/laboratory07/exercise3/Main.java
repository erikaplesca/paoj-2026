package com.pao.laboratory07.exercise3;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine().trim());
        List<Comanda> comenzi = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String line = sc.nextLine().trim();
            String[] tokens = line.split(" ");

            switch (tokens[0]) {
                case "STANDARD" -> {
                    String nume = tokens[1];
                    double pret = Double.parseDouble(tokens[2]);
                    String client = tokens[3];
                    comenzi.add(new ComandaStandard(nume, pret, client));
                }
                case "DISCOUNTED" -> {
                    String nume = tokens[1];
                    double pret = Double.parseDouble(tokens[2]);
                    int discount = Integer.parseInt(tokens[3]);
                    String client = tokens[4];
                    comenzi.add(new ComandaRedusa(nume, pret, discount, client));
                }
                case "GIFT" -> {
                    String nume = tokens[1];
                    String client = tokens[2];
                    comenzi.add(new ComandaGratuita(nume, client));
                }
            }
        }

        // Print all orders
        for (Comanda c : comenzi) {
            System.out.println(c.descriere());
        }

        // Process commands
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(" ");
            String cmd = parts[0];

            switch (cmd) {
                case "STATS" -> printStats(comenzi);
                case "FILTER" -> {
                    double threshold = Double.parseDouble(parts[1]);
                    printFilter(comenzi, threshold);
                }
                case "SORT" -> printSort(comenzi);
                case "SPECIAL" -> printSpecial(comenzi);
                case "QUIT" -> {
                    return;
                }
            }
        }
    }

    private static void printStats(List<Comanda> comenzi) {
        System.out.println();
        System.out.println("--- STATS ---");

        // Group by type and compute average
        Map<String, Double> medii = comenzi.stream()
                .collect(Collectors.groupingBy(
                        Comanda::tipComanda,
                        LinkedHashMap::new,
                        Collectors.averagingDouble(Comanda::pretFinal)
                ));

        // Print in fixed order: STANDARD, DISCOUNTED, GIFT
        for (String tip : List.of("STANDARD", "DISCOUNTED", "GIFT")) {
            if (medii.containsKey(tip)) {
                System.out.printf(Locale.US, "%s: medie = %.2f lei\n", tip, medii.get(tip));
            }
        }
    }

    private static void printFilter(List<Comanda> comenzi, double threshold) {
        System.out.println();
        System.out.printf(Locale.US, "--- FILTER (>= %.2f) ---\n", threshold);

        comenzi.stream()
                .filter(c -> c.pretFinal() >= threshold)
                .forEach(c -> {
                    String line = switch (c) {
                        case ComandaStandard s ->
                                String.format(Locale.US, "STANDARD: %s, pret: %.2f lei - client: %s",
                                        c.getNume(), c.pretFinal(), c.getClient());
                        case ComandaRedusa r ->
                                String.format(Locale.US, "DISCOUNTED: %s, pret: %.2f lei - client: %s",
                                        c.getNume(), c.pretFinal(), c.getClient());
                        case ComandaGratuita g ->
                                String.format("GIFT: %s, gratuit - client: %s",
                                        c.getNume(), c.getClient());
                    };
                    System.out.println(line);
                });
    }

    private static void printSort(List<Comanda> comenzi) {
        System.out.println();
        System.out.println("--- SORT (by client, then by pret) ---");

        comenzi.stream()
                .sorted(Comparator.comparing(Comanda::getClient)
                        .thenComparing(Comanda::pretFinal))
                .forEach(c -> {
                    String line = switch (c) {
                        case ComandaStandard s ->
                                String.format(Locale.US, "STANDARD: %s, pret: %.2f lei - client: %s",
                                        c.getNume(), c.pretFinal(), c.getClient());
                        case ComandaRedusa r ->
                                String.format(Locale.US, "DISCOUNTED: %s, pret: %.2f lei - client: %s",
                                        c.getNume(), c.pretFinal(), c.getClient());
                        case ComandaGratuita g ->
                                String.format("GIFT: %s, gratuit - client: %s",
                                        c.getNume(), c.getClient());
                    };
                    System.out.println(line);
                });
    }

    private static void printSpecial(List<Comanda> comenzi) {
        System.out.println();
        System.out.println("--- SPECIAL (discount > 15%) ---");

        comenzi.stream()
                .filter(c -> c instanceof ComandaRedusa r && r.getDiscountProcent() > 15)
                .forEach(c -> {
                    ComandaRedusa r = (ComandaRedusa) c;
                    System.out.printf(Locale.US, "DISCOUNTED: %s, pret: %.2f lei (-%d%%) - client: %s\n",
                            r.getNume(), r.pretFinal(), r.getDiscountProcent(), r.getClient());
                });
    }
}