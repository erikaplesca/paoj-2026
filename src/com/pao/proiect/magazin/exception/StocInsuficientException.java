package com.pao.proiect.magazin.exception;

public class StocInsuficientException extends RuntimeException {
    public StocInsuficientException(String numeProdus, int cerut, int disponibil) {
        super("Stoc insuficient pentru produsul \"" + numeProdus
                + "\": cerute " + cerut + ", disponibile " + disponibil + ".");
    }
}
