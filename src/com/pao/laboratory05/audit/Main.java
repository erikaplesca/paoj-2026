package com.pao.laboratory05.audit;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AngajatService service = AngajatService.getInstance();

        while (true) {
            System.out.println("\n===== Gestionare Angajați (cu Audit) =====");
            System.out.println("1. Adaugă angajat");
            System.out.println("2. Listare după salariu");
            System.out.println("3. Caută după departament");
            System.out.println("4. Afișează audit log");
            System.out.println("0. Ieșire");
            System.out.print("Opțiune: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1:
                    System.out.print("Nume: "); String nume = scanner.nextLine();
                    System.out.print("Departament (nume): "); String dNume = scanner.nextLine();
                    System.out.print("Departament (locatie): "); String dLocatie = scanner.nextLine();
                    System.out.print("Salariu: "); double sal = scanner.nextDouble();
                    service.addAngajat(new Angajat(nume, new Departament(dNume, dLocatie), sal));
                    break;
                case 2:
                    service.listBySalary();
                    break;
                case 3:
                    System.out.print("Introduceți numele departamentului: ");
                    service.findByDepartament(scanner.nextLine());
                    break;
                case 4:
                    service.printAuditLog();
                    break;
                case 0:
                    System.out.println("Ieșire program...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opțiune invalidă!");
            }
        }
    }
}