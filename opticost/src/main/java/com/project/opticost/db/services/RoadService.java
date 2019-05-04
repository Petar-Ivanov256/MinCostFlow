package com.project.opticost.db.services;

import com.project.opticost.db.model.Road;
import com.project.opticost.db.repo.RoadRepository;
import com.project.opticost.db.services.interfaces.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class RoadService extends AbstractService<Road, Long> {

    @Autowired
    RoadRepository roadsRepo;

    @Override
    public JpaRepository<Road, Long> getRepo() {
        return roadsRepo;
    }
}
