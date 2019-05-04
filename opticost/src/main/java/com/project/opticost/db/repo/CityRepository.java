package com.project.opticost.db.repo;

import com.project.opticost.db.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    City findByCityName(String cityName);
}
