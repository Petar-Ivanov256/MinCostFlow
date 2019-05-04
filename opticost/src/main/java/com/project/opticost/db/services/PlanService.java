package com.project.opticost.db.services;

import com.project.opticost.db.model.Plan;
import com.project.opticost.db.repo.PlanRepository;
import com.project.opticost.db.services.interfaces.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PlanService extends AbstractService<Plan, Long> {

    @Autowired
    PlanRepository planRepo;

    @Override
    public JpaRepository<Plan, Long> getRepo() {
        return planRepo;
    }

    public Plan findByPlanName(String planName) {
        return planRepo.findByPlanName(planName);
    }
}

