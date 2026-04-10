package com.pao.laboratory07.exercise1.exceptions;

public class CannotRevertInitialOrderStateException extends Exception {
    public CannotRevertInitialOrderStateException() {
        super("Nu există stare anterioară pentru undo.");
    }
}