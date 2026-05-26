package com.pao.proiect.magazin.util;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private final Properties props = new Properties();

    private DatabaseConnection() {
        incarcaProprietati();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void incarcaProprietati() {
        // Calea corecta catre fisier exact asa cum e la tine in proiect
        Path p = Paths.get("src/com/pao/proiect/magazin/resources/db.properties");

        if (Files.exists(p)) {
            try (InputStream input = Files.newInputStream(p)) {
                props.load(input);
            } catch (Exception e) {
                throw new RuntimeException("Eroare la citirea db.properties de pe disk: " + e.getMessage(), e);
            }
        } else {
            // Cautare prin ClassLoader cu calea COMPLETA a pachetului
            try (InputStream in = DatabaseConnection.class
                    .getClassLoader()
                    .getResourceAsStream("com/pao/proiect/magazin/resources/db.properties")) {
                if (in == null) {
                    throw new RuntimeException("Eroare critica: Nu gasesc db.properties nicaieri!");
                }
                props.load(in);
            } catch (Exception e) {
                throw new RuntimeException("Eroare la incarcarea db.properties prin ClassLoader: " + e.getMessage(), e);
            }
        }
    }

    public Connection getConnection() {
        try {
            String url = props.getProperty("db.url");

            // Asiguram incarcarea driverului de SQLite
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection(url);

            // IMPORTANT pentru SQLite: activam cheile straine
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
            }

            return connection;
        } catch (Exception e) {
            throw new RuntimeException("Eroare la deschiderea unei conexiuni noi", e);
        }
    }

    public void initSchema() {
        // Cautare cu calea COMPLETA catre schema.sql
        try (InputStream in = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("com/pao/proiect/magazin/resources/schema.sql")) {

            if (in == null) {
                Path pSchema = Paths.get("src/com/pao/proiect/magazin/resources/schema.sql");
                if (Files.exists(pSchema)) {
                    String sql = Files.readString(pSchema);
                    executaScript(sql);
                    return;
                }
                throw new RuntimeException("Nu gasesc schema.sql pe classpath sau pe disk");
            }

            String sql = new String(in.readAllBytes());
            executaScript(sql);

        } catch (Exception e) {
            throw new RuntimeException("Eroare la rularea schema.sql", e);
        }
    }

    private void executaScript(String sql) throws Exception {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            for (String stmt : sql.split(";")) {
                String curatat = stmt.trim();
                if (curatat.isEmpty()) {
                    continue;
                }
                st.execute(curatat);
            }
        }
    }
}