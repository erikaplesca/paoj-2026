package com.pao.proiect.magazin.service;

import com.pao.proiect.magazin.exception.EntitateInexistentaException;
import com.pao.proiect.magazin.model.CodProdus;
import com.pao.proiect.magazin.model.Produs;
import com.pao.proiect.magazin.repository.ProdusRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public class ProdusService {
    private static ProdusService instance;

    // Declararea corectă a repository-ului ca atribut privat al clasei
    private final ProdusRepository produsRepository = new ProdusRepository();

    private ProdusService() {}

    public static synchronized ProdusService getInstance() {
        if (instance == null) {
            instance = new ProdusService();
        }
        return instance;
    }

    public void adauga(Produs produs) {
        Objects.requireNonNull(produs, "Produsul nu poate fi null.");

        // CORECTURĂ: Convertim CodProdus la String folosind .toString() pentru findById
        if (produsRepository.findById(produs.getCod().toString()).isPresent()) {
            throw new IllegalStateException("Exista deja un produs cu codul: " + produs.getCod());
        }

        produsRepository.save(produs);
        AuditService.getInstance().logAction("adauga_produs");
    }

    // Metodă utilizată de FurnizorService la aprovizionare pentru UPDATE în DB
    public void actualizeaza(Produs produs) {
        Objects.requireNonNull(produs, "Produsul nu poate fi null.");
        produsRepository.update(produs);
    }

    public void sterge(CodProdus cod) {
        Objects.requireNonNull(cod, "Codul nu poate fi null.");

        // CORECTURĂ: Convertim CodProdus la String folosind .toString() pentru findById
        if (produsRepository.findById(cod.toString()).isEmpty()) {
            throw new EntitateInexistentaException("Produs", cod.toString());
        }

        // CORECTURĂ: Convertim CodProdus la String folosind .toString() pentru delete
        produsRepository.delete(cod.toString());
        AuditService.getInstance().logAction("sterge_produs");
    }

    public Produs cautaDupaCod(CodProdus cod) {
        Objects.requireNonNull(cod, "Codul nu poate fi null.");

        // CORECTURĂ: Convertim CodProdus la String folosind .toString() pentru findById
        Produs p = produsRepository.findById(cod.toString())
                .orElseThrow(() -> new EntitateInexistentaException("Produs", cod.toString()));

        AuditService.getInstance().logAction("cauta_produs_dupa_cod");
        return p;
    }

    public List<Produs> listeazaToate() {
        // Preluăm datele reale din baza de date și le sortăm automat folosind interfața Comparable din clasa Produs
        List<Produs> toate = produsRepository.findAll();
        return new ArrayList<>(new TreeSet<>(toate));
    }

    public List<Produs> dupaCategorie(String numeCategorie) {
        Objects.requireNonNull(numeCategorie, "Numele categoriei nu poate fi null.");

        List<Produs> toate = produsRepository.findAll();
        List<Produs> rezultat = new ArrayList<>();

        for (Produs p : toate) {
            if (p.getCategorie().getNume().equalsIgnoreCase(numeCategorie)) {
                rezultat.add(p);
            }
        }

        AuditService.getInstance().logAction("cauta_produse_dupa_categorie");
        return new ArrayList<>(new TreeSet<>(rezultat));
    }

    public List<Produs> stocSubPrag(int prag) {
        List<Produs> toate = produsRepository.findAll();
        List<Produs> rezultat = new ArrayList<>();

        for (Produs p : toate) {
            if (p.getStoc() < prag) {
                rezultat.add(p);
            }
        }

        return new ArrayList<>(new TreeSet<>(rezultat));
    }

    public Collection<Produs> toateValorile() {
        return new TreeSet<>(produsRepository.findAll());
    }
}