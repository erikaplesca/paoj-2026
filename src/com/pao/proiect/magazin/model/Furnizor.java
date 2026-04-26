package com.pao.proiect.magazin.model;

import java.util.Objects;

public class Furnizor extends Persoana {
    private String numeCompanie;
    private String cui;

    public Furnizor(String numeReprezentant, String cnp, String numeCompanie, String cui) {
        super(numeReprezentant, cnp);
        this.numeCompanie = Objects.requireNonNull(numeCompanie, "Numele companiei nu poate fi null.");
        this.cui = Objects.requireNonNull(cui, "CUI-ul nu poate fi null.");
    }

    @Override
    public String getRol() {
        return "Furnizor";
    }

    public String getNumeCompanie() {
        return numeCompanie;
    }

    public void setNumeCompanie(String numeCompanie) {
        this.numeCompanie = Objects.requireNonNull(numeCompanie);
    }

    public String getCui() {
        return cui;
    }

    @Override
    public String toString() {
        return "Furnizor{companie='" + numeCompanie + "', cui='" + cui
                + "', reprezentant='" + nume + "'}";
    }
}
