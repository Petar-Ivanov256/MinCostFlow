package com.project.opticost.controller;

import com.project.opticost.model.City;
import com.project.opticost.repo.CitiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServiceController {

    @Autowired
    CitiesRepository citiesRepo;

    @RequestMapping(value = "/save-cities", method = RequestMethod.POST)
    public List<City> saveCities(@RequestBody List<City> cities) {
        //TODO return the saved cities (with the IDs)

        return citiesRepo.saveAll(cities);
    }
}
