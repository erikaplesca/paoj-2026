package com.pao.proiect.magazin.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Aprovizionare {
    private final int id;
    private final Furnizor furnizor;
    private final Produs produs;
    private final int cantitate;
    private final LocalDateTime data;

    public Aprovizionare(int id, Furnizor furnizor, Produs produs, int cantitate, LocalDateTime data) {
        if (id <= 0) throw new IllegalArgumentException("Id-ul aprovizionarii trebuie sa fie pozitiv.");
        if (cantitate <= 0) throw new IllegalArgumentException("Cantitatea trebuie sa fie pozitiva.");
        this.id = id;
        this.furnizor = Objects.requireNonNull(furnizor, "Furnizorul nu poate fi null.");
        this.produs = Objects.requireNonNull(produs, "Produsul nu poate fi null.");
        this.cantitate = cantitate;
        this.data = Objects.requireNonNull(data, "Data nu poate fi null.");
    }

    public int getId() {
        return id;
    }

    public Furnizor getFurnizor() {
        return furnizor;
    }

    public Produs getProdus() {
        return produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public LocalDateTime getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Aprovizionare{id=" + id
                + ", furnizor='" + furnizor.getNumeCompanie()
                + "', produs='" + produs.getNume()
                + "', cantitate=" + cantitate
                + ", data=" + data + "}";
    }
}
