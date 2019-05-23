package com.project.opticost.utils.requests.helpers;

import com.project.opticost.db.model.Road;

import java.math.BigDecimal;

public class MinCostResultRequestEntity {
    Road edge;
    BigDecimal price;
    Double flow;

    public MinCostResultRequestEntity(Road edge, BigDecimal price, Double flow) {
        this.edge = edge;
        this.price = price;
        this.flow = flow;
    }

    public Road getEdge() {
        return edge;
    }

    public void setEdge(Road edge) {
        this.edge = edge;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getFlow() {
        return flow;
    }

    public void setFlow(Double flow) {
        this.flow = flow;
    }
}
