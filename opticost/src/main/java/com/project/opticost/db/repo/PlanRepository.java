package com.project.opticost.db.repo;

import com.project.opticost.db.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Plan findByPlanName(String cityName);
}