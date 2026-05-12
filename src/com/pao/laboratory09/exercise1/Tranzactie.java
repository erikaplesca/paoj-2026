package com.pao.laboratory09.exercise1;

import java.io.Serializable;
import java.util.Locale;

public class Tranzactie implements Serializable {
    private static final long serialVersionUID = 1L;

    public int id;
    public double suma;
    public String data;
    String contSursa;
    String contDestinatie;
    TipTranzactie tip;
    transient String note;

    public Tranzactie(int id, double suma, String data,
                      String contSursa, String contDestinatie, TipTranzactie tip) {
        this.id = id;
        this.suma = suma;
        this.data = data;
        this.contSursa = contSursa;
        this.contDestinatie = contDestinatie;
        this.tip = tip;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[%d] %s %s: %.2f RON | %s -> %s",
                id, data, tip, suma, contSursa, contDestinatie);
    }
}