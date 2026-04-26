package com.pao.proiect.magazin.model;

import java.util.Objects;

public class Categorie {
    private String nume;
    private String descriere;

    public Categorie(String nume, String descriere) {
        this.nume = Objects.requireNonNull(nume, "Numele categoriei nu poate fi null.");
        this.descriere = descriere == null ? "" : descriere;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = Objects.requireNonNull(nume);
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere == null ? "" : descriere;
    }

    @Override
    public String toString() {
        return "Categorie{nume='" + nume + "', descriere='" + descriere + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categorie categorie = (Categorie) o;
        return Objects.equals(nume, categorie.nume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nume);
    }
}
