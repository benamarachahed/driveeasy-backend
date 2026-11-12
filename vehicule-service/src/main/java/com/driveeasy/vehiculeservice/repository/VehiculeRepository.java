package com.driveeasy.vehiculeservice.repository;

import com.driveeasy.vehiculeservice.model.Statut;
import com.driveeasy.vehiculeservice.model.Vehicule;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VehiculeRepository {

    private final List<Vehicule> vehicules = new ArrayList<>();
    private Long nextId = 1L;

    public List<Vehicule> findAll() {
        return vehicules;
    }

    public Optional<Vehicule> findById(Long id) {
        return vehicules.stream().filter(v -> v.getId().equals(id)).findFirst();
    }

    public Optional<Vehicule> findByImmatriculation(String immatriculation) {
        return vehicules.stream()
                .filter(v -> v.getImmatriculation().equalsIgnoreCase(immatriculation))
                .findFirst();
    }

    public Vehicule save(Vehicule vehicule) {
        if (vehicule.getId() == null) {
            vehicule.setId(nextId++);
            vehicules.add(vehicule);
            return vehicule;
        }
        return update(vehicule);
    }

    public Vehicule update(Vehicule vehicule) {
        for (int i = 0; i < vehicules.size(); i++) {
            if (vehicules.get(i).getId().equals(vehicule.getId())) {
                vehicules.set(i, vehicule);
                return vehicule;
            }
        }
        throw new RuntimeException("V‚hicule non trouv‚ : " + vehicule.getId());
    }

    public void deleteById(Long id) {
        vehicules.removeIf(v -> v.getId().equals(id));
    }

    public List<Vehicule> findByStatut(Statut statut) {
        return vehicules.stream().filter(v -> v.getStatut() == statut).toList();
    }
}