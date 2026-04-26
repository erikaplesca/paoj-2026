package com.pao.proiect.magazin.model;

public class Angajat extends Persoana {
    protected int idAngajat;
    protected double salariu;

    public Angajat(String nume, String cnp, int idAngajat, double salariu) {
        super(nume, cnp);
        if (idAngajat <= 0) {
            throw new IllegalArgumentException("idAngajat trebuie sa fie pozitiv.");
        }
        if (salariu < 0) {
            throw new IllegalArgumentException("Salariul nu poate fi negativ.");
        }
        this.idAngajat = idAngajat;
        this.salariu = salariu;
    }

    @Override
    public String getRol() {
        return "Angajat";
    }

    public int getIdAngajat() {
        return idAngajat;
    }

    public double getSalariu() {
        return salariu;
    }

    public void setSalariu(double salariu) {
        if (salariu < 0) throw new IllegalArgumentException("Salariul nu poate fi negativ.");
        this.salariu = salariu;
    }

    @Override
    public String toString() {
        return getRol() + "{id=" + idAngajat + ", nume='" + nume
                + "', cnp='" + cnp + "', salariu=" + salariu + "}";
    }
}
