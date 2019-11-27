package com.project.opticost.utils.requests.helpers;

import java.util.List;

public class PlanRequstEntity {
    private Long id;
    private String planName;
    private List<RoadRequestEntity> roads;

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public List<RoadRequestEntity> getRoads() {
        return roads;
    }

    public void setRoads(List<RoadRequestEntity> roads) {
        this.roads = roads;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
