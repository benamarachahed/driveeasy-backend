package com.driveeasy.locationservice.exception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(Long id) {
        super("Location non trouvée avec l'id : " + id);
    }
}