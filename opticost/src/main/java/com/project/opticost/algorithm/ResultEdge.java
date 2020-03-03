package com.project.opticost.algorithm;

import java.math.BigDecimal;

public class ResultEdge {
    String fromCity;
    String toCity;
    BigDecimal price;
    Integer flow;

    public ResultEdge(String fromCity, String toCity, BigDecimal price, Integer flow) {
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.price = price;
        this.flow = flow;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getFlow() {
        return flow;
    }

    public void setFlow(Integer flow) {
        this.flow = flow;
    }
}
