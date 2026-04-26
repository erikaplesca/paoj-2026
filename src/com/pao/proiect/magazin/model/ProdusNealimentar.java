package com.pao.proiect.magazin.model;

public class ProdusNealimentar extends Produs {
    private static final double TVA_NEALIMENTAR = 0.19;

    private int garantieLuni;

    public ProdusNealimentar(CodProdus cod, String nume, double pret, int stoc,
                             Categorie categorie, int garantieLuni) {
        super(cod, nume, pret, stoc, categorie);
        if (garantieLuni < 0) {
            throw new IllegalArgumentException("Garantia nu poate fi negativa.");
        }
        this.garantieLuni = garantieLuni;
    }

    @Override
    public double calculeazaTaxa() {
        return pret * TVA_NEALIMENTAR;
    }

    public int getGarantieLuni() {
        return garantieLuni;
    }

    public void setGarantieLuni(int garantieLuni) {
        if (garantieLuni < 0) throw new IllegalArgumentException("Garantia nu poate fi negativa.");
        this.garantieLuni = garantieLuni;
    }

    @Override
    public String toString() {
        return "ProdusNealimentar{cod=" + cod + ", nume='" + nume
                + "', pret=" + pret + ", stoc=" + stoc
                + ", categorie='" + categorie.getNume()
                + "', garantie=" + garantieLuni + " luni}";
    }
}
