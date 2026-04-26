package com.pao.proiect.magazin.model;

import java.util.Objects;

public abstract class Persoana {
    protected String nume;
    protected String cnp;

    protected Persoana(String nume, String cnp) {
        this.nume = Objects.requireNonNull(nume, "Numele nu poate fi null.");
        this.cnp = Objects.requireNonNull(cnp, "CNP-ul nu poate fi null.");
    }

    public abstract String getRol();

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = Objects.requireNonNull(nume);
    }

    public String getCnp() {
        return cnp;
    }

    @Override
    public String toString() {
        return getRol() + "{nume='" + nume + "', cnp='" + cnp + "'}";
    }
}
