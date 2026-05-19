package com.pao.laboratory10.exercise2;

import com.pao.laboratory10.exercise1.Tranzactie;
import com.pao.laboratory10.exercise1.TipTranzactie;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Setăm Locale.US pentru parsarea corectă a sumelor cu zecimale (ex: 1500.00)
        scanner.useLocale(Locale.US);

        if (!scanner.hasNextInt()) return;

        // 1. Citim numărul N și stocăm tranzacțiile într-un ArrayList
        int n = scanner.nextInt();
        List<Tranzactie> tranzactii = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int id = scanner.nextInt();
            double suma = scanner.nextDouble();
            String data = scanner.next();
            TipTranzactie tip = TipTranzactie.valueOf(scanner.next());
            tranzactii.add(new Tranzactie(id, suma, data, tip));
        }

        // 2. Procesăm comenzile din stdin până la final
        while (scanner.hasNext()) {
            String command = scanner.next();

            switch (command) {
                case "UNIQUE_IDS": {
                    // LinkedHashSet păstrează ordinea primei apariții și ignoră duplicatele
                    LinkedHashSet<Integer> uniqueIds = new LinkedHashSet<>();
                    for (Tranzactie t : tranzactii) {
                        uniqueIds.add(t.getId());
                    }
                    System.out.println("IDs unice (" + uniqueIds.size() + "): " + uniqueIds);
                    break;
                }

                case "MONTHLY_REPORT": {
                    // TreeMap sortează automat cheile lexicografic (adică cronologic pentru yyyy-MM)
                    // Valoarea va fi un array: index 0 pentru CREDIT, index 1 pentru DEBIT
                    TreeMap<String, double[]> report = new TreeMap<>();
                    for (Tranzactie t : tranzactii) {
                        String luna = t.getData().substring(0, 7);
                        report.putIfAbsent(luna, new double[]{0.0, 0.0});

                        if (t.getTip() == TipTranzactie.CREDIT) {
                            report.get(luna)[0] += t.getSuma();
                        } else {
                            report.get(luna)[1] += t.getSuma();
                        }
                    }

                    for (Map.Entry<String, double[]> entry : report.entrySet()) {
                        System.out.printf(Locale.US, "%s: CREDIT %.2f RON, DEBIT %.2f RON%n",
                                entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
                    }
                    break;
                }

                case "TOP": {
                    int limit = scanner.nextInt();
                    // Creăm o copie pentru a nu modifica lista originală
                    List<Tranzactie> copieDesc = new ArrayList<>(tranzactii);
                    copieDesc.sort((t1, t2) -> Double.compare(t2.getSuma(), t1.getSuma()));

                    System.out.println("Top " + limit + ":");
                    for (int i = 0; i < Math.min(limit, copieDesc.size()); i++) {
                        System.out.println(copieDesc.get(i));
                    }
                    break;
                }

                case "SORT_ASC": {
                    tranzactii.sort(Comparator.comparingDouble(Tranzactie::getSuma));
                    for (Tranzactie t : tranzactii) {
                        System.out.println(t);
                    }
                    break;
                }

                case "SORT_DESC": {
                    tranzactii.sort((t1, t2) -> Double.compare(t2.getSuma(), t1.getSuma()));
                    for (Tranzactie t : tranzactii) {
                        System.out.println(t);
                    }
                    break;
                }

                case "REVERSE": {
                    Collections.reverse(tranzactii);
                    for (Tranzactie t : tranzactii) {
                        System.out.println(t);
                    }
                    break;
                }

                case "MIN_MAX": {
                    Tranzactie min = Collections.min(tranzactii, Comparator.comparingDouble(Tranzactie::getSuma));
                    Tranzactie max = Collections.max(tranzactii, Comparator.comparingDouble(Tranzactie::getSuma));
                    System.out.println("MIN: " + min);
                    System.out.println("MAX: " + max);
                    break;
                }

                case "CME_DEMO": {
                    try {
                        // Modificarea structurii în timpul iterației cu un enhanced for loop va declanșa excepția
                        for (Tranzactie t : tranzactii) {
                            tranzactii.remove(t);
                        }
                    } catch (ConcurrentModificationException e) {
                        System.out.println("ConcurrentModificationException prins: modificare in iteratie detectata.");
                    }
                    break;
                }
            }
        }

        scanner.close();
    }
}