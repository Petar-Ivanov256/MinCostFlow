package com.project.opticost.repo;

import com.project.opticost.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitiesRepository extends JpaRepository<City, Long> {
}
