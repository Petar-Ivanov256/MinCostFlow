package com.project.opticost.db.services;

import com.project.opticost.db.model.City;
import com.project.opticost.db.model.Plan;
import com.project.opticost.db.repo.CityRepository;
import com.project.opticost.db.services.interfaces.AbstractService;
import com.project.opticost.utils.exceptions.CitiesWithTheSameCoordinatesException;
import com.project.opticost.utils.exceptions.CitiesWithTheSameNameException;
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
            result.setxCoord(maxX + maxY/2);
            result.setyCoord(maxY + maxX/2);
            result = citiesRepo.saveAndFlush(result);
        }

        return result;
    }

    public boolean checkIfCityIsPresent(Plan plan, String cityName) {
        return plan.getRoads().stream().anyMatch(x -> x.getToCity().getCityName().equals(cityName)) ||
                plan.getRoads().stream().anyMatch(x -> x.getFromCity().getCityName().equals(cityName));
    }

    public void validate(List<City> cities) throws CitiesWithTheSameCoordinatesException, CitiesWithTheSameNameException {
        for (int i = 0; i < cities.size(); i++) {
            City curCity = cities.get(i);
            for (int j = 0; j < cities.size(); j++) {
                if(i == j){
                    continue;
                }
                City checkCity = cities.get(j);
                if(checkCity.getCityName().equals(curCity.getCityName())){
                    throw new CitiesWithTheSameNameException("The cities should be with unique names");
                }

                if(checkCity.getxCoord().equals(curCity.getxCoord()) &&
                        checkCity.getyCoord().equals(curCity.getyCoord())){
                    throw new CitiesWithTheSameCoordinatesException("The cites should have unique X and Y coordinates");
                }
            }
        }
    }
}
