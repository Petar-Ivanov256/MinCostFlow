package com.project.opticost.algorithm;

public class Parent {
    private Vertex vertex;
    private ResidualEdge residualEdge;

    public Parent(Vertex parent, ResidualEdge residualEdge) {
        this.vertex = parent;
        this.residualEdge = residualEdge;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    public ResidualEdge getResidualEdge() {
        return residualEdge;
    }

    public void setResidualEdge(ResidualEdge residualEdge) {
        this.residualEdge = residualEdge;
    }
}
