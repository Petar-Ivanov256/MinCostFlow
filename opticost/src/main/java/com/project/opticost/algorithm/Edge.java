package com.project.opticost.algorithm;

import java.util.Objects;

public class Edge {
    private Vertex from;
    private Vertex to;
    private int capacity;
    private double price;

    public Edge(Vertex from, Vertex to, int capacity, double price)
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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
}
