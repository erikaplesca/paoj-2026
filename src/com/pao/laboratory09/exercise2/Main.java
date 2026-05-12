package com.pao.laboratory09.exercise2;

import com.pao.laboratory09.exercise1.TipTranzactie;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex2.bin";
    private static final int RECORD_SIZE = 32;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int n = Integer.parseInt(sc.nextLine().trim());

        // 1. Citire tranzacții
        int[] ids = new int[n];
        double[] sume = new double[n];
        String[] date = new String[n];
        TipTranzactie[] tipuri = new TipTranzactie[n];

        for (int i = 0; i < n; i++) {
            String[] parts = sc.nextLine().trim().split("\\s+");
            ids[i] = Integer.parseInt(parts[0]);
            sume[i] = Double.parseDouble(parts[1]);
            date[i] = parts[2];
            tipuri[i] = TipTranzactie.valueOf(parts[3]);
        }

        // 2. Scriere binară cu DataOutputStream
        new File("output").mkdirs();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(OUTPUT_FILE))) {
            for (int i = 0; i < n; i++) {
                // bytes 0-3: id, little-endian
                byte[] idBytes = ByteBuffer.allocate(4)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(ids[i]).array();
                dos.write(idBytes);

                // bytes 4-11: suma, little-endian
                byte[] sumaBytes = ByteBuffer.allocate(8)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putDouble(sume[i]).array();
                dos.write(sumaBytes);

                // bytes 12-21: data, 10 chars ASCII, paddat cu spatii
                String dataPadded = String.format("%-10s", date[i]);
                dos.write(dataPadded.getBytes("ASCII"));

                // byte 22: tip (0=CREDIT, 1=DEBIT)
                dos.write(tipuri[i] == TipTranzactie.CREDIT ? 0 : 1);

                // byte 23: status (0=PENDING implicit)
                dos.write(0);

                // bytes 24-31: padding zerouri
                dos.write(new byte[8]);
            }
        }

        // 3. Procesare comenzi cu RandomAccessFile
        try (RandomAccessFile raf = new RandomAccessFile(OUTPUT_FILE, "rw")) {
            while (sc.hasNextLine()) {
                String linie = sc.nextLine().trim();
                if (linie.isEmpty()) continue;

                if (linie.startsWith("READ ")) {
                    int idx = Integer.parseInt(linie.substring(5).trim());
                    System.out.println(readRecord(raf, idx));

                } else if (linie.startsWith("UPDATE ")) {
                    String[] parts = linie.split("\\s+");
                    int idx = Integer.parseInt(parts[1]);
                    String statusStr = parts[2];
                    byte statusByte = statusToByte(statusStr);

                    raf.seek((long) idx * RECORD_SIZE + 23);
                    raf.write(statusByte);

                    System.out.println("Updated [" + idx + "]: " + statusStr);

                } else if (linie.equals("PRINT_ALL")) {
                    long numRecords = raf.length() / RECORD_SIZE;
                    for (int i = 0; i < numRecords; i++) {
                        System.out.println(readRecord(raf, i));
                    }
                }
            }
        }
    }

    private static String readRecord(RandomAccessFile raf, int idx) throws IOException {
        raf.seek((long) idx * RECORD_SIZE);
        byte[] buf = new byte[RECORD_SIZE];
        raf.readFully(buf);

        ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);

        int id = bb.getInt();                          // bytes 0-3
        double suma = bb.getDouble();                  // bytes 4-11
        byte[] dataBytes = new byte[10];
        bb.get(dataBytes);                             // bytes 12-21
        String data = new String(dataBytes, "ASCII").trim();
        byte tipByte = bb.get();                       // byte 22
        byte statusByte = bb.get();                    // byte 23

        String tip = tipByte == 0 ? "CREDIT" : "DEBIT";
        String status = byteToStatus(statusByte);

        return String.format(Locale.US,
                "[%d] id=%d data=%s tip=%s suma=%.2f RON status=%s",
                idx, id, data, tip, suma, status);
    }

    private static byte statusToByte(String status) {
        return switch (status) {
            case "PENDING" -> 0;
            case "PROCESSED" -> 1;
            case "REJECTED" -> 2;
            default -> throw new IllegalArgumentException("Status necunoscut: " + status);
        };
    }

    private static String byteToStatus(byte b) {
        return switch (b) {
            case 0 -> "PENDING";
            case 1 -> "PROCESSED";
            case 2 -> "REJECTED";
            default -> "UNKNOWN";
        };
    }
}