package com.driveeasy.clientservice.repository;

import com.driveeasy.clientservice.model.Client;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ClientRepository {

    private final List<Client> clients = new ArrayList<>();
    private Long nextId = 1L;

    public List<Client> findAll() {
        return clients;
    }

    public Optional<Client> findById(Long id) {
        return clients.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public Optional<Client> findByCin(String cin) {
        return clients.stream().filter(c -> c.getCin().equalsIgnoreCase(cin)).findFirst();
    }

    public Optional<Client> findByEmail(String email) {
        return clients.stream().filter(c -> c.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public Optional<Client> findByNumeroPermis(String numeroPermis) {
        return clients.stream().filter(c -> c.getNumeroPermis().equalsIgnoreCase(numeroPermis)).findFirst();
    }

    public Client save(Client client) {
        if (client.getId() == null) {
            client.setId(nextId++);
            clients.add(client);
            return client;
        }
        return update(client);
    }

    public Client update(Client client) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId().equals(client.getId())) {
                clients.set(i, client);
                return client;
            }
        }
        throw new RuntimeException("Client non trouvé : " + client.getId());
    }

    public void deleteById(Long id) {
        clients.removeIf(c -> c.getId().equals(id));
    }
}