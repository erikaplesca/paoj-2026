package com.pao.proiect.magazin.service;

import com.pao.proiect.magazin.exception.EntitateInexistentaException;
import com.pao.proiect.magazin.model.Aprovizionare;
import com.pao.proiect.magazin.model.Furnizor;
import com.pao.proiect.magazin.model.Produs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public class FurnizorService {
    private static FurnizorService instance;

    private final Map<String, Furnizor> dupaCui = new HashMap<>();
    private final Queue<Aprovizionare> coadaAprovizionari = new ArrayDeque<>();
    private int nextAprovizionareId = 1;

    private FurnizorService() {}

    public static synchronized FurnizorService getInstance() {
        if (instance == null) {
            instance = new FurnizorService();
        }
        return instance;
    }

    public void adauga(Furnizor furnizor) {
        Objects.requireNonNull(furnizor, "Furnizorul nu poate fi null.");
        if (dupaCui.containsKey(furnizor.getCui())) {
            throw new IllegalStateException("Exista deja un furnizor cu CUI-ul: " + furnizor.getCui());
        }
        dupaCui.put(furnizor.getCui(), furnizor);
        AuditService.getInstance().logAction("inregistreaza_furnizor");
    }

    public void sterge(String cui) {
        Objects.requireNonNull(cui, "CUI-ul nu poate fi null.");
        if (!dupaCui.containsKey(cui)) {
            throw new EntitateInexistentaException("Furnizor", cui);
        }
        dupaCui.remove(cui);
    }

    public Furnizor cautaDupaCui(String cui) {
        Objects.requireNonNull(cui, "CUI-ul nu poate fi null.");
        Furnizor f = dupaCui.get(cui);
        if (f == null) {
            throw new EntitateInexistentaException("Furnizor", cui);
        }
        return f;
    }

    public List<Furnizor> listeazaToate() {
        return new ArrayList<>(dupaCui.values());
    }

    public Aprovizionare aprovizioneaza(String cuiFurnizor, Produs produs, int cantitate) {
        Furnizor furnizor = cautaDupaCui(cuiFurnizor);
        Objects.requireNonNull(produs, "Produsul nu poate fi null.");
        if (cantitate <= 0) throw new IllegalArgumentException("Cantitatea trebuie sa fie pozitiva.");

        // 1. Modificăm stocul obiectului în memorie
        produs.cresteStoc(cantitate);

        // 2. IMPORTANT: Executăm UPDATE în DB pentru stocul modificat, nu INSERT
        ProdusService.getInstance().actualizeaza(produs);

        // 3. Înregistrăm aprovizionarea în coada istorică din memorie
        Aprovizionare ap = new Aprovizionare(nextAprovizionareId++, furnizor, produs, cantitate, LocalDateTime.now());
        coadaAprovizionari.offer(ap);

        AuditService.getInstance().logAction("reaprovizioneaza_stoc");
        return ap;
    }

    public List<Aprovizionare> istoricAprovizionari() {
        return new ArrayList<>(coadaAprovizionari);
    }
}