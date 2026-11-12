package com.driveeasy.vehiculeservice.controller;

import com.driveeasy.vehiculeservice.model.Carburant;
import com.driveeasy.vehiculeservice.model.Statut;
import com.driveeasy.vehiculeservice.model.Vehicule;
import com.driveeasy.vehiculeservice.service.VehiculeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/vehicules")
public class VehiculeController {

    private final VehiculeService service;

    public VehiculeController(VehiculeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Vehicule> findAll(
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) Carburant carburant,
            @RequestParam(required = false) Statut statut,
            @RequestParam(required = false) BigDecimal prixMax) {
        return service.findAll(categorie, carburant, statut, prixMax);
    }

    @GetMapping("/{id}")
    public Vehicule findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicule create(@RequestBody Vehicule vehicule) {
        return service.create(vehicule);
    }

    @PutMapping("/{id}")
    public Vehicule update(@PathVariable Long id, @RequestBody Vehicule vehicule) {
        return service.update(id, vehicule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/statut")
    public Vehicule changeStatut(@PathVariable Long id, @RequestParam Statut statut) {
        return service.changeStatut(id, statut);
    }

    @GetMapping("/disponibles")
    public List<Vehicule> findDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return service.findDisponibles(dateDebut, dateFin);
    }
}
