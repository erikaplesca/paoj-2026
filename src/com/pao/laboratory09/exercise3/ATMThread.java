package com.pao.laboratory09.exercise3;

import com.pao.laboratory09.exercise1.Tranzactie;
import com.pao.laboratory09.exercise1.TipTranzactie;

public class ATMThread extends Thread {
    private final int atmId;
    private final CoadaTranzactii coada;
    private static int contor = 1;

    public ATMThread(int atmId, CoadaTranzactii coada) {
        this.atmId = atmId;
        this.coada = coada;
    }

    public static synchronized void resetContor() {
        contor = 1;
    }

    @Override
    public void run() {
        for (int i = 0; i < 4; i++) {
            int id;
            synchronized (ATMThread.class) {
                id = contor++;
            }
            double suma = 100.0 * id;
            Tranzactie t = new Tranzactie(id, suma, "2024-01-15",
                    "RO0" + atmId + "SRC", "RO0" + atmId + "DST",
                    id % 2 == 0 ? TipTranzactie.DEBIT : TipTranzactie.CREDIT);

            System.out.printf("[ATM-%d] trimite: Tranzactie #%d %.2f RON%n", atmId, id, suma);
            try {
                coada.adauga(t, atmId);
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}