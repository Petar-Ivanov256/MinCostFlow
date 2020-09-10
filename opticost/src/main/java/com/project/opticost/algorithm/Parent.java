package com.project.opticost.algorithm;

public class Parent {
    private Vertex parent;
    private ResidualEdge connection;

    public Parent(Vertex parent, ResidualEdge connection) {
        this.parent = parent;
        this.connection = connection;
    }

    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public ResidualEdge getConnection() {
        return connection;
    }

    public void setConnection(ResidualEdge connection) {
        this.connection = connection;
    }
}
