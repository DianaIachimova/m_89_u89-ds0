package com.example.insurance_app.application.exception;

public class DuplicateIdentificationNumberException extends RuntimeException {
    private final String identificationNumber;

    public DuplicateIdentificationNumberException(String identificationNumber) {
        super("Client with identification number %s already exists".formatted(identificationNumber));
        this.identificationNumber = identificationNumber;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }
}
