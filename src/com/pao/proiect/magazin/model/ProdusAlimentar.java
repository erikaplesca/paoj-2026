package com.pao.proiect.magazin.model;

import java.time.LocalDate;
import java.util.Objects;

public class ProdusAlimentar extends Produs {
    private static final double TVA_ALIMENTAR = 0.09;

    private LocalDate dataExpirare;

    public ProdusAlimentar(CodProdus cod, String nume, double pret, int stoc,
                           Categorie categorie, LocalDate dataExpirare) {
        super(cod, nume, pret, stoc, categorie);
        this.dataExpirare = Objects.requireNonNull(dataExpirare, "Data expirarii nu poate fi null.");
    }

    @Override
    public double calculeazaTaxa() {
        return pret * TVA_ALIMENTAR;
    }

    public LocalDate getDataExpirare() {
        return dataExpirare;
    }

    public void setDataExpirare(LocalDate dataExpirare) {
        this.dataExpirare = Objects.requireNonNull(dataExpirare);
    }

    public boolean esteExpirat(LocalDate referinta) {
        return referinta.isAfter(dataExpirare);
    }

    @Override
    public String toString() {
        return "ProdusAlimentar{cod=" + cod + ", nume='" + nume
                + "', pret=" + pret + ", stoc=" + stoc
                + ", categorie='" + categorie.getNume()
                + "', expira=" + dataExpirare + "}";
    }
}
