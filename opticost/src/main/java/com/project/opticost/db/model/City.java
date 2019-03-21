package com.project.opticost.db.model;

import javax.persistence.*;

@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "city_name", unique = true)
    private String cityName;

    @Column(name = "x_coordinate")
    private Integer xCoord;

    @Column(name = "y_coordinate")
    private Integer yCoord;

//    @OneToMany(
//            mappedBy = "cities",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
//    private List<Road> roads = new ArrayList<>();
//
//
//    public void addRoad(Road road) {
//        roads.add(road);
    //TODO need to add the correct one
//        road.setPost(this);
//    }
//
//    public void removeRoad(Road road) {
//        roads.remove(road);
    //TODO need to remove the correct one
//        road.setPost(null);
//    }

    public City() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getxCoord() {
        return xCoord;
    }

    public void setxCoord(Integer xCoord) {
        this.xCoord = xCoord;
    }

    public Integer getyCoord() {
        return yCoord;
    }

    public void setyCoord(Integer yCoord) {
        this.yCoord = yCoord;
    }

    public void merge(City other){
        this.setId(other.getId());
        this.setCityName(other.getCityName());
        this.setxCoord(other.getxCoord());
        this.setyCoord(other.getyCoord());
    }
}
