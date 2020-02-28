package com.project.opticost.algorithm;

import java.math.BigDecimal;
import java.util.Objects;

public class Edge {
    private Vertex from;
    private Vertex to;
    private double capacity;
    private BigDecimal price;

    public Edge(Vertex from, Vertex to, double capacity, BigDecimal price)
    {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.price = price;
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

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(from, edge.from) &&
                Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {

        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from.getName() +
                ", to=" + to.getName() +
                ", capacity=" + capacity +
                ", price=" + price +
                '}';
    }
}
