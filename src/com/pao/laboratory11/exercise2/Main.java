package com.pao.laboratory11.exercise2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            run();
        } catch (IOException e) {
            // Keep deterministic checker output.
        }
    }

    private static void run() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String first = nextNonEmpty(br);
        if (first == null) {
            return;
        }

        int n = Integer.parseInt(first);
        List<Tx> txs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String line = nextNonEmpty(br);
            if (line == null) {
                return;
            }

            // Separarea token-urilor pe spatii multiple, conform cerintei
            String[] p = line.split("\\s+");
            txs.add(new Tx(
                    Integer.parseInt(p[0]),
                    Double.parseDouble(p[1]),
                    p[2],
                    p[3],
                    p[4],
                    p[5]));
        }

        int q = Integer.parseInt(nextNonEmpty(br));
        for (int i = 0; i < q; i++) {
            String line = nextNonEmpty(br);
            if (line == null) {
                return;
            }

            String[] p = line.split("\\s+");
            String op = p[0];

            switch (op) {
                case "REPORT_MONTH": {
                    String month = p[1];
                    // Folosim Stream API si DoubleSummaryStatistics pentru a extrage atat suma cat si count-ul dintr-o singura trecere
                    DoubleSummaryStatistics stats = txs.stream()
                            .filter(tx -> tx.date.startsWith(month))
                            .collect(Collectors.summarizingDouble(tx -> tx.amount));

                    System.out.printf(Locale.US, "MONTH %s total=%.2f count=%d%n",
                            month, stats.getSum(), stats.getCount());
                    break;
                }

                case "REPORT_ACCOUNT": {
                    String account = p[1];
                    // Aceeasi logica folosind Stream API pentru conturi
                    DoubleSummaryStatistics stats = txs.stream()
                            .filter(tx -> tx.account.equals(account))
                            .collect(Collectors.summarizingDouble(tx -> tx.amount));

                    System.out.printf(Locale.US, "ACCOUNT %s total=%.2f count=%d%n",
                            account, stats.getSum(), stats.getCount());
                    break;
                }

                case "TOP_CHANNELS": {
                    int k = Integer.parseInt(p[1]);

                    if (txs.isEmpty()) {
                        System.out.println("NONE");
                        break;
                    }

                    // 1. Grupăm și numărăm aparițiile fiecărui canal
                    Map<String, Long> counts = txs.stream()
                            .collect(Collectors.groupingBy(tx -> tx.channel, Collectors.counting()));

                    // 2. Sortăm stream-ul conform regulilor: count descendent, apoi alfabetic ascendent
                    counts.entrySet().stream()
                            .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                                    .thenComparing(Map.Entry.comparingByKey()))
                            .limit(k)
                            .forEach(e -> System.out.println(e.getKey() + " " + e.getValue()));
                    break;
                }

                default:
                    // Ignoram comenzile necunoscute
                    break;
            }
        }
    }

    private static String nextNonEmpty(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                return line.trim();
            }
        }
        return null;
    }

    // Am pastrat clasa interna exact cum era definita, avand campurile specificate (id, amount, date, country, channel, account)
    private static final class Tx {
        private final int id;
        private final double amount;
        private final String date;
        private final String country;
        private final String channel;
        private final String account;

        private Tx(int id, double amount, String date, String country, String channel, String account) {
            this.id = id;
            this.amount = amount;
            this.date = date;
            this.country = country;
            this.channel = channel;
            this.account = account;
        }
    }
}