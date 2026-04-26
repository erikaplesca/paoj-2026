package com.pao.proiect.magazin.model;

import java.util.Objects;

public class Client extends Persoana {
    private String email;
    private String telefon;

    public Client(String nume, String cnp, String email, String telefon) {
        super(nume, cnp);
        this.email = Objects.requireNonNull(email, "Email-ul nu poate fi null.");
        this.telefon = Objects.requireNonNull(telefon, "Telefonul nu poate fi null.");
    }

    @Override
    public String getRol() {
        return "Client";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email);
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = Objects.requireNonNull(telefon);
    }

    @Override
    public String toString() {
        return "Client{nume='" + nume + "', cnp='" + cnp
                + "', email='" + email + "', telefon='" + telefon + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(cnp, client.cnp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cnp);
    }
}
