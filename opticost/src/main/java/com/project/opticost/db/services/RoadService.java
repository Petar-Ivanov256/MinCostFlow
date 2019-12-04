package com.project.opticost.db.services;

import com.project.opticost.db.model.City;
import com.project.opticost.db.model.Plan;
import com.project.opticost.db.model.Road;
import com.project.opticost.db.repo.RoadRepository;
import com.project.opticost.db.services.interfaces.AbstractService;
import com.project.opticost.utils.exceptions.RoadsWithNotMatchingPlanException;
import com.project.opticost.utils.requests.helpers.RoadRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoadService extends AbstractService<Road, Long> {

    @Autowired
    RoadRepository roadsRepo;

    @Autowired
    CityService cityService;

    @Autowired
    PlanService planService;

    @Override
    public JpaRepository<Road, Long> getRepo() {
        return roadsRepo;
    }

    public List<Road> persistRoads(List<RoadRequestEntity> roads, Long planId) throws RoadsWithNotMatchingPlanException {
        List<Road> extractedRoads = extractRoads(roads);
        roadsRepo.deleteAll(roadsRepo.findRoadsByPlan(planService.getOne(planId)));
        return roadsRepo.saveAll(extractedRoads);
    }

//    public Plan getPlanOfTheRoads(List<Road> roads) throws RoadsWithNotMatchingPlanException {
//        Plan plan = roads.get(0).getPlan();
//
//        for(Road road: roads){
//            if(!road.getPlan().getId().equals(plan.getId())){
//                throw new RoadsWithNotMatchingPlanException();
//            }
//        }
//
//        return plan;
//    }

    public List<Road> extractRoads(List<RoadRequestEntity> roads) {
        List<Road> results = new ArrayList<>();
        for (RoadRequestEntity road : roads) {
            City fromCity = cityService.findByCityName(road.getFromCity());
            City toCity = cityService.findByCityName(road.getToCity());
            Plan plan = planService.findByPlanName(road.getPlanName());

            if (fromCity != null && toCity != null) {
                Road roadEntity = new Road();
                roadEntity.setFromCity(fromCity);
                roadEntity.setToCity(toCity);
                roadEntity.setCapacity(road.getCapacity());
                roadEntity.setPrice(road.getPrice());
                roadEntity.setPlan(plan);

                results.add(roadEntity);
            }
        }

        return results;
    }
}
