package com.driveeasy.clientservice.controller;

import com.driveeasy.clientservice.model.CategorieClient;
import com.driveeasy.clientservice.model.Client;
import com.driveeasy.clientservice.model.Eligibilite;
import com.driveeasy.clientservice.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public List<Client> findAll(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String cin,
            @RequestParam(required = false) String email) {
        return service.findAll(nom, cin, email);
    }

    @GetMapping("/{id}")
    public Client findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client create(@RequestBody Client client) {
        return service.create(client);
    }

    @PutMapping("/{id}")
    public Client update(@PathVariable Long id, @RequestBody Client client) {
        return service.update(id, client);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}/eligibilite")
    public Eligibilite verifierEligibilite(@PathVariable Long id) {
        return service.verifierEligibilite(id);
    }

    @PatchMapping("/{id}/categorie")
    public Client changerCategorie(@PathVariable Long id, @RequestParam CategorieClient categorie) {
        return service.changerCategorie(id, categorie);
    }
}