package com.project.opticost.algorithm;

import java.math.BigDecimal;
import java.util.Objects;

public class ResidualEdge  {
    private Vertex from;
    private Vertex to;
    private double flow;
    private BigDecimal price;
    private boolean result;

    public ResidualEdge(Vertex from, Vertex to, double flow, BigDecimal price, boolean result)
    {
        this.from = from;
        this.to = to;
        this.flow = flow;
        this.price = price;
        this.result = result;
    }

    public Vertex getFrom() {
        return from;
    }

    public void setFrom(Vertex from) {
        this.from = from;
    }

    public Vertex getTo() {
        return to;
    }

    public void setTo(Vertex to) {
        this.to = to;
    }

    public double getFlow() {
        return flow;
    }

    public void setFlow(double flow) {
        this.flow = flow;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResidualEdge edge = (ResidualEdge) o;
        return Objects.equals(from, edge.from) &&
                Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
