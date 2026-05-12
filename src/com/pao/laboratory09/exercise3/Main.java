package com.pao.laboratory09.exercise3;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ATMThread.resetContor(); // safe daca ruleaza de mai multe ori

        CoadaTranzactii coada = new CoadaTranzactii(5);
        ProcessorThread processorThread = new ProcessorThread(coada);

        ATMThread atm1 = new ATMThread(1, coada);
        ATMThread atm2 = new ATMThread(2, coada);
        ATMThread atm3 = new ATMThread(3, coada);

        Thread processorFir = new Thread(processorThread);

        processorFir.start();
        atm1.start();
        atm2.start();
        atm3.start();

        // Asteptam toti producatorii sa termine de trimis
        atm1.join();
        atm2.join();
        atm3.join();

        // Asteptam ca procesorul sa consume tot ce a ramas in coada
        coada.waitUntilEmpty();

        // Abia acum oprim consumatorul
        processorThread.activ = false;
        coada.trezeste();

        processorFir.join();

        System.out.println("Toate tranzactiile procesate. Total: " + processorThread.getTotal());
    }
}