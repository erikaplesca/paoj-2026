package com.pao.proiect.magazin.repository;

import com.pao.proiect.magazin.model.Client;
import com.pao.proiect.magazin.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepository implements Repository<Client, String> {

    @Override
    public void save(Client client) {
        String sql = "INSERT INTO clienti (nume, cnp, email, telefon) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, client.getNume());
            ps.setString(2, client.getCnp());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getTelefon());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea clientului '" + client.getCnp() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Client> findById(String cnp) {
        String sql = "SELECT nume, cnp, email, telefon FROM clienti WHERE cnp = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cnp);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Client(
                            rs.getString("nume"),
                            rs.getString("cnp"),
                            rs.getString("email"),
                            rs.getString("telefon")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea clientului cu CNP '" + cnp + "': " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT nume, cnp, email, telefon FROM clienti ORDER BY nume";
        List<Client> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Client(
                        rs.getString("nume"),
                        rs.getString("cnp"),
                        rs.getString("email"),
                        rs.getString("telefon")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la listarea clientilor: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void update(Client client) {
        String sql = "UPDATE clienti SET nume = ?, email = ?, telefon = ? WHERE cnp = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, client.getNume());
            ps.setString(2, client.getEmail());
            ps.setString(3, client.getTelefon());
            ps.setString(4, client.getCnp());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea clientului '" + client.getCnp() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String cnp) {
        String sql = "DELETE FROM clienti WHERE cnp = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cnp);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la stergerea clientului cu CNP '" + cnp + "': " + e.getMessage(), e);
        }
    }
}
