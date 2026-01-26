package com.example.insurance_app.application.exception;

import java.util.UUID;

public class ClientNotFoundException extends ResourceNotFoundException {
    public ClientNotFoundException(UUID clientId) {
        super("Client", "id", clientId);
    }
}
