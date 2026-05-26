-- ============================================================
--  schema.sql  —  SQLite
--  Gestiune stocuri magazin (categorii, produse, furnizori, comenzi)
-- ============================================================

-- Ordinea de DROP: intai copiii (cu FK), apoi parintii.
DROP TABLE IF EXISTS linii_comanda;
DROP TABLE IF EXISTS comenzi;
DROP TABLE IF EXISTS aprovizionari;
DROP TABLE IF EXISTS produse;
DROP TABLE IF EXISTS categorii;
DROP TABLE IF EXISTS clienti;
DROP TABLE IF EXISTS furnizori;

-- In SQLite respectarea cheilor straine NU e activata implicit.
-- O activam per-conexiune (vezi DatabaseConnection / db.properties).
PRAGMA foreign_keys = ON;

CREATE TABLE categorii (
                           id        INTEGER PRIMARY KEY AUTOINCREMENT,
                           nume      TEXT    NOT NULL UNIQUE,
                           descriere TEXT
);

CREATE TABLE produse (
                         id            INTEGER PRIMARY KEY AUTOINCREMENT,
                         cod_prefix    TEXT    NOT NULL,
                         cod_serial    INTEGER NOT NULL,
                         nume          TEXT    NOT NULL,
                         pret          REAL    NOT NULL,
                         stoc          INTEGER NOT NULL DEFAULT 0,
                         tip           TEXT    NOT NULL,
                         id_categorie  INTEGER,
                         data_expirare TEXT,
                         garantie_luni INTEGER,
                         UNIQUE (cod_prefix, cod_serial),
                         FOREIGN KEY (id_categorie) REFERENCES categorii(id)
);

CREATE TABLE clienti (
                         id      INTEGER PRIMARY KEY AUTOINCREMENT,
                         nume    TEXT    NOT NULL,
                         cnp     TEXT    NOT NULL UNIQUE,
                         email   TEXT,
                         telefon TEXT
);

CREATE TABLE furnizori (
                           id            INTEGER PRIMARY KEY AUTOINCREMENT,
                           nume          TEXT    NOT NULL,
                           cnp           TEXT,
                           nume_companie TEXT,
                           cui           TEXT    NOT NULL UNIQUE
);

CREATE TABLE comenzi (
                         id           INTEGER PRIMARY KEY AUTOINCREMENT,
                         id_client    INTEGER NOT NULL,
                         data_comanda TEXT    NOT NULL,
                         total        REAL    NOT NULL,
                         FOREIGN KEY (id_client) REFERENCES clienti(id)
);

CREATE TABLE linii_comanda (
                               id          INTEGER PRIMARY KEY AUTOINCREMENT,
                               id_comanda  INTEGER NOT NULL,
                               id_produs   INTEGER NOT NULL,
                               cantitate   INTEGER NOT NULL,
                               pret_unitar REAL    NOT NULL,
                               FOREIGN KEY (id_comanda) REFERENCES comenzi(id),
                               FOREIGN KEY (id_produs)  REFERENCES produse(id)
);

CREATE TABLE aprovizionari (
                               id          INTEGER PRIMARY KEY AUTOINCREMENT,
                               id_furnizor INTEGER NOT NULL,
                               id_produs   INTEGER NOT NULL,
                               cantitate   INTEGER NOT NULL,
                               data        TEXT    NOT NULL,
                               FOREIGN KEY (id_furnizor) REFERENCES furnizori(id),
                               FOREIGN KEY (id_produs)   REFERENCES produse(id)
);