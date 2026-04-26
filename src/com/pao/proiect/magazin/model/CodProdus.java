package com.pao.proiect.magazin.model;

import java.util.Objects;

public final class CodProdus {
    private final String prefixCategorie;
    private final int serial;

    public CodProdus(String prefixCategorie, int serial) {
        if (prefixCategorie == null || prefixCategorie.isBlank()) {
            throw new IllegalArgumentException("Prefix categorie nu poate fi null sau gol.");
        }
        if (serial <= 0) {
            throw new IllegalArgumentException("Serial-ul trebuie sa fie strict pozitiv.");
        }
        this.prefixCategorie = prefixCategorie.toUpperCase();
        this.serial = serial;
    }

    public String getPrefixCategorie() {
        return prefixCategorie;
    }

    public int getSerial() {
        return serial;
    }

    @Override
    public String toString() {
        return prefixCategorie + "-" + String.format("%03d", serial);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodProdus that = (CodProdus) o;
        return serial == that.serial && Objects.equals(prefixCategorie, that.prefixCategorie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefixCategorie, serial);
    }
}
