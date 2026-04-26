package com.pao.proiect.magazin.exception;

public class EntitateInexistentaException extends RuntimeException {
    public EntitateInexistentaException(String tipEntitate, String identificator) {
        super(tipEntitate + " inexistent(a) cu identificatorul: " + identificator);
    }
}
