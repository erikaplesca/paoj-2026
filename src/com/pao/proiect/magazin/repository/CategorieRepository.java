package com.pao.proiect.magazin.repository;

import com.pao.proiect.magazin.model.Categorie;
import com.pao.proiect.magazin.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategorieRepository implements Repository<Categorie, String> {

    @Override
    public void save(Categorie categorie) {
        String sql = "INSERT INTO categorii (nume, descriere) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categorie.getNume());
            ps.setString(2, categorie.getDescriere());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea categoriei '" + categorie.getNume() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Categorie> findById(String nume) {
        String sql = "SELECT nume, descriere FROM categorii WHERE nume = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nume);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Categorie(rs.getString("nume"), rs.getString("descriere")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea categoriei '" + nume + "': " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Categorie> findAll() {
        String sql = "SELECT nume, descriere FROM categorii ORDER BY nume";
        List<Categorie> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Categorie(rs.getString("nume"), rs.getString("descriere")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la listarea categoriilor: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void update(Categorie categorie) {
        String sql = "UPDATE categorii SET descriere = ? WHERE nume = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categorie.getDescriere());
            ps.setString(2, categorie.getNume());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea categoriei '" + categorie.getNume() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String nume) {
        String sql = "DELETE FROM categorii WHERE nume = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nume);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la stergerea categoriei '" + nume + "': " + e.getMessage(), e);
        }
    }
}
