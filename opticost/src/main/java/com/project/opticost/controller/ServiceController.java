package com.project.opticost.controller;

import com.project.opticost.algorithm.Edge;
import com.project.opticost.algorithm.Graph;
import com.project.opticost.algorithm.ResultEdge;
import com.project.opticost.algorithm.Vertex;
import com.project.opticost.db.model.City;
import com.project.opticost.db.model.Plan;
import com.project.opticost.db.model.Road;
import com.project.opticost.db.services.CityService;
import com.project.opticost.db.services.PlanService;
import com.project.opticost.db.services.RoadService;
import com.project.opticost.utils.exceptions.*;
import com.project.opticost.utils.requests.helpers.MinCostResultRequestEntity;
import com.project.opticost.utils.requests.helpers.MultiCostRequestEntity;
import com.project.opticost.utils.requests.helpers.PlanRequstEntity;
import com.project.opticost.utils.requests.helpers.RoadRequestEntity;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class ServiceController {

    @Autowired
    RoadService roadService;

    @Autowired
    CityService cityService;

    @Autowired
    PlanService planService;

    @RequestMapping(value = "/save-cities", method = RequestMethod.POST)
    public List<City> saveCities(@RequestBody List<City> cities) throws CitiesWithTheSameCoordinatesException, CitiesWithTheSameNameException {
        cityService.validate(cities);
        for (City city : cities) {
            City dbCity = cityService.findByCityName(city.getCityName());
            if (dbCity != null) {
                city.setId(dbCity.getId());
            }
        }
        return cityService.saveAll(cities);
    }

    @RequestMapping(value = "/save-roads/{planId}", method = RequestMethod.POST)
    public List<Road> saveRoads(@RequestBody List<RoadRequestEntity> roads, @PathVariable("planId") Long planId) throws RoadsWithTheSameFromToException {
        roadService.validate(roads);
        return roadService.persistRoads(roads, planId);
    }

    @RequestMapping(value = "/plans", method = RequestMethod.GET)
    public List<Plan> gePlans() {
        return planService.findAll();
    }

    @RequestMapping(value = "/save-plan", method = RequestMethod.POST)
    public Plan savePlan(@RequestBody PlanRequstEntity plan) {
        Plan planEntity = new Plan();
        Plan dbPlan = planService.findByPlanName(plan.getPlanName());

        if (dbPlan != null) {
            planEntity.setId(dbPlan.getId());
        }

        planEntity.setPlanName(plan.getPlanName());
        planEntity.setRoads(roadService.extractRoads(plan.getRoads()));

        return planService.saveAndFlush(planEntity);
    }

    @RequestMapping(value = "/update-plan", method = RequestMethod.PUT)
    public Plan updatePlan(@RequestBody PlanRequstEntity plan) throws PlanNotInDataBaseException {
        Plan dbPlan = planService.getOne(plan.getId());
        if (dbPlan == null) {
            throw new PlanNotInDataBaseException("Can't find a plan for update");
        }
        dbPlan.setPlanName(plan.getPlanName());

        return planService.saveAndFlush(dbPlan);
    }

    @RequestMapping(value = "/delete-plan/{id}", method = RequestMethod.DELETE)
    public void deletePlan(@PathVariable("id") Long id) {
        planService.deleteById(id);
    }

    @ResponseBody
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/process-file")
    public Plan processInputCSV(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartFile multipartFile = request.getFile("file");
        InputStream stream = multipartFile.getInputStream();
        String[] data = IOUtils.toString(stream, StandardCharsets.UTF_8).split("\\r?\\n");
        List<String> lines = new ArrayList<>(Arrays.asList(data));
        List<Road> result = new ArrayList<>();

        Plan existingPlan = planService.findByPlanName(multipartFile.getOriginalFilename());
        if(existingPlan != null){
            planService.getRepo().delete(existingPlan);
        }
        Plan plan = new Plan();
        plan.setPlanName(multipartFile.getOriginalFilename());

        for (int i = 1; i < lines.size(); i++) {
            String[] content = lines.get(i).split(",");
            String fromCityName = content[0].trim();
            String toCityName = content[1].trim();
            Integer capacity = Integer.parseInt(content[2].trim());
            BigDecimal price = new BigDecimal(content[3].trim());
            City fromCity = cityService.createCityFromName(fromCityName);
            City toCity = cityService.createCityFromName(toCityName);

            Road edge = new Road();
            edge.setFromCity(fromCity);
            edge.setToCity(toCity);
            edge.setPrice(price);
            edge.setCapacity(capacity);
            edge.setPlan(plan);

            result.add(edge);
        }

        plan.setRoads(result);

        return planService.saveAndFlush(plan);
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public List<MinCostResultRequestEntity> runMinCostFlow(@RequestBody MultiCostRequestEntity run) throws Exception {
        Graph graph = new Graph();
        Plan plan = planService.findByPlanName(run.getSelectedPlan());
        Vertex fromCity = null;
        Vertex toCity = null;

        Set<City> citySet = new HashSet<>();

        if (plan != null) {
            if (cityService.checkIfCityIsPresent(plan, run.getFromCity()) && cityService.checkIfCityIsPresent(plan, run.getToCity())) {
                fromCity = new Vertex(run.getFromCity());
                toCity = new Vertex(run.getToCity());
            }else {
                throw new CitiesNotInPlanException("Corrupted request: used cities are not in the Plan");
            }

            for (Road road : plan.getRoads()) {
                citySet.add(road.getFromCity());
                citySet.add(road.getToCity());
            }

            List<Vertex> vertices = new ArrayList<>();
            for (City city : citySet) {
                vertices.add(new Vertex(city.getCityName()));
            }

            for (Road road : plan.getRoads()) {
                Vertex fromVertex = vertices.stream()
                        .filter(x -> x.getName().equals(road.getFromCity().getCityName()))
                        .findFirst()
                        .get();

                Vertex toVertex = vertices.stream()
                        .filter(x -> x.getName().equals(road.getToCity().getCityName()))
                        .findFirst()
                        .get();

                graph.addEdge(new Edge(fromVertex, toVertex, road.getCapacity(), road.getPrice()));
            }
        }

        graph.minCostFlowCycleCancel(fromCity, toCity, run.getCargo());
        graph.printGraphMinCostFlow();

        List<ResultEdge> redges = graph.getResult();
        List<MinCostResultRequestEntity> result = new ArrayList<>();
        for (ResultEdge redge : redges) {
            City from = cityService.findByCityName(redge.getFromCity());
            City to = cityService.findByCityName(redge.getToCity());
            Road road = roadService.findRoadByFromCityAndToCityAndPlan(from, to, plan);
            if(road == null){
                throw new ResultRoadNotFoundInTheDatabaseException("Corrupted result: mismatch between the database and the result");
            }
            result.add(new MinCostResultRequestEntity(road, redge.getPrice(), redge.getFlow()));
        }

        return result;
    }
}
