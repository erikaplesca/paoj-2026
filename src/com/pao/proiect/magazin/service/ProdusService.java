package com.pao.proiect.magazin.service;

import com.pao.proiect.magazin.exception.EntitateInexistentaException;
import com.pao.proiect.magazin.model.CodProdus;
import com.pao.proiect.magazin.model.Produs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class ProdusService {
    private static ProdusService instance;

    private final Map<CodProdus, Produs> indexCod = new HashMap<>();
    private final TreeSet<Produs> sortateNume = new TreeSet<>();

    private ProdusService() {}

    public static synchronized ProdusService getInstance() {
        if (instance == null) {
            instance = new ProdusService();
        }
        return instance;
    }

    public void adauga(Produs produs) {
        Objects.requireNonNull(produs, "Produsul nu poate fi null.");
        if (indexCod.containsKey(produs.getCod())) {
            throw new IllegalStateException("Exista deja un produs cu codul: " + produs.getCod());
        }
        indexCod.put(produs.getCod(), produs);
        sortateNume.add(produs);
    }

    public void sterge(CodProdus cod) {
        Objects.requireNonNull(cod, "Codul nu poate fi null.");
        Produs p = indexCod.remove(cod);
        if (p == null) {
            throw new EntitateInexistentaException("Produs", cod.toString());
        }
        sortateNume.remove(p);
    }

    public Produs cautaDupaCod(CodProdus cod) {
        Objects.requireNonNull(cod, "Codul nu poate fi null.");
        Produs p = indexCod.get(cod);
        if (p == null) {
            throw new EntitateInexistentaException("Produs", cod.toString());
        }
        return p;
    }

    public List<Produs> listeazaToate() {
        return new ArrayList<>(sortateNume);
    }

    public List<Produs> dupaCategorie(String numeCategorie) {
        Objects.requireNonNull(numeCategorie, "Numele categoriei nu poate fi null.");
        List<Produs> rezultat = new ArrayList<>();
        for (Produs p : sortateNume) {
            if (p.getCategorie().getNume().equalsIgnoreCase(numeCategorie)) {
                rezultat.add(p);
            }
        }
        return rezultat;
    }

    public List<Produs> stocSubPrag(int prag) {
        List<Produs> rezultat = new ArrayList<>();
        for (Produs p : sortateNume) {
            if (p.getStoc() < prag) {
                rezultat.add(p);
            }
        }
        return rezultat;
    }

    public Collection<Produs> toateValorile() {
        return sortateNume;
    }
}
