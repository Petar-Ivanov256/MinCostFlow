package com.project.opticost.controller;

import com.project.opticost.algorithm.Edge;
import com.project.opticost.algorithm.Graph;
import com.project.opticost.algorithm.Vertex;
import com.project.opticost.db.model.City;
import com.project.opticost.db.model.Road;
import com.project.opticost.db.services.CityService;
import com.project.opticost.db.services.RoadService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class ServiceController {

    @Autowired
    RoadService roadService;

    @Autowired
    CityService cityService;

    @RequestMapping(value = "/save-cities", method = RequestMethod.POST)
    public List<City> saveCities(@RequestBody List<City> cities) {
        for (City city : cities) {
            City dbCity = cityService.findByCityName(city.getCityName());
            if (dbCity != null) {
                city.setId(dbCity.getId());
            }
        }
        return cityService.saveAll(cities);
    }

    @RequestMapping(value = "/save-roads", method = RequestMethod.POST)
    public List<Road> saveRoads(@RequestBody List<RoadRequestEntity> roads) {
        List<Road> results = new ArrayList<>();
        for (RoadRequestEntity road : roads) {
            City fromCity = cityService.findByCityName(road.getFromCity());
            City toCity = cityService.findByCityName(road.getToCity());

            if(fromCity != null && toCity != null){
                Road roadEntity = new Road();
                roadEntity.setFromCity(fromCity);
                roadEntity.setToCity(toCity);
                roadEntity.setCapacity(road.getCapacity());
                roadEntity.setPrice(road.getPrice());

                results.add(roadEntity);
            }
        }
        return roadService.saveAll(results);
    }

    @ResponseBody
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/process-file")
    public Object processInputCSV(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartFile multipartFile = request.getFile("file");
        InputStream stream = multipartFile.getInputStream();
        String[] data = IOUtils.toString(stream, StandardCharsets.UTF_8).split("\\r?\\n");
        List<String> lines = new ArrayList<>(Arrays.asList(data));
        List<Road> result = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String[] content = lines.get(i).split(",");
            String fromCityName = content[0];
            String toCityName = content[1];
            Double capacity = Double.parseDouble(content[2]);
            BigDecimal price = new BigDecimal(content[3]);
            City fromCity = cityService.createCityFromName(fromCityName);
            City toCity = cityService.createCityFromName(toCityName);

            Road edge =  new Road();
            edge.setFromCity(fromCity);
            edge.setToCity(toCity);
            edge.setPrice(price);
            edge.setCapacity(capacity);

            roadService.saveAndFlush(edge);
            result.add(edge);
        }

//        Function<String, Road> mapToItem = (line) -> {
//            String[] data = line.split(",");
//
//
//            Road road = new Road();
//
//
//            return road;
//        };
//
//        List<VodInput> listVodInput = list.stream().skip(1).map(mapToItem).collect(Collectors.toList());
//        listVodInput.sort((v1, v2) -> {
//            if (v1.getTitle().equals(v2.getTitle())) {
//                if (v1.getSeason().equals(v2.getSeason())) {
//                    return v1.getEpisode().compareTo(v2.getEpisode());
//                } else {
//                    return v1.getSeason().compareTo(v2.getSeason());
//                }
//            } else {
//                return v1.getTitle().compareTo(v2.getTitle());
//            }
//        });

        return result;
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public void runMinCostFlow() throws Exception {
        Graph graph = new Graph();

        Vertex v0 = new Vertex("0");
        Vertex v1 = new Vertex("1");
        Vertex v2 = new Vertex("2");

        graph.addEdge(new Edge(v0, v1, 2, 7));
        graph.addEdge(new Edge(v0, v2, 10, 16));
        graph.addEdge(new Edge(v1, v2, 5, 4));


//        graph.minCostFlowCostScaling(new Vertex("0"), new Vertex("2"), 4);
        graph.minCostFlowCycleCancel(new Vertex("0"), new Vertex("2"), 4);
        graph.printGraphMinCostFlow();
    }

}
