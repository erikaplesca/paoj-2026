package com.pao.proiect.magazin.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Comanda {
    private final int id;
    private final Client client;
    private final LocalDateTime data;
    private final List<LinieComanda> linii;

    public Comanda(int id, Client client, LocalDateTime data, List<LinieComanda> linii) {
        if (id <= 0) throw new IllegalArgumentException("Id-ul comenzii trebuie sa fie pozitiv.");
        this.id = id;
        this.client = Objects.requireNonNull(client, "Clientul nu poate fi null.");
        this.data = Objects.requireNonNull(data, "Data nu poate fi null.");
        Objects.requireNonNull(linii, "Liniile nu pot fi null.");
        if (linii.isEmpty()) {
            throw new IllegalArgumentException("Comanda trebuie sa aiba cel putin o linie.");
        }
        this.linii = new ArrayList<>(linii);
    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public LocalDateTime getData() {
        return data;
    }

    public List<LinieComanda> getLinii() {
        return Collections.unmodifiableList(linii);
    }

    public double getTotal() {
        double total = 0;
        for (LinieComanda l : linii) {
            total += l.getSubtotal();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Comanda{id=").append(id)
                .append(", client='").append(client.getNume())
                .append("', data=").append(data)
                .append(", total=").append(getTotal())
                .append(", linii=[");
        for (int i = 0; i < linii.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(linii.get(i));
        }
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comanda comanda = (Comanda) o;
        return id == comanda.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
