package com.pao.proiect.magazin.service;

import com.pao.proiect.magazin.exception.EntitateInexistentaException;
import com.pao.proiect.magazin.model.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClientService {
    private static ClientService instance;

    private final Map<String, Client> dupaCnp = new HashMap<>();

    private ClientService() {}

    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    public void adauga(Client client) {
        Objects.requireNonNull(client, "Clientul nu poate fi null.");
        if (dupaCnp.containsKey(client.getCnp())) {
            throw new IllegalStateException("Exista deja un client cu CNP-ul: " + client.getCnp());
        }
        dupaCnp.put(client.getCnp(), client);
    }

    public void sterge(String cnp) {
        Objects.requireNonNull(cnp, "CNP-ul nu poate fi null.");
        if (!dupaCnp.containsKey(cnp)) {
            throw new EntitateInexistentaException("Client", cnp);
        }
        dupaCnp.remove(cnp);
    }

    public Client cautaDupaCnp(String cnp) {
        Objects.requireNonNull(cnp, "CNP-ul nu poate fi null.");
        Client c = dupaCnp.get(cnp);
        if (c == null) {
            throw new EntitateInexistentaException("Client", cnp);
        }
        return c;
    }

    public List<Client> listeazaToate() {
        return new ArrayList<>(dupaCnp.values());
    }
}
