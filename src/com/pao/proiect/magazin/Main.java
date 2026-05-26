package com.pao.proiect.magazin;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import com.pao.proiect.magazin.exception.EntitateInexistentaException;
import com.pao.proiect.magazin.exception.StocInsuficientException;
import com.pao.proiect.magazin.model.Angajat;
import com.pao.proiect.magazin.model.Aprovizionare;
import com.pao.proiect.magazin.model.Categorie;
import com.pao.proiect.magazin.model.Client;
import com.pao.proiect.magazin.model.CodProdus;
import com.pao.proiect.magazin.model.Comanda;
import com.pao.proiect.magazin.model.Furnizor;
import com.pao.proiect.magazin.model.Manager;
import com.pao.proiect.magazin.model.Produs;
import com.pao.proiect.magazin.model.ProdusAlimentar;
import com.pao.proiect.magazin.model.ProdusNealimentar;
import com.pao.proiect.magazin.repository.CategorieRepository;
import com.pao.proiect.magazin.repository.ClientRepository;
import com.pao.proiect.magazin.repository.ComandaRepository;
import com.pao.proiect.magazin.repository.ProdusRepository;
import com.pao.proiect.magazin.service.AuditService;
import com.pao.proiect.magazin.service.CategorieService;
import com.pao.proiect.magazin.service.ClientService;
import com.pao.proiect.magazin.service.ComandaService;
import com.pao.proiect.magazin.service.FurnizorService;
import com.pao.proiect.magazin.service.ProdusService;
import com.pao.proiect.magazin.util.DatabaseConnection;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DatabaseConnection.getInstance().initSchema();

        CategorieService categorieService = CategorieService.getInstance();
        ProdusService produsService = ProdusService.getInstance();
        FurnizorService furnizorService = FurnizorService.getInstance();
        ClientService clientService = ClientService.getInstance();
        ComandaService comandaService = ComandaService.getInstance();

        Manager manager = new Manager("Erika Plesca", "2980101123456", 1, 8500.0, "Operatiuni Magazin");
        Angajat angajatStoc = new Angajat("Andrei Popescu", "1990202123456", 2, 4200.0);
        System.out.println("Magazinul este coordonat de: " + manager);
        System.out.println("Aprovizionarile sunt operate de: " + angajatStoc);
        System.out.println();

        actiunea1AdaugaCategorie(categorieService);
        actiunea2AdaugaProdus(categorieService, produsService);
        actiunea3InregistreazaFurnizor(furnizorService);
        actiunea4InregistreazaClient(clientService);
        actiunea5Aprovizioneaza(furnizorService, produsService);
        actiunea6PlaseazaComanda(clientService, produsService, comandaService);
        actiunea7CautaDupaCategorie(produsService);
        actiunea8CautaDupaCod(produsService);
        actiunea9TopProduseVandute(comandaService);
        actiunea10IstoricClient(clientService, comandaService);

        sectiuneBonusStocSubPrag(produsService);

        demoJDBC(categorieService, produsService, clientService, comandaService);

        runMeniu(categorieService, produsService, furnizorService, clientService, comandaService);
    }
    private static void afiseazaTitlu(int numar, String titlu) {
        System.out.println();
        System.out.println("=== Actiunea " + numar + ": " + titlu + " ===");
    }

    private static void actiunea1AdaugaCategorie(CategorieService service) {
        afiseazaTitlu(1, "Adauga o categorie noua");
        service.adauga(new Categorie("Lactate", "Produse lactate proaspete"));
        service.adauga(new Categorie("Electrocasnice", "Aparate electrocasnice mici"));
        for (Categorie c : service.listeazaToate()) {
            System.out.println("  + " + c);
        }
    }

    private static void actiunea2AdaugaProdus(CategorieService cs, ProdusService ps) {
        afiseazaTitlu(2, "Adauga un produs nou intr-o categorie");
        Categorie lactate = cs.cautaDupaNume("Lactate");
        Categorie electro = cs.cautaDupaNume("Electrocasnice");

        ps.adauga(new ProdusAlimentar(new CodProdus("ALIM", 1), "Iaurt natural 400g",
                7.50, 0, lactate, LocalDate.of(2026, 5, 30)));
        ps.adauga(new ProdusAlimentar(new CodProdus("ALIM", 2), "Branza telemea 500g",
                18.90, 0, lactate, LocalDate.of(2026, 6, 15)));
        ps.adauga(new ProdusNealimentar(new CodProdus("NEAL", 1), "Mixer manual 300W",
                149.99, 0, electro, 24));
        ps.adauga(new ProdusNealimentar(new CodProdus("NEAL", 2), "Cantar de bucatarie",
                89.50, 0, electro, 12));

        for (Produs p : ps.listeazaToate()) {
            System.out.println("  + " + p + " | TVA = " + String.format("%.2f", p.calculeazaTaxa()));
        }
    }

    private static void actiunea3InregistreazaFurnizor(FurnizorService service) {
        afiseazaTitlu(3, "Inregistreaza un furnizor nou");
        service.adauga(new Furnizor("Mihai Ionescu", "1850303123456", "Lacto Prod SRL", "RO12345678"));
        service.adauga(new Furnizor("Ana Marinescu", "2820404123456", "ElectroDistrib SA", "RO87654321"));
        for (Furnizor f : service.listeazaToate()) {
            System.out.println("  + " + f);
        }
    }

    private static void actiunea4InregistreazaClient(ClientService service) {
        afiseazaTitlu(4, "Inregistreaza un client nou");
        service.adauga(new Client("Maria Dumitru", "2950505123456", "maria.d@example.com", "0711222333"));
        service.adauga(new Client("Cristian Vasile", "1880606123456", "cristi.v@example.com", "0744555666"));
        for (Client c : service.listeazaToate()) {
            System.out.println("  + " + c);
        }
    }

    private static void actiunea5Aprovizioneaza(FurnizorService fs, ProdusService ps) {
        afiseazaTitlu(5, "Reaprovizioneaza stocul unui produs");
        Produs iaurt = ps.cautaDupaCod(new CodProdus("ALIM", 1));
        Produs branza = ps.cautaDupaCod(new CodProdus("ALIM", 2));
        Produs mixer = ps.cautaDupaCod(new CodProdus("NEAL", 1));
        Produs cantar = ps.cautaDupaCod(new CodProdus("NEAL", 2));

        Aprovizionare a1 = fs.aprovizioneaza("RO12345678", iaurt, 50);
        Aprovizionare a2 = fs.aprovizioneaza("RO12345678", branza, 30);
        Aprovizionare a3 = fs.aprovizioneaza("RO87654321", mixer, 10);
        Aprovizionare a4 = fs.aprovizioneaza("RO87654321", cantar, 2);

        System.out.println("  + " + a1);
        System.out.println("  + " + a2);
        System.out.println("  + " + a3);
        System.out.println("  + " + a4);
        System.out.println("  Stoc dupa aprovizionare:");
        for (Produs p : ps.listeazaToate()) {
            System.out.println("    - " + p.getNume() + " | stoc=" + p.getStoc());
        }
    }

    private static void actiunea6PlaseazaComanda(ClientService cs, ProdusService ps, ComandaService oms) {
        afiseazaTitlu(6, "Plaseaza o comanda");
        Client maria = cs.cautaDupaCnp("2950505123456");
        Client cristi = cs.cautaDupaCnp("1880606123456");

        Map<CodProdus, Integer> cosMaria = new LinkedHashMap<>();
        cosMaria.put(new CodProdus("ALIM", 1), 3);
        cosMaria.put(new CodProdus("ALIM", 2), 1);
        cosMaria.put(new CodProdus("NEAL", 2), 1);
        Comanda c1 = oms.plaseazaComanda(maria, cosMaria);
        System.out.println("  + " + c1);

        Map<CodProdus, Integer> cosCristi = new LinkedHashMap<>();
        cosCristi.put(new CodProdus("NEAL", 1), 2);
        cosCristi.put(new CodProdus("ALIM", 1), 5);
        Comanda c2 = oms.plaseazaComanda(cristi, cosCristi);
        System.out.println("  + " + c2);

        Map<CodProdus, Integer> cosImposibil = new HashMap<>();
        cosImposibil.put(new CodProdus("NEAL", 2), 100);
        try {
            oms.plaseazaComanda(maria, cosImposibil);
        } catch (StocInsuficientException ex) {
            System.out.println("  ! Comanda esuata corect: " + ex.getMessage());
        }
    }

    private static void actiunea7CautaDupaCategorie(ProdusService ps) {
        afiseazaTitlu(7, "Cauta produse dupa categorie");
        List<Produs> lactate = ps.dupaCategorie("Lactate");
        System.out.println("  Produse din categoria 'Lactate':");
        for (Produs p : lactate) {
            System.out.println("    - " + p);
        }
        List<Produs> electro = ps.dupaCategorie("Electrocasnice");
        System.out.println("  Produse din categoria 'Electrocasnice':");
        for (Produs p : electro) {
            System.out.println("    - " + p);
        }
    }

    private static void actiunea8CautaDupaCod(ProdusService ps) {
        afiseazaTitlu(8, "Cauta un produs dupa cod");
        Produs gasit = ps.cautaDupaCod(new CodProdus("ALIM", 1));
        System.out.println("  Produs gasit: " + gasit);
        try {
            ps.cautaDupaCod(new CodProdus("ALIM", 999));
        } catch (EntitateInexistentaException ex) {
            System.out.println("  ! Cautare cu cod inexistent: " + ex.getMessage());
        }
    }

    private static void actiunea9TopProduseVandute(ComandaService oms) {
        afiseazaTitlu(9, "Top produse vandute");
        Map<Produs, Integer> top = oms.topProduseVandute();
        int rang = 1;
        for (Map.Entry<Produs, Integer> e : top.entrySet()) {
            System.out.println("  " + rang + ". " + e.getKey().getNume()
                    + " - " + e.getValue() + " buc.");
            rang++;
        }
    }

    private static void actiunea10IstoricClient(ClientService cs, ComandaService oms) {
        afiseazaTitlu(10, "Istoricul comenzilor unui client");
        Client maria = cs.cautaDupaCnp("2950505123456");
        List<Comanda> istoric = oms.istoricClient(maria.getCnp());
        System.out.println("  Comenzi pentru " + maria.getNume() + ":");
        for (Comanda c : istoric) {
            System.out.println("    - " + c);
        }
    }

    // -----------------------------------------------------------------------
    // Meniu interactiv
    // -----------------------------------------------------------------------

    private static void runMeniu(CategorieService cs, ProdusService ps,
                                 FurnizorService fs, ClientService cls,
                                 ComandaService oms) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n \n \n ");
        System.out.println("  MENIU - Gestiune Magazin");

        boolean running = true;
        while (running) {
            System.out.println("\n  1.  Adauga categorie");
            System.out.println("  2.  Adauga produs");
            System.out.println("  3.  Inregistreaza furnizor");
            System.out.println("  4.  Inregistreaza client");
            System.out.println("  5.  Aprovizioneaza produs");
            System.out.println("  6.  Plaseaza comanda");
            System.out.println("  7.  Cauta produse dupa categorie");
            System.out.println("  8.  Cauta produs dupa cod");
            System.out.println("  9.  Top produse vandute");
            System.out.println("  10. Istoricul comenzilor unui client");
            System.out.println("  0.  Iesire");
            System.out.print("\n  Alege actiunea: ");

            String linie = sc.nextLine().trim();
            int optiune;
            try {
                optiune = Integer.parseInt(linie);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Optiune invalida. Introdu un numar intre 0 si 10.");
                continue;
            }

            System.out.println();
            try {
                switch (optiune) {
                    case 1  -> meniu1AdaugaCategorie(sc, cs);
                    case 2  -> meniu2AdaugaProdus(sc, cs, ps);
                    case 3  -> meniu3AdaugaFurnizor(sc, fs);
                    case 4  -> meniu4AdaugaClient(sc, cls);
                    case 5  -> meniu5Aprovizioneaza(sc, fs, ps);
                    case 6  -> meniu6PlaseazaComanda(sc, cls, oms);
                    case 7  -> meniu7CautaDupaCategorie(sc, ps);
                    case 8  -> meniu8CautaDupaCod(sc, ps);
                    case 9  -> meniu9TopVandute(oms);
                    case 10 -> meniu10IstoricClient(sc, cls, oms);
                    case 0  -> { running = false; System.out.println("  La revedere!"); }
                    default -> System.out.println("  [!] Optiune invalida.");
                }
            } catch (EntitateInexistentaException | StocInsuficientException ex) {
                System.out.println("  [!] Eroare: " + ex.getMessage());
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("  [!] Date invalide: " + ex.getMessage());
            }
        }
        sc.close();
    }

    private static void meniu1AdaugaCategorie(Scanner sc, CategorieService cs) {
        System.out.print("  Nume categorie: ");
        String nume = sc.nextLine().trim();
        System.out.print("  Descriere: ");
        String desc = sc.nextLine().trim();
        cs.adauga(new Categorie(nume, desc));
        System.out.println("  [OK] Categorie adaugata: " + cs.cautaDupaNume(nume));
        System.out.println("  Toate categoriile: ");
        for (Categorie c : cs.listeazaToate()) {
            System.out.println("    - " + c.getNume());
        }
    }

    private static void meniu2AdaugaProdus(Scanner sc, CategorieService cs, ProdusService ps) {
        System.out.print("  Tip produs (1=alimentar, 2=nealimentar): ");
        String tip = sc.nextLine().trim();
        System.out.print("  Prefix cod (ex. ALIM, NEAL): ");
        String prefix = sc.nextLine().trim();
        System.out.print("  Serial cod (numar intreg pozitiv): ");
        int serial = Integer.parseInt(sc.nextLine().trim());
        System.out.print("  Nume produs: ");
        String nume = sc.nextLine().trim();
        System.out.print("  Pret (ex. 12.50): ");
        double pret = Double.parseDouble(sc.nextLine().trim());
        System.out.print("  Stoc initial: ");
        int stoc = Integer.parseInt(sc.nextLine().trim());
        System.out.print("  Categorie (nume exact): ");
        String catNume = sc.nextLine().trim();
        Categorie cat = cs.cautaDupaNume(catNume);
        CodProdus cod = new CodProdus(prefix, serial);
        Produs p;
        if ("1".equals(tip)) {
            System.out.print("  Data expirare (AAAA-LL-ZZ): ");
            LocalDate exp = LocalDate.parse(sc.nextLine().trim());
            p = new ProdusAlimentar(cod, nume, pret, stoc, cat, exp);
        } else {
            System.out.print("  Garantie (luni): ");
            int gar = Integer.parseInt(sc.nextLine().trim());
            p = new ProdusNealimentar(cod, nume, pret, stoc, cat, gar);
        }
        ps.adauga(p);
        System.out.println("  [OK] Produs adaugat: " + p);
        System.out.println("  TVA produs: " + String.format("%.2f", p.calculeazaTaxa()) + " RON");
    }

    private static void meniu3AdaugaFurnizor(Scanner sc, FurnizorService fs) {
        System.out.print("  Nume reprezentant: ");
        String nume = sc.nextLine().trim();
        System.out.print("  CNP reprezentant: ");
        String cnp = sc.nextLine().trim();
        System.out.print("  Nume companie: ");
        String companie = sc.nextLine().trim();
        System.out.print("  CUI (ex. RO12345678): ");
        String cui = sc.nextLine().trim();
        fs.adauga(new Furnizor(nume, cnp, companie, cui));
        System.out.println("  [OK] Furnizor inregistrat: " + fs.cautaDupaCui(cui));
        System.out.println("  Total furnizori in sistem: " + fs.listeazaToate().size());
    }

    private static void meniu4AdaugaClient(Scanner sc, ClientService cls) {
        System.out.print("  Nume client: ");
        String nume = sc.nextLine().trim();
        System.out.print("  CNP: ");
        String cnp = sc.nextLine().trim();
        System.out.print("  Email: ");
        String email = sc.nextLine().trim();
        System.out.print("  Telefon: ");
        String tel = sc.nextLine().trim();
        cls.adauga(new Client(nume, cnp, email, tel));
        System.out.println("  [OK] Client inregistrat: " + cls.cautaDupaCnp(cnp));
        System.out.println("  Total clienti in sistem: " + cls.listeazaToate().size());
    }

    private static void meniu5Aprovizioneaza(Scanner sc, FurnizorService fs, ProdusService ps) {
        System.out.println("  Furnizori disponibili:");
        for (Furnizor f : fs.listeazaToate()) {
            System.out.println("    - " + f.getNumeCompanie() + " | CUI: " + f.getCui());
        }
        System.out.print("  CUI furnizor: ");
        String cui = sc.nextLine().trim();
        System.out.println("  Produse disponibile:");
        for (Produs p : ps.listeazaToate()) {
            System.out.println("    - " + p.getCod() + " | " + p.getNume() + " | stoc actual: " + p.getStoc());
        }
        System.out.print("  Prefix cod produs (ex. ALIM): ");
        String prefix = sc.nextLine().trim();
        System.out.print("  Serial cod produs: ");
        int serial = Integer.parseInt(sc.nextLine().trim());
        System.out.print("  Cantitate: ");
        int cant = Integer.parseInt(sc.nextLine().trim());
        Produs produs = ps.cautaDupaCod(new CodProdus(prefix, serial));
        int stocInainte = produs.getStoc();
        Aprovizionare ap = fs.aprovizioneaza(cui, produs, cant);
        System.out.println("  [OK] " + ap);
        System.out.println("  Stoc '" + produs.getNume() + "': " + stocInainte + " -> " + produs.getStoc());
    }

    private static void meniu6PlaseazaComanda(Scanner sc, ClientService cls, ComandaService oms) {
        System.out.println("  Clienti disponibili:");
        for (Client c : cls.listeazaToate()) {
            System.out.println("    - " + c.getNume() + " | CNP: " + c.getCnp());
        }
        System.out.print("  CNP client: ");
        String cnp = sc.nextLine().trim();
        Client client = cls.cautaDupaCnp(cnp);

        Map<CodProdus, Integer> cos = new LinkedHashMap<>();
        System.out.println("  Adauga produse (prefix serial cantitate, ex: ALIM 1 3). Scrie 'gata' cand termini.");
        while (true) {
            System.out.print("  > ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("gata")) break;
            String[] parts = input.split("\\s+");
            if (parts.length != 3) {
                System.out.println("  [!] Format gresit. Exemplu: ALIM 1 3");
                continue;
            }
            try {
                CodProdus cod = new CodProdus(parts[0], Integer.parseInt(parts[1]));
                int cantitate = Integer.parseInt(parts[2]);
                cos.put(cod, cantitate);
                System.out.println("  Adaugat: " + cod + " x" + cantitate);
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] Date invalide: " + e.getMessage());
            }
        }
        Comanda comanda = oms.plaseazaComanda(client, cos);
        System.out.println("  [OK] Comanda plasata cu succes!");
        System.out.println("  " + comanda);
        System.out.println("  Total de plata: " + String.format("%.2f", comanda.getTotal()) + " RON");
    }

    private static void meniu7CautaDupaCategorie(Scanner sc, ProdusService ps) {
        System.out.print("  Nume categorie: ");
        String catNume = sc.nextLine().trim();
        List<Produs> rezultat = ps.dupaCategorie(catNume);
        if (rezultat.isEmpty()) {
            System.out.println("  Niciun produs gasit in categoria '" + catNume + "'.");
        } else {
            System.out.println("  Produse din '" + catNume + "' (" + rezultat.size() + "):");
            for (Produs p : rezultat) {
                System.out.println("    - " + p);
            }
        }
    }

    private static void meniu8CautaDupaCod(Scanner sc, ProdusService ps) {
        System.out.print("  Prefix cod (ex. ALIM): ");
        String prefix = sc.nextLine().trim();
        System.out.print("  Serial: ");
        int serial = Integer.parseInt(sc.nextLine().trim());
        Produs p = ps.cautaDupaCod(new CodProdus(prefix, serial));
        System.out.println("  [OK] Produs gasit: " + p);
        System.out.println("  TVA: " + String.format("%.2f", p.calculeazaTaxa()) + " RON");
    }

    private static void meniu9TopVandute(ComandaService oms) {
        Map<Produs, Integer> top = oms.topProduseVandute();
        if (top.isEmpty()) {
            System.out.println("  Nu exista comenzi inca.");
            return;
        }
        System.out.println("  Top produse vandute:");
        int rang = 1;
        for (Map.Entry<Produs, Integer> e : top.entrySet()) {
            System.out.println("  " + rang + ". " + e.getKey().getNume()
                    + " [" + e.getKey().getCod() + "]"
                    + " - " + e.getValue() + " buc. vandute");
            rang++;
        }
    }

    private static void meniu10IstoricClient(Scanner sc, ClientService cls, ComandaService oms) {
        System.out.println("  Clienti disponibili:");
        for (Client c : cls.listeazaToate()) {
            System.out.println("    - " + c.getNume() + " | CNP: " + c.getCnp());
        }
        System.out.print("  CNP client: ");
        String cnp = sc.nextLine().trim();
        Client client = cls.cautaDupaCnp(cnp);
        List<Comanda> istoric = oms.istoricClient(cnp);
        System.out.println("  Istoric comenzi pentru " + client.getNume() + ":");
        if (istoric.isEmpty()) {
            System.out.println("    (nicio comanda)");
        } else {
            double totalGeneral = 0;
            for (Comanda c : istoric) {
                System.out.println("    Comanda #" + c.getId() + " | " + c.getData()
                        + " | Total: " + String.format("%.2f", c.getTotal()) + " RON");
                for (var l : c.getLinii()) {
                    System.out.println("      * " + l.getProdus().getNume()
                            + " x" + l.getCantitate()
                            + " @ " + l.getPretUnitar() + " RON");
                }
                totalGeneral += c.getTotal();
            }
            System.out.println("  Total cheltuit: " + String.format("%.2f", totalGeneral) + " RON");
        }
    }

    // -----------------------------------------------------------------------
    // Demo JDBC: repository-uri, tranzactii, JOIN-uri
    // -----------------------------------------------------------------------

    private static void demoJDBC(CategorieService cs, ProdusService ps,
                                 ClientService cls, ComandaService oms) {
        System.out.println();
        System.out.println("=== Demo Etapa II: Persistenta JDBC ===");
        System.out.println("(Foloseste SQLite - baza de date paoj_proiect.db, creata automat)");
        System.out.println();

        try {
            CategorieRepository catRepo = new CategorieRepository();
            ClientRepository clientRepo = new ClientRepository();
            ProdusRepository produsRepo = new ProdusRepository();
            ComandaRepository comandaRepo = new ComandaRepository();

            // --- Salvare date in DB ---
            System.out.println("  [DB] Salvare categorii...");
            for (Categorie c : cs.listeazaToate()) {
                catRepo.save(c);
            }

            System.out.println("  [DB] Salvare produse...");
            for (Produs p : ps.listeazaToate()) {
                produsRepo.save(p);
            }

            System.out.println("  [DB] Salvare clienti...");
            for (Client c : cls.listeazaToate()) {
                clientRepo.save(c);
            }

            System.out.println("  [DB] Salvare comenzi (cu tranzactii JDBC)...");
            for (Comanda c : oms.listeazaToate()) {
                comandaRepo.save(c);
            }

            // --- Citire din DB ---
            System.out.println();
            System.out.println("  [DB] Categorii din baza de date:");
            for (Categorie c : catRepo.findAll()) {
                System.out.println("    - " + c);
            }

            System.out.println("  [DB] Clienti din baza de date:");
            for (Client c : clientRepo.findAll()) {
                System.out.println("    - " + c);
            }

            System.out.println("  [DB] Produse din baza de date (JOIN cu categorii):");
            for (Produs p : produsRepo.findAll()) {
                System.out.println("    - " + p);
            }

            // --- JOIN 1: comenzi cu client ---
            System.out.println();
            System.out.println("  [DB] Comenzi cu date client (JOIN 1):");
            for (Comanda c : comandaRepo.findAll()) {
                System.out.printf("    - Comanda #%d | Client: %s | Total: %.2f RON%n",
                        c.getId(), c.getClient().getNume(), c.getTotal());
            }

            // --- JOIN 2: top produse vandute cu categorie ---
            System.out.println();
            System.out.println("  [DB] Top produse vandute cu categorie (JOIN 2):");
            int rang = 1;
            for (String[] row : comandaRepo.findTopProduseVanduteCuCategorie()) {
                System.out.printf("    %d. %s [%s] — %s buc.%n", rang++, row[0], row[1], row[2]);
            }

            // --- JOIN 3: linii comanda cu detalii ---
            System.out.println();
            System.out.println("  [DB] Linii comanda cu detalii produs (JOIN 3):");
            for (String[] row : comandaRepo.findLiniiCuDetalii()) {
                System.out.printf("    Comanda #%s | %s [%s] x%s @ %s RON%n",
                        row[0], row[2], row[3], row[4], row[5]);
            }

            // --- findById demo ---
            System.out.println();
            catRepo.findById("Lactate").ifPresentOrElse(
                    c -> System.out.println("  [DB] findById('Lactate'): " + c),
                    () -> System.out.println("  [DB] Categoria 'Lactate' nu a fost gasita in DB."));

            System.out.println();
            System.out.println("  [DB] Demo JDBC finalizat cu succes!");

        } catch (RuntimeException e) {
            System.out.println("  [DB] Conexiunea la baza de date nu este disponibila: " + e.getMessage());
            System.out.println("  [DB] Verifica driverul SQLite si schema.sql.");
            System.out.println("  [DB] Restul demonstratiei (in-memory) a functionat corect.");
        }

        // --- Afisare audit.csv ---
        System.out.println();
        System.out.println("=== Continut audit.csv ===");
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader("audit.csv"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("  " + line);
            }
            reader.close();
        } catch (java.io.IOException e) {
            System.out.println("  (fisierul audit.csv nu a putut fi citit: " + e.getMessage() + ")");
        }
    }

    private static void sectiuneBonusStocSubPrag(ProdusService ps) {
        afiseazaTitlu(11, "Bonus - produse cu stoc sub prag");
        int prag = 5;
        List<Produs> sub = ps.stocSubPrag(prag);
        System.out.println("  Produse cu stoc < " + prag + ":");
        if (sub.isEmpty()) {
            System.out.println("    (niciunul)");
        } else {
            for (Produs p : sub) {
                System.out.println("    - " + p.getNume() + " | stoc=" + p.getStoc());
            }
        }
    }
}