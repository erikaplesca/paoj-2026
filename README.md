# Gestiune Stocuri Magazin — Proiect PAO Etapa I

Sistem de gestiune a stocurilor pentru un magazin: categorii, produse (alimentare /
nealimentare), furnizori, clienti, aprovizionari si comenzi. 

> Realizat de Maria-Erika Plesca, grupa 233 

---

## 1.1 — Actiuni posibile in sistem 

1. **Adauga o categorie noua** in magazin
2. **Adauga un produs nou** intr-o categorie (alimentar / nealimentar)
3. **Inregistreaza un furnizor nou**
4. **Inregistreaza un client nou**
5. **Reaprovizioneaza stocul** unui produs (de la un furnizor — creste stocul)
6. **Plaseaza o comanda** (un client cumpara N produse — scade stocul)
7. **Cauta produse dupa categorie**
8. **Cauta un produs** dupa codul produsului (`CodProdus`)
9. **Afiseaza top produse vandute** (sortat descrescator dupa cantitate vanduta)
10. **Afiseaza istoricul comenzilor** unui client

> Bonus pentru demo: o a 11-a sectiune afiseaza produsele cu stoc sub un prag
> (utila pentru alerta de reaprovizionare), apelata si ea din `Main`.

---

## 1.2 — Tipuri de obiecte din domeniu

| # | Tip | Rol |
|---|-----|-----|
| 1 | `Categorie` | Grupeaza produsele (ex. *Lactate*, *Electrocasnice*). |
| 2 | `Produs` *(abstract)* | Clasa de baza pentru produse, cu cod, nume, pret, stoc si TVA abstract. |
| 3 | `ProdusAlimentar` | Extinde `Produs`; adauga `dataExpirare`, TVA 9%. |
| 4 | `ProdusNealimentar` | Extinde `Produs`; adauga `garantieLuni`, TVA 19%. |
| 5 | `CodProdus` *(imutabila)* | Identificator unic format din `prefix + serial` (ex. `ALIM-001`). |
| 6 | `Persoana` *(abstract)* | Clasa de baza pentru orice persoana din sistem (`abstract getRol()`). |
| 7 | `Angajat` | Extinde `Persoana`; adauga `idAngajat` si `salariu`. |
| 8 | `Manager` | Extinde `Angajat`; adauga `departament` (ierarhie cu 3 niveluri). |
| 9 | `Client` | Extinde `Persoana`; adauga `email`, `telefon`. |
| 10 | `Furnizor` | Extinde `Persoana`; adauga `numeCompanie`, `cui`. |
| 11 | `Comanda` | Comanda plasata de un client, cu lista de linii si total. |
| 12 | `LinieComanda` *(imutabila)* | Linie din comanda — `produs`, `cantitate`, `pretUnitar`. |
| 13 | `Aprovizionare` | Inregistrare a unei aprovizionari de la un furnizor. |

---

## 2. Implementare Java 

### 2.1 Clase si OOP

- [x] **13 clase** (>= 8 cerute)
- [x] Atribute `private` / `protected` cu getteri/setteri unde e necesar
- [x] `toString()`, `equals()`, `hashCode()` suprascrise la `Produs`, `Client`, `Comanda`,
      `Categorie`, `CodProdus`
- [x] Ierarhie de mostenire **cu 3 niveluri**: `Persoana` -> `Angajat` -> `Manager`
- [x] Clasa **abstracta** `Persoana` cu metoda abstracta `getRol()`; clasa abstracta
      `Produs` cu `calculeazaTaxa()`
- [x] Clasa **imutabila** `CodProdus` (`final class`, atribute `final`, fara setteri,
      validare in constructor); `LinieComanda` este de asemenea imutabila
- [x] **2 exceptii custom** aruncate si tratate in cod:
      - `StocInsuficientException` — la plasarea unei comenzi cand stocul nu ajunge
      - `EntitateInexistentaException` — la cautari pe id-uri inexistente

### 2.2 Colectii

- [x] **4 tipuri diferite** de colectii: `List`, `Map`, `TreeSet`, `Queue`
- [x] **Sortata**: `TreeSet<Produs>` — `Produs implements Comparable<Produs>` (dupa nume).
      Plus `Comparator` in `ComandaService.topProduseVandute()`
- [x] **Map** pentru indexare:
      - `Map<CodProdus, Produs>` in `ProdusService` (index dupa cod)
      - `Map<String, List<Comanda>>` in `ComandaService` (comenzi grupate dupa CNP client)
      - `Map<String, Categorie>`, `Map<String, Furnizor>`, `Map<String, Client>`

### 2.3 Servicii

- [x] **5 servicii Singleton** (constructor `private` + `getInstance()` sincronizat):
      `CategorieService`, `ProdusService`, `FurnizorService`, `ClientService`, `ComandaService`
- [x] Fiecare expune: `adauga`, `sterge`, `cautaDupa<id/nume>`, `listeazaToate`
- [x] `Main` apeleaza toate cele 10 actiuni din sectiunea 1.1

### 2.4 Organizare si calitate

- [x] Cod organizat in sub-pachete:
      ```
      com.pao.proiect.magazin/
      ├── model/        ← clase de domeniu
      ├── service/      ← servicii Singleton
      ├── exception/    ← exceptii custom
      └── Main.java
      ```
- [x] Fara cod duplicat — logica comuna (validari, ierarhii) extrasa in clase de baza
- [x] Fara `NullPointerException` — `Objects.requireNonNull` pe inputurile serviciilor

---

## Structura proiectului

```
proiect-etapa1/
├── README.md
└── src/
    └── com/pao/proiect/magazin/
        ├── Main.java
        ├── model/
        │   ├── Persoana.java
        │   ├── Angajat.java
        │   ├── Manager.java
        │   ├── Client.java
        │   ├── Furnizor.java
        │   ├── Categorie.java
        │   ├── Produs.java
        │   ├── ProdusAlimentar.java
        │   ├── ProdusNealimentar.java
        │   ├── CodProdus.java
        │   ├── Comanda.java
        │   ├── LinieComanda.java
        │   └── Aprovizionare.java
        ├── service/
        │   ├── CategorieService.java
        │   ├── ProdusService.java
        │   ├── FurnizorService.java
        │   ├── ClientService.java
        │   └── ComandaService.java
        └── exception/
            ├── StocInsuficientException.java
            └── EntitateInexistentaException.java
```

---

## Cum se ruleaza

Din folderul `proiect-etapa1/`:

```bash
# Compileaza toate sursele in folderul out/
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# Ruleaza demo-ul cu toate cele 10 actiuni
java -cp out com.pao.proiect.magazin.Main
```

Sau direct din IntelliJ: `Run` pe clasa `com.pao.proiect.magazin.Main`.

---

## Note pentru Etapa II

Designul lasa loc pentru extinderile Etapei II:
- pachet `repository/` paralel cu `service/` pentru persistenta JDBC
- `AuditService` Singleton apelat din toate serviciile (toate cele 10 actiuni)
- `ComandaService.plaseazaComanda()` va deveni o tranzactie JDBC (insert in
  `comenzi` + `linii_comanda` + update `produse.stoc`)
