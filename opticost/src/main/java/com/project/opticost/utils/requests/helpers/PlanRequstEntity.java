package com.project.opticost.utils.requests.helpers;

import java.util.List;

public class PlanRequstEntity {
    private String name;
    private List<RoadRequestEntity> roads;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoadRequestEntity> getRoads() {
        return roads;
    }

    public void setRoads(List<RoadRequestEntity> roads) {
        this.roads = roads;
    }
}
