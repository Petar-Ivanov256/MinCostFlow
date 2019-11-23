package com.project.opticost.db.repo;

import com.project.opticost.db.model.City;
import com.project.opticost.db.model.Plan;
import com.project.opticost.db.model.Road;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadRepository extends JpaRepository<Road, Long> {
    Road findRoadByFromCityAndToCityAndPlan(City fromCity, City toCity, Plan plan);
}
