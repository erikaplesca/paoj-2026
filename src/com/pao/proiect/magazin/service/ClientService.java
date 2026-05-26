package com.pao.proiect.magazin.service;

import com.pao.proiect.magazin.exception.EntitateInexistentaException;
import com.pao.proiect.magazin.model.Client;
import com.pao.proiect.magazin.repository.ClientRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClientService {
    private static ClientService instance;

    // Injectăm repository-ul concret
    private final ClientRepository clientRepository = new ClientRepository();

    private ClientService() {}

    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    public void adauga(Client client) {
        Objects.requireNonNull(client, "Clientul nu poate fi null.");

        // Validare: verificăm în DB dacă mai există CNP-ul
        if (clientRepository.findById(client.getCnp()).isPresent()) {
            throw new IllegalStateException("Exista deja un client cu CNP-ul: " + client.getCnp());
        }

        // Salvare în Baza de Date
        clientRepository.save(client);
        AuditService.getInstance().logAction("inregistreaza_client");
    }

    public Client cautaDupaCnp(String cnp) {
        Objects.requireNonNull(cnp, "CNP-ul nu poate fi null.");

        // Căutare direct în DB
        return clientRepository.findById(cnp)
                .orElseThrow(() -> new EntitateInexistentaException("Client", cnp));
    }

    public List<Client> listeazaToate() {
        // Citire în timp real din DB
        return clientRepository.findAll();
    }

    public void sterge(String cnp) {
        Objects.requireNonNull(cnp, "CNP-ul nu poate fi null.");
        if (clientRepository.findById(cnp).isEmpty()) {
            throw new EntitateInexistentaException("Client", cnp);
        }
        clientRepository.delete(cnp);
    }
}