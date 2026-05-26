package com.pao.proiect.magazin.service;

import com.pao.proiect.magazin.exception.EntitateInexistentaException;
import com.pao.proiect.magazin.model.Categorie;
import com.pao.proiect.magazin.repository.CategorieRepository;

import java.util.List;
import java.util.Objects;

public class CategorieService {
    private static CategorieService instance;

    // Injectare Repository
    private final CategorieRepository categorieRepository = new CategorieRepository();

    private CategorieService() {}

    public static synchronized CategorieService getInstance() {
        if (instance == null) {
            instance = new CategorieService();
        }
        return instance;
    }

    public void adauga(Categorie categorie) {
        Objects.requireNonNull(categorie, "Categoria nu poate fi null.");

        // Validare existență în DB
        if (categorieRepository.findById(categorie.getNume()).isPresent()) {
            throw new IllegalStateException("Exista deja o categorie cu numele: " + categorie.getNume());
        }

        categorieRepository.save(categorie);
        AuditService.getInstance().logAction("adauga_categorie");
    }

    public void sterge(String nume) {
        Objects.requireNonNull(nume, "Numele categoriei nu poate fi null.");

        if (categorieRepository.findById(nume).isEmpty()) {
            throw new EntitateInexistentaException("Categorie", nume);
        }

        categorieRepository.delete(nume);
        AuditService.getInstance().logAction("sterge_categorie");
    }

    public Categorie cautaDupaNume(String nume) {
        Objects.requireNonNull(nume, "Numele categoriei nu poate fi null.");

        return categorieRepository.findById(nume)
                .orElseThrow(() -> new EntitateInexistentaException("Categorie", nume));
    }

    public List<Categorie> listeazaToate() {
        return categorieRepository.findAll();
    }
}