package com.pao.proiect.magazin.repository;

import com.pao.proiect.magazin.model.Categorie;
import com.pao.proiect.magazin.model.Client;
import com.pao.proiect.magazin.model.CodProdus;
import com.pao.proiect.magazin.model.Comanda;
import com.pao.proiect.magazin.model.LinieComanda;
import com.pao.proiect.magazin.model.Produs;
import com.pao.proiect.magazin.model.ProdusAlimentar;
import com.pao.proiect.magazin.model.ProdusNealimentar;
import com.pao.proiect.magazin.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ComandaRepository implements Repository<Comanda, Integer> {

    /**
     * Salveaza o comanda in DB folosind o tranzactie JDBC explicita:
     * 1. INSERT INTO comenzi
     * 2. INSERT INTO linii_comanda (pentru fiecare linie)
     * 3. UPDATE produse SET stoc = stoc - cantitate (pentru fiecare produs)
     * La eroare: ROLLBACK garanteaza ca nu raman date partiale in DB.
     */
    @Override
    public void save(Comanda comanda) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                long idComandaDb = insertComanda(conn, comanda);
                for (LinieComanda linie : comanda.getLinii()) {
                    insertLinieComanda(conn, idComandaDb, linie);
                    updateStocProdus(conn, linie.getProdus(), linie.getCantitate());
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Eroare la salvarea comenzii #" + comanda.getId()
                        + " — rollback efectuat: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare conexiune la salvarea comenzii: " + e.getMessage(), e);
        }
    }

    private long insertComanda(Connection conn, Comanda comanda) throws SQLException {
        String sql = "INSERT INTO comenzi (id_client, data_comanda, total) " +
                "VALUES ((SELECT id FROM clienti WHERE cnp = ?), ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, comanda.getClient().getCnp());
            // CORECTURĂ: Salvăm LocalDateTime ca String
            ps.setString(2, comanda.getData().toString());
            ps.setDouble(3, comanda.getTotal());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("Nu s-a generat cheie pentru comanda.");
            }
        }
    }

    private void insertLinieComanda(Connection conn, long idComanda, LinieComanda linie) throws SQLException {
        String sql = "INSERT INTO linii_comanda (id_comanda, id_produs, cantitate, pret_unitar) " +
                     "VALUES (?, (SELECT id FROM produse WHERE cod_prefix = ? AND cod_serial = ?), ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idComanda);
            ps.setString(2, linie.getProdus().getCod().getPrefixCategorie());
            ps.setInt(3, linie.getProdus().getCod().getSerial());
            ps.setInt(4, linie.getCantitate());
            ps.setDouble(5, linie.getPretUnitar());
            ps.executeUpdate();
        }
    }

    private void updateStocProdus(Connection conn, Produs produs, int cantitate) throws SQLException {
        String sql = "UPDATE produse SET stoc = stoc - ? WHERE cod_prefix = ? AND cod_serial = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantitate);
            ps.setString(2, produs.getCod().getPrefixCategorie());
            ps.setInt(3, produs.getCod().getSerial());
            ps.executeUpdate();
        }
    }

    /**
     * JOIN 1: comenzi cu datele complete ale clientului.
     */
    @Override
    public List<Comanda> findAll() {
        String sql = "SELECT c.id, c.data_comanda, c.total, " +
                     "cl.nume AS cl_nume, cl.cnp AS cl_cnp, cl.email, cl.telefon " +
                     "FROM comenzi c " +
                     "JOIN clienti cl ON c.id_client = cl.id " +
                     "ORDER BY c.id";
        List<Comanda> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idComanda = rs.getInt("id");
                Client client = new Client(
                        rs.getString("cl_nume"), rs.getString("cl_cnp"),
                        rs.getString("email"), rs.getString("telefon"));
                // Extragem data ca string și o parsăm
                String dataStr = rs.getString("data_comanda");
                LocalDateTime data = LocalDateTime.parse(dataStr);
                List<LinieComanda> linii = findLiniiPentruComanda(conn, idComanda);
                result.add(new Comanda(idComanda, client, data, linii));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la listarea comenzilor: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Optional<Comanda> findById(Integer id) {
        String sql = "SELECT c.id, c.data_comanda, c.total, " +
                     "cl.nume AS cl_nume, cl.cnp AS cl_cnp, cl.email, cl.telefon " +
                     "FROM comenzi c " +
                     "JOIN clienti cl ON c.id_client = cl.id " +
                     "WHERE c.id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client(
                            rs.getString("cl_nume"), rs.getString("cl_cnp"),
                            rs.getString("email"), rs.getString("telefon"));
                    String dataStr = rs.getString("data_comanda");
                    LocalDateTime data = LocalDateTime.parse(dataStr);
                    List<LinieComanda> linii = findLiniiPentruComanda(conn, id);
                    return Optional.of(new Comanda(id, client, data, linii));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea comenzii #" + id + ": " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Comanda comanda) {
        String sql = "UPDATE comenzi SET total = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, comanda.getTotal());
            ps.setInt(2, comanda.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea comenzii #" + comanda.getId() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM linii_comanda WHERE id_comanda = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM comenzi WHERE id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Eroare la stergerea comenzii #" + id + ": " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare conexiune la stergerea comenzii: " + e.getMessage(), e);
        }
    }

    /**
     * JOIN 2: top produse vandute cu numele categoriei.
     * Returneaza: [numeProdus, numeCategorie, totalVandut]
     */
    public List<String[]> findTopProduseVanduteCuCategorie() {
        String sql = "SELECT p.nume AS produs, cat.nume AS categorie, " +
                     "SUM(lc.cantitate) AS total_vandut " +
                     "FROM linii_comanda lc " +
                     "JOIN produse p ON lc.id_produs = p.id " +
                     "JOIN categorii cat ON p.id_categorie = cat.id " +
                     "GROUP BY p.id, p.nume, cat.nume " +
                     "ORDER BY total_vandut DESC";
        List<String[]> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new String[]{
                        rs.getString("produs"),
                        rs.getString("categorie"),
                        String.valueOf(rs.getInt("total_vandut"))
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la top produse vandute: " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * JOIN 3: toate liniile de comanda cu detalii produs si comanda.
     * Returneaza: [idComanda, dataComanda, numeProdus, cod, cantitate, pretUnitar]
     */
    public List<String[]> findLiniiCuDetalii() {
        String sql = "SELECT c.id AS id_comanda, c.data_comanda, " +
                     "p.nume AS produs, p.cod_prefix, p.cod_serial, " +
                     "lc.cantitate, lc.pret_unitar " +
                     "FROM linii_comanda lc " +
                     "JOIN comenzi c ON lc.id_comanda = c.id " +
                     "JOIN produse p ON lc.id_produs = p.id " +
                     "ORDER BY c.id, p.nume";
        List<String[]> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new String[]{
                        String.valueOf(rs.getInt("id_comanda")),
                        rs.getTimestamp("data_comanda").toString(),
                        rs.getString("produs"),
                        rs.getString("cod_prefix") + "-" + String.format("%03d", rs.getInt("cod_serial")),
                        String.valueOf(rs.getInt("cantitate")),
                        String.valueOf(rs.getDouble("pret_unitar"))
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la listarea liniilor de comanda: " + e.getMessage(), e);
        }
        return result;
    }

    private List<LinieComanda> findLiniiPentruComanda(Connection conn, int idComanda) throws SQLException {
        String sql = "SELECT lc.cantitate, lc.pret_unitar, " +
                     "p.cod_prefix, p.cod_serial, p.nume, p.pret, p.stoc, p.tip, " +
                     "p.data_expirare, p.garantie_luni, " +
                     "c.nume AS cat_nume, c.descriere AS cat_desc " +
                     "FROM linii_comanda lc " +
                     "JOIN produse p ON lc.id_produs = p.id " +
                     "LEFT JOIN categorii c ON p.id_categorie = c.id " +
                     "WHERE lc.id_comanda = ?";
        List<LinieComanda> linii = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idComanda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produs produs = mapRowToProdus(rs);
                    linii.add(new LinieComanda(produs, rs.getInt("cantitate"), rs.getDouble("pret_unitar")));
                }
            }
        }
        return linii;
    }

    private Produs mapRowToProdus(ResultSet rs) throws SQLException {
        CodProdus cod = new CodProdus(rs.getString("cod_prefix"), rs.getInt("cod_serial"));
        String tip = rs.getString("tip");
        Categorie categorie = new Categorie(rs.getString("cat_nume"), rs.getString("cat_desc"));

        if ("ALIMENTAR".equals(tip)) {
            // Citire sigură ca String pentru SQLite
            String dataStr = rs.getString("data_expirare");
            LocalDate dataExpirare;

            if (dataStr != null) {
                try {
                    dataExpirare = LocalDate.parse(dataStr);
                } catch (Exception e) {
                    long epoch = Long.parseLong(dataStr);
                    dataExpirare = java.time.Instant.ofEpochMilli(epoch)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate();
                }
            } else {
                dataExpirare = LocalDate.now().plusYears(1);
            }

            return new ProdusAlimentar(cod, rs.getString("nume"), rs.getDouble("pret"),
                    rs.getInt("stoc"), categorie, dataExpirare);
        } else {
            return new ProdusNealimentar(cod, rs.getString("nume"), rs.getDouble("pret"),
                    rs.getInt("stoc"), categorie, rs.getInt("garantie_luni"));
        }
    }
}
