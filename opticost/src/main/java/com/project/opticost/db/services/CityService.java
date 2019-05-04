package com.project.opticost.db.services;

import com.project.opticost.db.model.City;
import com.project.opticost.db.repo.CityRepository;
import com.project.opticost.db.services.interfaces.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CityService extends AbstractService<City,Long> {

    @Autowired
    CityRepository citiesRepo;

    @Override
    public JpaRepository<City, Long> getRepo() {
        return citiesRepo;
    }

    public City findByCityName(String cityName) {
        return citiesRepo.findByCityName(cityName);
    }

    public City createCityFromName(String cityName) {
        City result = this.findByCityName(cityName);

        if (result == null) {
            List<City> repoCities = citiesRepo.findAll();
            Integer maxX = repoCities.stream()
                    .max(Comparator.comparing(City::getxCoord))
                    .orElseThrow(NoSuchElementException::new)
                    .getxCoord();

            Integer maxY = repoCities.stream()
                    .max(Comparator.comparing(City::getyCoord))
                    .orElseThrow(NoSuchElementException::new)
                    .getyCoord();

            result = new City();

            result.setCityName(cityName);
            result.setxCoord(maxX + 1);
            result.setyCoord(maxY + 1);
            result = citiesRepo.saveAndFlush(result);
        }

        return result;
    }
}
