package com.project.opticost.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private final int adjMatrixSize = 20;

    private List<Edge> listOfEdges;
    private List<Vertex> listOfVertices;
    private Map<Vertex, List<Edge>> adjacencyList;
    private double[][] adjacencyMatrix;
    // TODO implement it with secound instance of Graph
    private double[][] residualGraph;
    private double[][] priceGraph;
    private double epsilon;

    public Graph() {

        this.listOfEdges = new ArrayList<>();
        this.listOfVertices = new ArrayList<>();
        this.adjacencyList = new HashMap<>();
        // TODO make method to take care of the size of the matrix
        this.adjacencyMatrix = new double[adjMatrixSize][adjMatrixSize];
    }

    public void addEdge(Edge newEdge) {
        // TODO Validations:
        // 1. Check for parallel arcs
        newEdge = this.updateEdges(newEdge);
        if (this.adjacencyList.containsKey(newEdge.getFrom())) {
            this.adjacencyList.get(newEdge.getFrom()).add(newEdge);
        } else {
            this.adjacencyList.put(newEdge.getFrom(), new ArrayList<>());
            this.adjacencyList.get(newEdge.getFrom()).add(newEdge);
        }

        this.updateAdjacencyMatrix();
    }

    private Edge updateEdges(Edge edge) {
        edge = this.updateVertices(edge);

        if (!listOfEdges.contains(edge)) {
            listOfEdges.add(edge);
        }

        return edge;
    }

    private Edge updateVertices(Edge edge) {
        if (!listOfVertices.contains(edge.getFrom())) {
            int seq = Vertex.getNextSeq();
            listOfVertices.add(edge.getFrom());
            edge.getFrom().setSeq(seq);
            Vertex.setNextSeq(seq++);
        } else {
            edge.setFrom(this.listOfVertices.stream()
                    .filter(x -> x.equals(edge.getFrom()))
                    .findAny().orElse(null));
        }

        if (!listOfVertices.contains(edge.getTo())) {
            int seq = Vertex.getNextSeq();
            listOfVertices.add(edge.getTo());
            edge.getTo().setSeq(seq);
            Vertex.setNextSeq(seq++);
        } else {
            edge.setTo(this.listOfVertices.stream()
                    .filter(x -> x.equals(edge.getTo()))
                    .findAny().orElse(null));
        }

        return edge;
    }

    public void removeVertex(Vertex rmVertex) {
        this.listOfVertices.remove(rmVertex);
        List<Edge> edgesToRemove = new ArrayList<>();
        for (Edge e : this.listOfEdges) {
            if (e.getFrom().equals(rmVertex) || e.getTo().equals(rmVertex)) {
                edgesToRemove.add(e);
            }
        }

        for (Edge e : edgesToRemove) {
            this.removeEdge(e);
        }

        for (int i = 0; i < adjMatrixSize; i++) {
            this.adjacencyMatrix[rmVertex.getSeq()][i] = 0;
            this.adjacencyMatrix[i][rmVertex.getSeq()] = 0;
        }

        this.adjacencyList.remove(rmVertex);
    }

    public boolean removeEdge(Edge rmEdge) {
        // TODO check what happens if you want to remove an Edge which is not in Graph
        boolean statusEdges = this.listOfEdges.remove(rmEdge);
        boolean statusVertices = this.adjacencyList.get(rmEdge.getFrom()).remove(rmEdge);
        this.adjacencyMatrix[rmEdge.getFrom().getSeq()][rmEdge.getTo().getSeq()] = 0;

        return statusEdges && statusVertices;
    }

    public double maxFlow(Vertex from, Vertex to) {
        residualGraph = new double[adjMatrixSize][adjMatrixSize];
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);
        Vertex end = this.listOfVertices.stream().filter(x -> x.equals(to)).findAny().orElse(null);

        // TODO make the size more reasonable
        for (int i = 0; i < adjMatrixSize; i++) {
            for (int j = 0; j < adjMatrixSize; j++) {
                this.residualGraph[i][j] = this.adjacencyMatrix[i][j];
            }
        }

        double maxFlow = 0d;
        while (BFS(start, end)) {
            double minFlow = double.MaxValue;
            // TODO if you use only parenst for BFS you can use only one Vertex
            Vertex path = end;
            while (path.getParents().size() != 0) {
                Vertex u = path.getParents().get(path.getParents().size() - 1);
                Vertex v = path;
                minFlow = Math.min(minFlow, residualGraph[u.getSeq()][v.getSeq()]);

                path = path.getParents().get(path.getParents().size() - 1);
            }

            path = end;
            while (path.getParents().size() != 0) {
                Vertex u = path.getParents().get(path.getParents().size() - 1);
                Vertex v = path;

                residualGraph[u.getSeq()][v.getSeq()] -= minFlow;
                residualGraph[v.getSeq()][u.getSeq()] += minFlow;

                path = path.getParents().get(path.getParents().size() - 1);
            }

            maxFlow += minFlow;
        }

        return maxFlow;
    }

    public Vertex findNegativeCycle(Vertex from)
    {
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);;

        for(Vertex v : this.listOfVertices)
        {
            v.setDistance(Integer.MAX_VALUE);
            v.setParents(new ArrayList<>());
        }
        start.setDistance(0);

        for (int i = 0; i < this.listOfVertices.size() - 1; i++)
        {
            for(Edge e : this.listOfEdges)
            {
                if (e.getTo().getDistance() > e.getFrom().getDistance() + e.getPrice())
                {
                    e.getTo().setDistance(e.getFrom().getDistance() + e .getPrice());
                    e.getTo().getParents().add(e.getFrom());
                }
            }
        }

        for (Edge e : this.listOfEdges)
        {
            if (e.getTo().getDistance() > e.getFrom().getDistance() + e.getPrice())
            {
                return e.getTo();
            }
        }

        return null;
    }
}
