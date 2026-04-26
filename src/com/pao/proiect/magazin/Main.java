package com.pao.proiect.magazin;

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
import com.pao.proiect.magazin.service.CategorieService;
import com.pao.proiect.magazin.service.ClientService;
import com.pao.proiect.magazin.service.ComandaService;
import com.pao.proiect.magazin.service.FurnizorService;
import com.pao.proiect.magazin.service.ProdusService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
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
