package com.project.opticost.db.services;

import com.project.opticost.db.model.City;
import com.project.opticost.db.model.Plan;
import com.project.opticost.db.model.Road;
import com.project.opticost.db.repo.RoadRepository;
import com.project.opticost.db.services.interfaces.AbstractService;
import com.project.opticost.utils.exceptions.RoadsWithTheSameFromToException;
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

    public List<Road> persistRoads(List<RoadRequestEntity> roads, Long planId) {
        List<Road> extractedRoads = extractRoads(roads);
        Plan plan = planService.getOne(planId);
        roadsRepo.deleteAll(roadsRepo.findRoadsByPlan(plan));
        extractedRoads.forEach(x -> x.setPlan(plan));
        return roadsRepo.saveAll(extractedRoads);
    }

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

    public Road findRoadByFromCityAndToCityAndPlan(City fromCity, City toCity, Plan plan) {
        return roadsRepo.findRoadByFromCityAndToCityAndPlan(fromCity, toCity, plan);
    }

    public void validate(List<RoadRequestEntity> roads) throws RoadsWithTheSameFromToException {
        for (int i = 0; i < roads.size(); i++) {
            RoadRequestEntity currRoad = roads.get(i);
            for (int j = 0; j < roads.size(); j++) {
                if (i == j) {
                    continue;
                }
                RoadRequestEntity checkRoad = roads.get(j);
                if(checkRoad.getFromCity().equals(currRoad.getFromCity()) && checkRoad.getToCity().equals(currRoad.getToCity())){
                    throw new RoadsWithTheSameFromToException("There should not be roads with same start and destination");
                }
            }

        }
    }

}
