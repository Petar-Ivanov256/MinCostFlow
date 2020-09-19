package com.project.opticost.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vertex {

    private static int nextSeq = 0;
    private String name;
    private int seq;
    private boolean isVisited;
    private Double distance;
    private List<Vertex> parents;
    private List<Edge> edges;
    private Parent parent;

    public Vertex(String name)
    {
        this.name = name;
        this.parents = new ArrayList<>();
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public boolean removeParent(Vertex parent)
    {
        return this.parents.remove(parent);
    }

    public void addParent(Vertex parent)
    {
        this.parents.add(parent);
    }

    public static int getNextSeq() {
        return nextSeq;
    }

    public static void setNextSeq(int nextSeq) {
        Vertex.nextSeq = nextSeq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public List<Vertex> getParents() {
        return parents;
    }

    public void setParents(List<Vertex> parents) {
        this.parents = parents;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(name, vertex.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "name='" + name + "\' " +
                "distance=" + distance +
                '}';
    }
}
