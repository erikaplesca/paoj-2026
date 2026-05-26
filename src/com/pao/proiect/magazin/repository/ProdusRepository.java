package com.pao.proiect.magazin.repository;

import com.pao.proiect.magazin.model.Categorie;
import com.pao.proiect.magazin.model.CodProdus;
import com.pao.proiect.magazin.model.Produs;
import com.pao.proiect.magazin.model.ProdusAlimentar;
import com.pao.proiect.magazin.model.ProdusNealimentar;
import com.pao.proiect.magazin.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ID-ul folosit este reprezentarea String a CodProdus (ex. "ALIM-001").
 * Coloana 'tip' (ALIMENTAR/NEALIMENTAR) actioneaza ca discriminator pentru ierarhia Produs.
 */
public class ProdusRepository implements Repository<Produs, String> {

    @Override
    public void save(Produs produs) {
        String sql = "INSERT INTO produse (cod_prefix, cod_serial, nume, pret, stoc, tip, " +
                "id_categorie, data_expirare, garantie_luni) " +
                "VALUES (?, ?, ?, ?, ?, ?, (SELECT id FROM categorii WHERE nume = ?), ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1-5: Atributele de bază
            ps.setString(1, produs.getCod().getPrefixCategorie());
            ps.setInt(2, produs.getCod().getSerial());
            ps.setString(3, produs.getNume());
            ps.setDouble(4, produs.getPret());
            ps.setInt(5, produs.getStoc());

            // 6: Rezolvarea erorii NOT NULL constraint pentru 'tip'
            if (produs instanceof ProdusAlimentar) {
                ps.setString(6, "ALIMENTAR");
            } else if (produs instanceof ProdusNealimentar) {
                ps.setString(6, "NEALIMENTAR");
            } else {
                ps.setString(6, "NECUNOSCUT");
            }

            // 7: Categoria pentru sub-query
            ps.setString(7, produs.getCategorie().getNume());

            // 8-9: Tratarea atributelor specifice fiecărui tip
            if (produs instanceof ProdusAlimentar) {
                ProdusAlimentar pa = (ProdusAlimentar) produs;
                // Salvăm data ca text pentru a nu mai avea erori de Parse
                ps.setString(8, pa.getDataExpirare().toString());
                ps.setNull(9, java.sql.Types.INTEGER);
            } else if (produs instanceof ProdusNealimentar) {
                ProdusNealimentar pn = (ProdusNealimentar) produs;
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setInt(9, pn.getGarantieLuni());
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea produsului '" + produs.getCod() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Produs> findById(String codString) {
        String[] parts = codString.split("-");
        String prefix = parts[0];
        int serial = Integer.parseInt(parts[1]);

        String sql = "SELECT p.*, c.nume AS cat_nume, c.descriere AS cat_desc " +
                "FROM produse p " +
                "LEFT JOIN categorii c ON p.id_categorie = c.id " +
                "WHERE p.cod_prefix = ? AND p.cod_serial = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prefix);
            ps.setInt(2, serial);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Refolosim metoda mapRowToProdus pe care am reparat-o anterior!
                    return Optional.of(mapRowToProdus(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea produsului '" + codString + "': " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Produs produs) {
        String sql = "UPDATE produse SET nume = ?, pret = ?, stoc = ? WHERE cod_prefix = ? AND cod_serial = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produs.getNume());
            ps.setDouble(2, produs.getPret());
            ps.setInt(3, produs.getStoc());
            ps.setString(4, produs.getCod().getPrefixCategorie());
            ps.setInt(5, produs.getCod().getSerial());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea produsului: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String codString) {
        String[] parts = codString.split("-");
        String prefix = parts[0];
        int serial = Integer.parseInt(parts[1]);

        String sql = "DELETE FROM produse WHERE cod_prefix = ? AND cod_serial = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prefix);
            ps.setInt(2, serial);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Eroare la stergerea produsului '" + codString + "': " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produs> findAll() {
        String sql = "SELECT p.cod_prefix, p.cod_serial, p.nume, p.pret, p.stoc, p.tip, " +
                     "p.data_expirare, p.garantie_luni, c.nume AS cat_nume, c.descriere AS cat_desc " +
                     "FROM produse p " +
                     "LEFT JOIN categorii c ON p.id_categorie = c.id " +
                     "ORDER BY p.nume";
        List<Produs> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRowToProdus(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la listarea produselor: " + e.getMessage(), e);
        }
        return result;
    }

    private Produs mapRowToProdus(ResultSet rs) throws SQLException {
        CodProdus cod = new CodProdus(rs.getString("cod_prefix"), rs.getInt("cod_serial"));
        String tip = rs.getString("tip");
        Categorie categorie = new Categorie(rs.getString("cat_nume"), rs.getString("cat_desc"));

        if ("ALIMENTAR".equals(tip)) {
            // REZOLVAREA ERORII: Citim ca String, pentru a evita parsing-ul defectuos al driverului SQLite
            String dataStr = rs.getString("data_expirare");
            java.time.LocalDate dataExpirare;

            if (dataStr != null) {
                try {
                    // Dacă e salvat format string (ex: "2026-05-30")
                    dataExpirare = java.time.LocalDate.parse(dataStr);
                } catch (Exception e) {
                    // Dacă e salvat ca epoch (ex: 1781470800000)
                    long epoch = Long.parseLong(dataStr);
                    dataExpirare = java.time.Instant.ofEpochMilli(epoch)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate();
                }
            } else {
                dataExpirare = java.time.LocalDate.now().plusYears(1);
            }

            return new ProdusAlimentar(cod, rs.getString("nume"), rs.getDouble("pret"),
                    rs.getInt("stoc"), categorie, dataExpirare);
        } else {
            return new ProdusNealimentar(cod, rs.getString("nume"), rs.getDouble("pret"),
                    rs.getInt("stoc"), categorie, rs.getInt("garantie_luni"));
        }
    }
}
