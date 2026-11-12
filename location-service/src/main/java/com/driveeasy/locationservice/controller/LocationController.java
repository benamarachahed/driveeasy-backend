package com.driveeasy.locationservice.controller;

import com.driveeasy.locationservice.model.Location;
import com.driveeasy.locationservice.service.LocationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Location> findAll() {
        return service.findAll();
    }

    // ⚠️ Route UNIQUE, impossible à confondre avec /{id}
    @PostMapping("/simulate")
    public BigDecimal simulate(
            @RequestParam Long vehiculeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam Long clientId) {
        return service.simulerPrix(vehiculeId, dateDebut, dateFin, clientId);
    }

    @GetMapping("/{id}")
    public Location findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Location create(@RequestBody Location location) {
        return service.create(location);
    }

    @PatchMapping("/{id}/depart")
    public Location demarrer(@PathVariable Long id) {
        return service.demarrer(id);
    }

    @PatchMapping("/{id}/retour")
    public Location retour(@PathVariable Long id, @RequestParam int kmRetour) {
        return service.retour(id, kmRetour);
    }

    @PatchMapping("/{id}/annulation")
    public Location annuler(@PathVariable Long id) {
        return service.annuler(id);
    }

    @GetMapping("/statistiques")
    public String statistiques() {
        return "Statistiques (à implémenter)";
    }
}