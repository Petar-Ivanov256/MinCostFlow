package com.project.opticost.algorithm;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private List<Vertex> verticesPath;
    private List<Edge> edgesPath;

    public Path()
    {
        this.verticesPath = new ArrayList<>();
        this.edgesPath = new ArrayList<>();
    }

    public void addVertexToPath(Vertex vertex)
    {
        this.verticesPath.add(vertex);
    }

    public void addEdgeToPath(Edge edge)
    {
        this.edgesPath.add(edge);
    }

    public void resetPath()
    {
        this.verticesPath.clear();
        this.edgesPath.clear();
    }

    public List<Vertex> getVerticesPath() {
        return verticesPath;
    }

    public void setVerticesPath(List<Vertex> verticesPath) {
        this.verticesPath = verticesPath;
    }

    public List<Edge> getEdgesPath() {
        return edgesPath;
    }

    public void setEdgesPath(List<Edge> edgesPath) {
        this.edgesPath = edgesPath;
    }
}
