package com.pao.proiect.magazin.model;

import java.util.Objects;

public final class LinieComanda {
    private final Produs produs;
    private final int cantitate;
    private final double pretUnitar;

    public LinieComanda(Produs produs, int cantitate, double pretUnitar) {
        this.produs = Objects.requireNonNull(produs, "Produsul nu poate fi null.");
        if (cantitate <= 0) throw new IllegalArgumentException("Cantitatea trebuie sa fie pozitiva.");
        if (pretUnitar < 0) throw new IllegalArgumentException("Pretul unitar nu poate fi negativ.");
        this.cantitate = cantitate;
        this.pretUnitar = pretUnitar;
    }

    public Produs getProdus() {
        return produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public double getPretUnitar() {
        return pretUnitar;
    }

    public double getSubtotal() {
        return cantitate * pretUnitar;
    }

    @Override
    public String toString() {
        return "LinieComanda{produs='" + produs.getNume()
                + "', cantitate=" + cantitate
                + ", pretUnitar=" + pretUnitar
                + ", subtotal=" + getSubtotal() + "}";
    }
}
