package com.pao.laboratory08.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    private static final String FILE_PATH = "src/com/pao/laboratory08/tests/studenti.txt";

    public static void main(String[] args) throws Exception {
        List<Student> studenti = citesteStudenti();

        Scanner scanner = new Scanner(System.in);
        String linie = scanner.nextLine().trim();
        String[] parts = linie.split(" ", 2);
        String comanda = parts[0];

        if (comanda.equals("PRINT")) {
            for (Student s : studenti) {
                System.out.println(s);
            }

        } else if (comanda.equals("SHALLOW")) {
            String nume = parts[1].trim();
            Student original = gasesteStudent(studenti, nume);
            Student clona = (Student) original.shallowClone();
            clona.getAdresa().setOras("MODIFICAT"); // afectează și originalul!
            System.out.println("Original: " + original);
            System.out.println("Clona: " + clona);

        } else if (comanda.equals("DEEP")) {
            String nume = parts[1].trim();
            Student original = gasesteStudent(studenti, nume);
            Student clona = (Student) original.deepClone();
            clona.getAdresa().setOras("MODIFICAT"); // originalul rămâne intact
            System.out.println("Original: " + original);
            System.out.println("Clona: " + clona);
        }
    }

    private static List<Student> citesteStudenti() throws IOException {
        List<Student> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                linie = linie.trim();
                if (linie.isEmpty()) continue;
                String[] parts = linie.split(",");
                String nume = parts[0].trim();
                int varsta = Integer.parseInt(parts[1].trim());
                String oras = parts[2].trim();
                String strada = parts[3].trim();
                lista.add(new Student(nume, varsta, new Adresa(oras, strada)));
            }
        }
        return lista;
    }

    private static Student gasesteStudent(List<Student> studenti, String nume) {
        for (Student s : studenti) {
            if (s.getNume().equals(nume)) return s;
        }
        throw new RuntimeException("Student negăsit: " + nume);
    }
}