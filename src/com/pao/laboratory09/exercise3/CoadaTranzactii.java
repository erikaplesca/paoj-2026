package com.pao.laboratory09.exercise3;

import com.pao.laboratory09.exercise1.Tranzactie;
import java.util.LinkedList;
import java.util.Queue;

public class CoadaTranzactii {
    private final Queue<Tranzactie> coada = new LinkedList<>();
    private final int capacitate;

    public CoadaTranzactii(int capacitate) {
        this.capacitate = capacitate;
    }

    public synchronized void adauga(Tranzactie t, int atmId) throws InterruptedException {
        while (coada.size() >= capacitate) {
            System.out.println("[ATM-" + atmId + "] astept loc...");
            wait();
        }
        coada.add(t);
        notifyAll();
    }

    public synchronized Tranzactie extrage() throws InterruptedException {
        while (coada.isEmpty()) {
            wait();
            if (coada.isEmpty()) return null;
        }
        Tranzactie t = coada.poll();
        notifyAll(); // trezeste si waitUntilEmpty()
        return t;
    }

    public synchronized void waitUntilEmpty() throws InterruptedException {
        while (!coada.isEmpty()) {
            wait();
        }
    }

    public synchronized void trezeste() {
        notifyAll();
    }
}