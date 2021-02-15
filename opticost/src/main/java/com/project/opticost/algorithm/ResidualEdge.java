package com.project.opticost.algorithm;

import java.math.BigDecimal;
import java.util.Objects;

public class ResidualEdge  {
    private Vertex from;
    private Vertex to;
    private Integer capacity;
    private BigDecimal price;
    private boolean result;
    private ResidualEdge mirrorEdge;

    private boolean traversed;

    public ResidualEdge(Vertex from, Vertex to, Integer capacity, BigDecimal price, boolean result)
    {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.price = price;
        this.result = result;
        this.traversed = false;
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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

    public ResidualEdge getMirrorEdge() {
        return mirrorEdge;
    }

    public void setMirrorEdge(ResidualEdge mirrorEdge) {
        this.mirrorEdge = mirrorEdge;
    }

    public boolean isTraversed() {
        return traversed;
    }

    public void setTraversed(boolean traversed) {
        this.traversed = traversed;
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

    @Override
    public String toString() {
        return "ResidualEdge{" +
                "from=" + from.getName() +
                ", to=" + to.getName() +
                ", flow=" + capacity +
                ", price=" + price +
                ", result=" + result +
                '}';
    }
}
