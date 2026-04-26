package com.pao.proiect.magazin.service;

import com.pao.proiect.magazin.exception.EntitateInexistentaException;
import com.pao.proiect.magazin.model.Categorie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CategorieService {
    private static CategorieService instance;

    private final Map<String, Categorie> dupaNume = new HashMap<>();

    private CategorieService() {}

    public static synchronized CategorieService getInstance() {
        if (instance == null) {
            instance = new CategorieService();
        }
        return instance;
    }

    public void adauga(Categorie categorie) {
        Objects.requireNonNull(categorie, "Categoria nu poate fi null.");
        if (dupaNume.containsKey(categorie.getNume())) {
            throw new IllegalStateException("Exista deja o categorie cu numele: " + categorie.getNume());
        }
        dupaNume.put(categorie.getNume(), categorie);
    }

    public void sterge(String nume) {
        Objects.requireNonNull(nume, "Numele categoriei nu poate fi null.");
        if (!dupaNume.containsKey(nume)) {
            throw new EntitateInexistentaException("Categorie", nume);
        }
        dupaNume.remove(nume);
    }

    public Categorie cautaDupaNume(String nume) {
        Objects.requireNonNull(nume, "Numele categoriei nu poate fi null.");
        Categorie c = dupaNume.get(nume);
        if (c == null) {
            throw new EntitateInexistentaException("Categorie", nume);
        }
        return c;
    }

    public List<Categorie> listeazaToate() {
        return new ArrayList<>(dupaNume.values());
    }
}
