package com.pao.laboratory11.exercise1;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    // Constante obligatorii pentru scoruri si praguri
    private static final Set<String> HIGH_RISK_COUNTRIES =
            new HashSet<>(Arrays.asList("RU", "NG", "IR", "KP", "SY"));

    private static final Map<String, Integer> CHANNEL_SCORE = new HashMap<>();
    static {
        CHANNEL_SCORE.put("WEB", 15);
        CHANNEL_SCORE.put("APP", 10);
        CHANNEL_SCORE.put("CRYPTO", 30);
        CHANNEL_SCORE.put("POS", 5);
        CHANNEL_SCORE.put("ATM", 0);
    }

    private static final int FLAG_THRESHOLD = 60;

    // Partea A - Definirea regulilor simple ca Predicate
    public static final Predicate<Transaction> amountOverThreshold = tx -> tx.amount >= 1000;
    public static final Predicate<Transaction> countryInRisk = tx -> HIGH_RISK_COUNTRIES.contains(tx.country);
    public static final Predicate<Transaction> channelSuspicious = tx ->
            Arrays.asList("WEB", "APP", "CRYPTO").contains(tx.channel);

    // Partea C - Comparatorul determinist
    private static final Comparator<Transaction> BY_RISK_DESC_THEN_ID_ASC =
            Comparator.comparingInt(Main::riskScore)
                    .reversed()
                    .thenComparingInt(t -> t.id);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);

        if (!scanner.hasNextInt()) return;

        int n = scanner.nextInt();
        Map<Integer, Transaction> byId = new HashMap<>();
        List<Transaction> all = new ArrayList<>();

        // Citirea tranzactiilor
        for (int i = 0; i < n; i++) {
            int id = scanner.nextInt();
            double amount = scanner.nextDouble();
            String date = scanner.next();
            String country = scanner.next().toUpperCase();
            String channel = scanner.next().toUpperCase();

            Transaction tx = new Transaction(id, amount, date, country, channel);
            byId.put(id, tx);
            all.add(tx);
        }

        if (!scanner.hasNextInt()) return;
        int q = scanner.nextInt();

        // Procesarea comenzilor
        for (int i = 0; i < q; i++) {
            String cmd = scanner.next().toUpperCase();

            switch (cmd) {
                case "CHECK": {
                    int id = scanner.nextInt();
                    Transaction tx = byId.get(id);
                    if (tx == null) {
                        System.out.println("CHECK " + id + " => NOT_FOUND");
                    } else {
                        System.out.println("CHECK " + id + " => " + formatRiskLine(tx, false));
                    }
                    break;
                }
                case "LIST_FLAGGED": {
                    List<Transaction> flagged = all.stream()
                            .filter(tx -> riskScore(tx) >= FLAG_THRESHOLD)
                            .sorted(BY_RISK_DESC_THEN_ID_ASC)
                            .collect(Collectors.toList());

                    if (flagged.isEmpty()) {
                        System.out.println("NONE");
                    } else {
                        flagged.forEach(tx -> System.out.println(formatRiskLine(tx, true)));
                    }
                    break;
                }
                case "TOP_RISK": {
                    int k = scanner.nextInt();
                    all.stream()
                            .sorted(BY_RISK_DESC_THEN_ID_ASC)
                            .limit(k)
                            .forEach(tx -> System.out.println(formatRiskLine(tx, true)));
                    break;
                }
                default: {
                    System.out.println("ERR UNKNOWN_COMMAND");
                    // Consumă restul liniei pentru a nu strica citirile viitoare
                    if (scanner.hasNextLine()) scanner.nextLine();
                    break;
                }
            }
        }
        scanner.close();
    }

    // Calcularea scorului de risc
    private static int riskScore(Transaction tx) {
        int score = 0;

        // Reguli pentru suma (mutually exclusive down to 100)
        if (tx.amount >= 5000.0) {
            score += 70;
        } else if (tx.amount >= 1000.0) {
            score += 40;
        } else if (tx.amount >= 500.0) {
            score += 20;
        }

        // Regula independenta pentru sume mici
        if (tx.amount <= 100.0) {
            score += 5;
        }

        // Regula pentru tara
        if (countryInRisk.test(tx)) {
            score += 25;
        }

        // Regula pentru canal
        score += CHANNEL_SCORE.getOrDefault(tx.channel, 0);

        return score;
    }

    private static String verdict(int score) {
        return score >= FLAG_THRESHOLD ? "FLAG" : "ALLOW";
    }

    private static String formatRiskLine(Transaction tx, boolean includeIdBracket) {
        int score = riskScore(tx);
        String v = verdict(score);
        if (includeIdBracket) {
            return "[" + tx.id + "] " + v + " score=" + score;
        } else {
            return v + " score=" + score;
        }
    }

    // Clasa interna pentru tranzactii
    private static class Transaction {
        final int id;
        final double amount;
        final String date;
        final String country;
        final String channel;

        Transaction(int id, double amount, String date, String country, String channel) {
            this.id = id;
            this.amount = amount;
            this.date = date;
            this.country = country;
            this.channel = channel;
        }
    }
}