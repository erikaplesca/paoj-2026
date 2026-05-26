package com.pao.proiect.magazin.service;

import com.pao.proiect.magazin.exception.EntitateInexistentaException;
import com.pao.proiect.magazin.exception.StocInsuficientException;
import com.pao.proiect.magazin.model.Client;
import com.pao.proiect.magazin.model.CodProdus;
import com.pao.proiect.magazin.model.Comanda;
import com.pao.proiect.magazin.model.LinieComanda;
import com.pao.proiect.magazin.model.Produs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ComandaService {
    private static ComandaService instance;

    private final List<Comanda> toate = new ArrayList<>();
    private final Map<String, List<Comanda>> dupaCnpClient = new HashMap<>();
    private int nextId = 1;

    private ComandaService() {}

    public static synchronized ComandaService getInstance() {
        if (instance == null) {
            instance = new ComandaService();
        }
        return instance;
    }

    public Comanda plaseazaComanda(Client client, Map<CodProdus, Integer> articole) {
        Objects.requireNonNull(client, "Clientul nu poate fi null.");
        Objects.requireNonNull(articole, "Articolele nu pot fi null.");
        if (articole.isEmpty()) {
            throw new IllegalArgumentException("Comanda nu poate fi goala.");
        }

        ProdusService produsService = ProdusService.getInstance();

        for (Map.Entry<CodProdus, Integer> e : articole.entrySet()) {
            Produs p = produsService.cautaDupaCod(e.getKey());
            int cerut = e.getValue();
            if (cerut <= 0) {
                throw new IllegalArgumentException("Cantitatea trebuie sa fie pozitiva pentru produsul "
                        + p.getNume());
            }
            if (p.getStoc() < cerut) {
                throw new StocInsuficientException(p.getNume(), cerut, p.getStoc());
            }
        }

        List<LinieComanda> linii = new ArrayList<>();
        for (Map.Entry<CodProdus, Integer> e : articole.entrySet()) {
            Produs p = produsService.cautaDupaCod(e.getKey());
            int cerut = e.getValue();
            p.scadeStoc(cerut);
            linii.add(new LinieComanda(p, cerut, p.getPret()));
        }

        Comanda comanda = new Comanda(nextId++, client, LocalDateTime.now(), linii);
        toate.add(comanda);
        dupaCnpClient.computeIfAbsent(client.getCnp(), k -> new ArrayList<>()).add(comanda);
        AuditService.getInstance().logAction("plaseaza_comanda");
        return comanda;
    }

    public Comanda cautaDupaId(int id) {
        for (Comanda c : toate) {
            if (c.getId() == id) return c;
        }
        throw new EntitateInexistentaException("Comanda", String.valueOf(id));
    }

    public void sterge(int id) {
        Comanda c = cautaDupaId(id);
        toate.remove(c);
        List<Comanda> alleClient = dupaCnpClient.get(c.getClient().getCnp());
        if (alleClient != null) {
            alleClient.remove(c);
        }
    }

    public List<Comanda> listeazaToate() {
        return new ArrayList<>(toate);
    }

    public List<Comanda> istoricClient(String cnp) {
        Objects.requireNonNull(cnp, "CNP-ul nu poate fi null.");
        AuditService.getInstance().logAction("istoric_comenzi_client");
        return new ArrayList<>(dupaCnpClient.getOrDefault(cnp, new ArrayList<>()));
    }

    public Map<Produs, Integer> topProduseVandute() {
        Map<Produs, Integer> totaluri = new HashMap<>();
        for (Comanda c : toate) {
            for (LinieComanda l : c.getLinii()) {
                totaluri.merge(l.getProdus(), l.getCantitate(), Integer::sum);
            }
        }
        List<Map.Entry<Produs, Integer>> intrari = new ArrayList<>(totaluri.entrySet());
        intrari.sort(Comparator.<Map.Entry<Produs, Integer>>comparingInt(Map.Entry::getValue).reversed()
                .thenComparing(e -> e.getKey().getNume(), String.CASE_INSENSITIVE_ORDER));

        Map<Produs, Integer> sortat = new LinkedHashMap<>();
        for (Map.Entry<Produs, Integer> e : intrari) {
            sortat.put(e.getKey(), e.getValue());
        }
        AuditService.getInstance().logAction("top_produse_vandute");
        return sortat;
    }
}
