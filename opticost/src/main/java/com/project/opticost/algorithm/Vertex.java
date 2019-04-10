package com.project.opticost.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vertex {

    private static int nextSeq = 0;
    private String name;
    private int seq;
    private boolean isVisited;
    private double distance;
    private List<Vertex> parents;
    private List<Vertex> children;
    private Vertex parent;
    private double pi;
    private double nodeBalance;
    private double nodeImbalance;
    private Edge currentEdge;
    private List<Edge> edges;

    public Vertex(String name)
    {
        this.name = name;
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
        this.nodeBalance = 0;
    }

    public boolean removeChild(Vertex child)
    {
        return this.children.remove(child);
    }

    public void addChild(Vertex child)
    {
        this.children.add(child);
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<Vertex> getParents() {
        return parents;
    }

    public void setParents(List<Vertex> parents) {
        this.parents = parents;
    }

    public List<Vertex> getChildren() {
        return children;
    }

    public void setChildren(List<Vertex> children) {
        this.children = children;
    }

    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public double getPi() {
        return pi;
    }

    public void setPi(double pi) {
        this.pi = pi;
    }

    public double getNodeBalance() {
        return nodeBalance;
    }

    public void setNodeBalance(double nodeBalance) {
        this.nodeBalance = nodeBalance;
    }

    public double getNodeImbalance() {
        return nodeImbalance;
    }

    public void setNodeImbalance(double nodeImbalance) {
        this.nodeImbalance = nodeImbalance;
    }

    public Edge getCurrentEdge() {
        return currentEdge;
    }

    public void setCurrentEdge(Edge currentEdge) {
        this.currentEdge = currentEdge;
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
                "name='" + name + '\'' +
                '}';
    }
}
