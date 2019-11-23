package com.project.opticost.db.services;

import com.project.opticost.db.model.Road;
import com.project.opticost.db.repo.RoadRepository;
import com.project.opticost.db.services.interfaces.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoadService extends AbstractService<Road, Long> {

    @Autowired
    RoadRepository roadsRepo;

    @Override
    public JpaRepository<Road, Long> getRepo() {
        return roadsRepo;
    }

    public List<Road> saveOrUpdateRoads(List<Road> roads){
        List<Road> roadsToSave = new ArrayList<>();
        for(Road road: roads){
            Road r = roadsRepo.findRoadByFromCityAndToCityAndPlan(road.getFromCity(), road.getToCity(), road.getPlan());
            if (r == null){
                roadsToSave.add(road);
            }else{
                r.setCapacity(road.getCapacity());
                r.setPrice(road.getPrice());
                roadsToSave.add(r);
            }
        }

        return roadsRepo.saveAll(roadsToSave);
    }
}
