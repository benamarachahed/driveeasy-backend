package com.driveeasy.locationservice.service;

import com.driveeasy.locationservice.client.ClientClient;
import com.driveeasy.locationservice.client.VehiculeClient;
import com.driveeasy.locationservice.exception.LocationNotFoundException;
import com.driveeasy.locationservice.exception.RegleMetierException;
import com.driveeasy.locationservice.model.*;
import com.driveeasy.locationservice.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LocationService {

    private final LocationRepository repository;
    private final VehiculeClient vehiculeClient;
    private final ClientClient clientClient;

    public LocationService(LocationRepository repository, VehiculeClient vehiculeClient, ClientClient clientClient) {
        this.repository = repository;
        this.vehiculeClient = vehiculeClient;
        this.clientClient = clientClient;
    }

    public List<Location> findAll() {
        return repository.findAll();
    }

    public Location findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new LocationNotFoundException(id));
    }

    public Location create(Location location) {
        // 1. Vérifier l'éligibilité du client
        EligibiliteDTO eligibilite = clientClient.verifierEligibilite(location.getClientId());
        if (!eligibilite.isEligible()) {
            throw new RegleMetierException("Client non éligible : " + eligibilite.getRaison());
        }

        // 2. Vérifier la disponibilité du véhicule
        VehiculeDTO vehicule = vehiculeClient.getVehicule(location.getVehiculeId());
        if (!"DISPONIBLE".equals(vehicule.getStatut())) {
            throw new RegleMetierException("Véhicule non disponible : " + vehicule.getStatut());
        }

        // 3. Valider les dates
        if (location.getDateDebut() == null || location.getDateFinPrevue() == null ||
                location.getDateFinPrevue().isBefore(location.getDateDebut())) {
            throw new RegleMetierException("Dates de location invalides.");
        }

        // 4. Calculer le prix
        long jours = ChronoUnit.DAYS.between(location.getDateDebut(), location.getDateFinPrevue());
        if (jours <= 0) jours = 1;

        BigDecimal prixJour = vehicule.getPrixJournalier();
        BigDecimal montant = prixJour.multiply(BigDecimal.valueOf(jours));

        // 5. Appliquer la remise fidélité
        ClientDTO client = clientClient.getClient(location.getClientId());
        BigDecimal remise = BigDecimal.ZERO;
        if ("FIDELE".equals(client.getCategorieClient())) {
            remise = montant.multiply(BigDecimal.valueOf(0.05));
        } else if ("VIP".equals(client.getCategorieClient())) {
            remise = montant.multiply(BigDecimal.valueOf(0.10));
        }
        montant = montant.subtract(remise).setScale(2, RoundingMode.HALF_UP);

        // 6. Créer la location
        location.setPrixJournalierApplique(prixJour);
        location.setMontantTotal(montant);
        location.setPenalite(BigDecimal.ZERO);
        location.setStatut(StatutLocation.RESERVEE);
        location.setDateCreation(LocalDate.now());

        Location saved = repository.save(location);

        // 7. Marquer le véhicule comme LOUE
        try {
            vehiculeClient.changeStatut(location.getVehiculeId(), "LOUE");
        } catch (Exception e) {
            // Compensation : annuler la réservation
            repository.deleteById(saved.getId());
            throw new RegleMetierException("Impossible de réserver le véhicule : " + e.getMessage());
        }

        return saved;
    }

    public BigDecimal simulerPrix(Long vehiculeId, LocalDate dateDebut, LocalDate dateFin, Long clientId) {
        VehiculeDTO vehicule = vehiculeClient.getVehicule(vehiculeId);
        ClientDTO client = clientClient.getClient(clientId);

        long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (jours <= 0) jours = 1;

        BigDecimal montant = vehicule.getPrixJournalier().multiply(BigDecimal.valueOf(jours));
        if ("FIDELE".equals(client.getCategorieClient())) {
            montant = montant.multiply(BigDecimal.valueOf(0.95));
        } else if ("VIP".equals(client.getCategorieClient())) {
            montant = montant.multiply(BigDecimal.valueOf(0.90));
        }
        return montant.setScale(2, RoundingMode.HALF_UP);
    }

    public Location demarrer(Long id) {
        Location location = findById(id);
        if (location.getStatut() != StatutLocation.RESERVEE) {
            throw new RegleMetierException("Seule une réservation peut être démarrée.");
        }
        location.setStatut(StatutLocation.EN_COURS);
        return repository.update(location);
    }

    public Location retour(Long id, int kmRetour) {
        Location location = findById(id);
        if (location.getStatut() != StatutLocation.EN_COURS) {
            throw new RegleMetierException("Seule une location en cours peut être retournée.");
        }

        location.setKmRetour(kmRetour);
        location.setDateRetourEffectif(LocalDate.now());

        // Calcul pénalité de retard
        long joursRetard = ChronoUnit.DAYS.between(location.getDateFinPrevue(), LocalDate.now());
        if (joursRetard > 0) {
            BigDecimal penalite = location.getPrixJournalierApplique()
                    .multiply(BigDecimal.valueOf(1.5))
                    .multiply(BigDecimal.valueOf(joursRetard));
            location.setPenalite(penalite);
            location.setMontantTotal(location.getMontantTotal().add(penalite));
        }

        location.setStatut(StatutLocation.TERMINEE);
        Location saved = repository.update(location);

        // Remettre le véhicule en DISPONIBLE
        try {
            vehiculeClient.changeStatut(location.getVehiculeId(), "DISPONIBLE");
        } catch (Exception e) {
            throw new RegleMetierException("Erreur lors de la mise à jour du véhicule : " + e.getMessage());
        }

        return saved;
    }

    public Location annuler(Long id) {
        Location location = findById(id);
        if (location.getStatut() != StatutLocation.RESERVEE) {
            throw new RegleMetierException("Seule une réservation peut être annulée.");
        }
        location.setStatut(StatutLocation.ANNULEE);
        Location saved = repository.update(location);

        // Remettre le véhicule en DISPONIBLE
        try {
            vehiculeClient.changeStatut(location.getVehiculeId(), "DISPONIBLE");
        } catch (Exception e) {
            throw new RegleMetierException("Erreur lors de la mise à jour du véhicule : " + e.getMessage());
        }

        return saved;
    }
}