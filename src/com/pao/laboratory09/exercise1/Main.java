package com.pao.laboratory09.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex1.ser";

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // 1. Citire tranzacții
        int n = Integer.parseInt(sc.nextLine().trim());
        List<Tranzactie> lista = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String[] parts = sc.nextLine().trim().split("\\s+");
            int id = Integer.parseInt(parts[0]);
            double suma = Double.parseDouble(parts[1]);
            String data = parts[2];
            String contSursa = parts[3];
            String contDestinatie = parts[4];
            TipTranzactie tip = TipTranzactie.valueOf(parts[5]);

            Tranzactie t = new Tranzactie(id, suma, data, contSursa, contDestinatie, tip);
            t.note = "procesat"; // setăm înainte de serializare
            lista.add(t);
        }

        // 2. Serializare
        new File("output").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(OUTPUT_FILE))) {
            oos.writeObject(lista);
        }

        // 3. Deserializare
        List<Tranzactie> deserializata;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(OUTPUT_FILE))) {
            deserializata = (List<Tranzactie>) ois.readObject();
        }

        // 4. Comenzi
        while (sc.hasNextLine()) {
            String linie = sc.nextLine().trim();
            if (linie.isEmpty()) continue;

            if (linie.equals("LIST")) {
                for (Tranzactie t : deserializata) {
                    System.out.println(t);
                }

            } else if (linie.startsWith("FILTER ")) {
                String prefix = linie.substring(7).trim();
                List<Tranzactie> filtrate = deserializata.stream()
                        .filter(t -> t.data.startsWith(prefix))
                        .toList();
                if (filtrate.isEmpty()) {
                    System.out.println("Niciun rezultat.");
                } else {
                    for (Tranzactie t : filtrate) {
                        System.out.println(t);
                    }
                }

            } else if (linie.startsWith("NOTE ")) {
                int cautId = Integer.parseInt(linie.substring(5).trim());
                Optional<Tranzactie> gasita = deserializata.stream()
                        .filter(t -> t.id == cautId)
                        .findFirst();
                if (gasita.isPresent()) {
                    System.out.println("NOTE[" + cautId + "]: " + gasita.get().note);
                } else {
                    System.out.println("NOTE[" + cautId + "]: not found");
                }
            }
        }
    }
}