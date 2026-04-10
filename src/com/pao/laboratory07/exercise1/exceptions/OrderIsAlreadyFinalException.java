package com.pao.laboratory07.exercise1.exceptions;

public class OrderIsAlreadyFinalException extends Exception {
    public OrderIsAlreadyFinalException() {
        super("Comanda este in stare finala.");
    }
}