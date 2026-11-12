package com.driveeasy.vehiculeservice.service;

import com.driveeasy.vehiculeservice.exception.ImmatriculationDejaUtiliseeException;
import com.driveeasy.vehiculeservice.exception.RegleMetierException;
import com.driveeasy.vehiculeservice.exception.VehiculeNotFoundException;
import com.driveeasy.vehiculeservice.model.Carburant;
import com.driveeasy.vehiculeservice.model.Statut;
import com.driveeasy.vehiculeservice.model.Vehicule;
import com.driveeasy.vehiculeservice.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiculeService {

    private final VehiculeRepository repository;

    public VehiculeService(VehiculeRepository repository) {
        this.repository = repository;
    }

    public List<Vehicule> findAll(String categorie, Carburant carburant, Statut statut, BigDecimal prixMax) {
        return repository.findAll().stream()
                .filter(v -> categorie == null || (v.getMarque() + " " + v.getModele()).toLowerCase().contains(categorie.toLowerCase()))
                .filter(v -> carburant == null || v.getCarburant() == carburant)
                .filter(v -> statut == null || v.getStatut() == statut)
                .filter(v -> prixMax == null || v.getPrixJournalier().compareTo(prixMax) <= 0)
                .collect(Collectors.toList());
    }

    public Vehicule findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new VehiculeNotFoundException(id));
    }

    public Vehicule create(Vehicule vehicule) {
        validate(vehicule);
        if (repository.findByImmatriculation(vehicule.getImmatriculation()).isPresent()) {
            throw new ImmatriculationDejaUtiliseeException(vehicule.getImmatriculation());
        }
        return repository.save(vehicule);
    }

    public Vehicule update(Long id, Vehicule vehicule) {
        Vehicule existing = findById(id);
        validate(vehicule);
        repository.findByImmatriculation(vehicule.getImmatriculation())
                .filter(v -> !v.getId().equals(id))
                .ifPresent(v -> { throw new ImmatriculationDejaUtiliseeException(vehicule.getImmatriculation()); });
        vehicule.setId(existing.getId());
        return repository.update(vehicule);
    }

    public void delete(Long id) {
        Vehicule vehicule = findById(id);
        if (vehicule.getStatut() == Statut.LOUE) {
            throw new RegleMetierException("Impossible de supprimer un v‚hicule lou‚.");
        }
        repository.deleteById(id);
    }

    public Vehicule changeStatut(Long id, Statut statut) {
        Vehicule vehicule = findById(id);
        vehicule.setStatut(statut);
        return repository.update(vehicule);
    }

    public List<Vehicule> findDisponibles(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null || dateFin.isBefore(dateDebut)) {
            throw new RegleMetierException("Dates invalides.");
        }
        return repository.findByStatut(Statut.DISPONIBLE);
    }

    private void validate(Vehicule v) {
        if (v.getPrixJournalier() == null || v.getPrixJournalier().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegleMetierException("Le prix journalier doit ˆtre strictement positif.");
        }
        if (v.getAnnee() > LocalDate.now().getYear()) {
            throw new RegleMetierException("L'ann‚e du v‚hicule ne peut pas ˆtre dans le futur.");
        }
    }
}
