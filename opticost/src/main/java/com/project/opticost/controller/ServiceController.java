package com.project.opticost.controller;

import com.project.opticost.db.model.City;
import com.project.opticost.db.model.Road;
import com.project.opticost.db.repo.CitiesRepository;
import com.project.opticost.db.repo.RoadsRepository;
import com.project.opticost.utils.requests.helpers.RoadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ServiceController {

    @Autowired
    CitiesRepository citiesRepo;

    @Autowired
    RoadsRepository roadsRepo;

    @RequestMapping(value = "/save-cities", method = RequestMethod.POST)
    public List<City> saveCities(@RequestBody List<City> cities) {
        return citiesRepo.saveAll(cities);
    }

    @RequestMapping(value = "/save-roads", method = RequestMethod.POST)
    public List<Road> saveRoads(@RequestBody List<RoadRequest> roads) {


        return roadsRepo.saveAll(new ArrayList<>());
    }
}
