package com.driveeasy.vehiculeservice.exception;

public class VehiculeNotFoundException extends RuntimeException {
    public VehiculeNotFoundException(Long id) {
        super("V‚hicule non trouv‚ avec l'id : " + id);
    }
}
