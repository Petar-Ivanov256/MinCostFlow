package com.project.opticost.db.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(
            mappedBy = "plan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Road> roads = new ArrayList<>();


    public void addRoad(Road road) {
        roads.add(road);
        road.setPlan(this);
    }

    public void removeRoad(Road road) {
        roads.remove(road);
        road.setPlan(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanName() {
        return name;
    }

    public void setPlanName(String planName) {
        this.name = planName;
    }

    public List<Road> getRoads() {
        return roads;
    }

    public void setRoads(List<Road> roads) {
        this.roads = roads;
    }
}
