package com.driveeasy.clientservice.service;

import com.driveeasy.clientservice.exception.ClientNotFoundException;
import com.driveeasy.clientservice.exception.DuplicataException;
import com.driveeasy.clientservice.exception.RegleMetierException;
import com.driveeasy.clientservice.model.CategorieClient;
import com.driveeasy.clientservice.model.Client;
import com.driveeasy.clientservice.model.Eligibilite;
import com.driveeasy.clientservice.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<Client> findAll(String nom, String cin, String email) {
        return repository.findAll().stream()
                .filter(c -> nom == null || (c.getNom() + " " + c.getPrenom()).toLowerCase().contains(nom.toLowerCase()))
                .filter(c -> cin == null || c.getCin().equalsIgnoreCase(cin))
                .filter(c -> email == null || c.getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }

    public Client findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }

    public Client create(Client client) {
        if (repository.findByCin(client.getCin()).isPresent()) {
            throw new DuplicataException("Le CIN existe déjà : " + client.getCin());
        }
        if (repository.findByEmail(client.getEmail()).isPresent()) {
            throw new DuplicataException("L'email existe déjà : " + client.getEmail());
        }
        if (repository.findByNumeroPermis(client.getNumeroPermis()).isPresent()) {
            throw new DuplicataException("Le numéro de permis existe déjà : " + client.getNumeroPermis());
        }
        if (client.getDateInscription() == null) {
            client.setDateInscription(LocalDate.now());
        }
        if (client.getCategorieClient() == null) {
            client.setCategorieClient(CategorieClient.STANDARD);
        }
        return repository.save(client);
    }

    public Client update(Long id, Client client) {
        Client existing = findById(id);
        repository.findByCin(client.getCin())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> { throw new DuplicataException("Le CIN existe déjà : " + client.getCin()); });
        repository.findByEmail(client.getEmail())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> { throw new DuplicataException("L'email existe déjà : " + client.getEmail()); });
        repository.findByNumeroPermis(client.getNumeroPermis())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> { throw new DuplicataException("Le numéro de permis existe déjà : " + client.getNumeroPermis()); });
        client.setId(existing.getId());
        client.setDateInscription(existing.getDateInscription());
        return repository.update(client);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    public Eligibilite verifierEligibilite(Long id) {
        Client client = findById(id);
        int age = Period.between(client.getDateNaissance(), LocalDate.now()).getYears();
        if (age < 21) {
            return new Eligibilite(false, "Le client doit avoir au moins 21 ans (actuel : " + age + ")");
        }
        if (client.getDateExpirationPermis() == null || client.getDateExpirationPermis().isBefore(LocalDate.now())) {
            return new Eligibilite(false, "Le permis de conduire est expiré");
        }
        return new Eligibilite(true, "Client éligible");
    }

    public Client changerCategorie(Long id, CategorieClient categorie) {
        Client client = findById(id);
        client.setCategorieClient(categorie);
        return repository.update(client);
    }

    public Client incrementerLocations(Long id) {
        Client client = findById(id);
        int nbLocations = 0;
        if (client.getCategorieClient() == CategorieClient.STANDARD) {
            client.setCategorieClient(CategorieClient.FIDELE);
        } else if (client.getCategorieClient() == CategorieClient.FIDELE) {
            client.setCategorieClient(CategorieClient.VIP);
        }
        return repository.update(client);
    }
}