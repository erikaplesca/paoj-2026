package com.pao.proiect.magazin.model;

import java.util.Objects;

public abstract class Produs implements Comparable<Produs> {
    protected final CodProdus cod;
    protected String nume;
    protected double pret;
    protected int stoc;
    protected Categorie categorie;

    protected Produs(CodProdus cod, String nume, double pret, int stoc, Categorie categorie) {
        this.cod = Objects.requireNonNull(cod, "Codul produsului nu poate fi null.");
        this.nume = Objects.requireNonNull(nume, "Numele produsului nu poate fi null.");
        if (pret < 0) throw new IllegalArgumentException("Pretul nu poate fi negativ.");
        if (stoc < 0) throw new IllegalArgumentException("Stocul nu poate fi negativ.");
        this.pret = pret;
        this.stoc = stoc;
        this.categorie = Objects.requireNonNull(categorie, "Categoria nu poate fi null.");
    }

    public abstract double calculeazaTaxa();

    public CodProdus getCod() {
        return cod;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = Objects.requireNonNull(nume);
    }

    public double getPret() {
        return pret;
    }

    public void setPret(double pret) {
        if (pret < 0) throw new IllegalArgumentException("Pretul nu poate fi negativ.");
        this.pret = pret;
    }

    public int getStoc() {
        return stoc;
    }

    public void cresteStoc(int cantitate) {
        if (cantitate <= 0) throw new IllegalArgumentException("Cantitatea trebuie sa fie pozitiva.");
        this.stoc += cantitate;
    }

    public void scadeStoc(int cantitate) {
        if (cantitate <= 0) throw new IllegalArgumentException("Cantitatea trebuie sa fie pozitiva.");
        if (cantitate > stoc) {
            throw new IllegalStateException("Stoc insuficient pentru " + nume);
        }
        this.stoc -= cantitate;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    @Override
    public int compareTo(Produs altul) {
        int cmp = this.nume.compareToIgnoreCase(altul.nume);
        if (cmp != 0) return cmp;
        return this.cod.toString().compareTo(altul.cod.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produs)) return false;
        Produs produs = (Produs) o;
        return Objects.equals(cod, produs.cod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cod);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{cod=" + cod + ", nume='" + nume
                + "', pret=" + pret + ", stoc=" + stoc
                + ", categorie='" + categorie.getNume() + "'}";
    }
}
