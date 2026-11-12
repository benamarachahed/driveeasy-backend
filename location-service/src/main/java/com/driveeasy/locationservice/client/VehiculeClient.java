package com.driveeasy.locationservice.client;

import com.driveeasy.locationservice.model.VehiculeDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;

@HttpExchange
public interface VehiculeClient {

    @GetExchange("/vehicules/{id}")
    VehiculeDTO getVehicule(@PathVariable Long id);

    @PatchExchange("/vehicules/{id}/statut")
    void changeStatut(@PathVariable Long id, @RequestParam String statut);
}