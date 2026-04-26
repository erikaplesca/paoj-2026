package com.pao.proiect.magazin.model;

public class Manager extends Angajat {
    private String departament;

    public Manager(String nume, String cnp, int idAngajat, double salariu, String departament) {
        super(nume, cnp, idAngajat, salariu);
        if (departament == null || departament.isBlank()) {
            throw new IllegalArgumentException("Departamentul nu poate fi null sau gol.");
        }
        this.departament = departament;
    }

    @Override
    public String getRol() {
        return "Manager";
    }

    public String getDepartament() {
        return departament;
    }

    public void setDepartament(String departament) {
        if (departament == null || departament.isBlank()) {
            throw new IllegalArgumentException("Departamentul nu poate fi null sau gol.");
        }
        this.departament = departament;
    }

    @Override
    public String toString() {
        return getRol() + "{id=" + idAngajat + ", nume='" + nume
                + "', departament='" + departament + "', salariu=" + salariu + "}";
    }
}
