package com.project.opticost.utils.requests.helpers;

import com.project.opticost.db.model.Road;

import java.math.BigDecimal;

public class MinCostResultRequestEntity {
    private Road edge;
    private BigDecimal price;
    private Integer flow;

    public MinCostResultRequestEntity(Road edge, BigDecimal price, Integer flow) {
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

    public Integer getFlow() {
        return flow;
    }

    public void setFlow(Integer flow) {
        this.flow = flow;
    }
}
