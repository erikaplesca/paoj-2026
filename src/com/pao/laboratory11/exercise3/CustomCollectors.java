package com.pao.laboratory11.exercise3;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collector;

public class CustomCollectors {

    // Containerul mutabil folosit în timpul colectării
    private static class Agg {
        Map<String, Long> byCountry = new HashMap<>();
        Map<String, Long> byChannel = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        List<Transaction> txs = new ArrayList<>();
    }

    public static Collector<Transaction, ?, Snapshot> toSnapshot(int topN) {
        return Collector.of(
                Agg::new,
                // 1. Accumulate: Adăugăm datele fiecărei tranzacții
                (agg, tx) -> {
                    agg.byCountry.merge(tx.getCountry(), 1L, Long::sum);
                    agg.byChannel.merge(tx.getChannel(), 1L, Long::sum);
                    agg.total = agg.total.add(tx.getAmount());
                    agg.txs.add(tx);
                },
                // 2. Combine: Combinăm sub-rezultatele (dacă stream-ul ar fi paralel)
                (agg1, agg2) -> {
                    agg2.byCountry.forEach((k, v) -> agg1.byCountry.merge(k, v, Long::sum));
                    agg2.byChannel.forEach((k, v) -> agg1.byChannel.merge(k, v, Long::sum));
                    agg1.total = agg1.total.add(agg2.total);
                    agg1.txs.addAll(agg2.txs);
                    return agg1;
                },
                // 3. Finisher: Construim Snapshot-ul și extragem topN
                agg -> {
                    // Sortăm lista pentru a extrage top-ul (am folosit id ca tie-breaker)
                    agg.txs.sort(Comparator.comparing(Transaction::getAmount).reversed()
                            .thenComparing(Transaction::getId));

                    List<Transaction> top = agg.txs.subList(0, Math.min(topN, agg.txs.size()));
                    return new Snapshot(agg.byCountry, agg.byChannel, agg.total, top);
                }
        );
    }
}