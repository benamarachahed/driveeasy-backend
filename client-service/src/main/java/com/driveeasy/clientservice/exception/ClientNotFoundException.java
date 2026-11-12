package com.driveeasy.clientservice.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long id) {
        super("Client non trouvé avec l'id : " + id);
    }
}