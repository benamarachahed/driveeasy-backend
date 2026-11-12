package com.driveeasy.vehiculeservice.exception;

public class ImmatriculationDejaUtiliseeException extends RuntimeException {
    public ImmatriculationDejaUtiliseeException(String immatriculation) {
        super("L'immatriculation existe d‚j… : " + immatriculation);
    }
}
