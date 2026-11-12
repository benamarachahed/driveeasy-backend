package com.driveeasy.locationservice.repository;

import com.driveeasy.locationservice.model.Location;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LocationRepository {

    private final List<Location> locations = new ArrayList<>();
    private Long nextId = 1L;

    public List<Location> findAll() {
        return locations;
    }

    public Optional<Location> findById(Long id) {
        return locations.stream().filter(l -> l.getId().equals(id)).findFirst();
    }

    public Location save(Location location) {
        if (location.getId() == null) {
            location.setId(nextId++);
            locations.add(location);
            return location;
        }
        return update(location);
    }

    public Location update(Location location) {
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getId().equals(location.getId())) {
                locations.set(i, location);
                return location;
            }
        }
        throw new RuntimeException("Location non trouvée : " + location.getId());
    }

    public void deleteById(Long id) {
        locations.removeIf(l -> l.getId().equals(id));
    }
}