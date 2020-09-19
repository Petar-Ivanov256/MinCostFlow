package com.project.opticost.algorithm;

import com.project.opticost.utils.exceptions.NoFeasibleSolutionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Graph {
    private List<Edge> listOfEdges;
    private List<Vertex> listOfVertices;
    private Map<Vertex, List<Edge>> adjacencyList;
    private Map<Vertex, List<ResidualEdge>> residualGraph;
    private BigDecimal minCostFlow;

    public Graph() {
        this.listOfEdges = new ArrayList<>();
        this.listOfVertices = new ArrayList<>();
        this.adjacencyList = new HashMap<>();
        this.residualGraph = new HashMap<>();
    }

    public BigDecimal getMinCostFlow() {
        return minCostFlow;
    }

    public void addEdge(Edge newEdge) {
        newEdge = this.updateEdges(newEdge);
        if (this.adjacencyList.containsKey(newEdge.getFrom())) {
            this.adjacencyList.get(newEdge.getFrom()).add(newEdge);
        } else {
            this.adjacencyList.put(newEdge.getFrom(), new ArrayList<>());
            this.adjacencyList.get(newEdge.getFrom()).add(newEdge);
        }
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
            Vertex.setNextSeq(seq + 1);
        } else {
            edge.setFrom(this.listOfVertices.stream()
                    .filter(x -> x.equals(edge.getFrom()))
                    .findAny().orElse(null));
        }

        if (!listOfVertices.contains(edge.getTo())) {
            int seq = Vertex.getNextSeq();
            listOfVertices.add(edge.getTo());
            edge.getTo().setSeq(seq);
            Vertex.setNextSeq(seq + 1);
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
        this.adjacencyList.remove(rmVertex);
    }

    public boolean removeEdge(Edge rmEdge) {
        boolean statusEdges = this.listOfEdges.remove(rmEdge);
        boolean statusVertices = this.adjacencyList.get(rmEdge.getFrom()).remove(rmEdge);

        return statusEdges && statusVertices;
    }

    public Integer maxFlow(Vertex from, Vertex to) {
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);
        Vertex end = this.listOfVertices.stream().filter(x -> x.equals(to)).findAny().orElse(null);

        buildResidualGraph();

        Integer maxFlow = 0;
        while (BFS(start, end)) {
            Integer minFlow = Integer.MAX_VALUE;
            Vertex path = end;
            List<ResidualEdge> pathEdges = new ArrayList<>();
            while (path.getParents().size() != 0) {
                Vertex u = path.getParents().get(path.getParents().size() - 1);
                Vertex v = path;

                ResidualEdge edge = residualGraph.get(u).stream()
                        .filter(x -> x.getTo().equals(v) && x.getFlow() > 0)
                        .findAny().orElseThrow(NoSuchElementException::new);

                minFlow = Math.min(minFlow, edge.getFlow());

                path = path.getParents().get(path.getParents().size() - 1);
                pathEdges.add(edge);
            }

            for (ResidualEdge pathEdge : pathEdges) {
                pathEdge.setFlow(pathEdge.getFlow() - minFlow);
                pathEdge.getMirrorEdge().setFlow(pathEdge.getMirrorEdge().getFlow() + minFlow);
            }

            maxFlow += minFlow;
        }

        return maxFlow;
    }

    public Vertex findNegativeCycleInResidualGraph(Vertex from) {
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);
        for (Vertex v : this.listOfVertices) {
            v.setDistance((double) Integer.MAX_VALUE);
            v.setParents(new ArrayList<>());
        }
        start.setDistance(0d);
        for (int i = 0; i < this.listOfVertices.size() - 1; i++) {
            for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph.entrySet()) {
                Vertex uVertex = this.listOfVertices.stream().filter(x -> x.equals(entry.getKey())).findAny().orElse(null);
                for (ResidualEdge e : entry.getValue()) {
                    if (e.getFlow() > 0) {
                        Vertex vVertex = this.listOfVertices.stream().filter(x -> x.equals(e.getTo())).findAny().orElse(null);
                        if (vVertex.getDistance() > uVertex.getDistance() + e.getPrice().doubleValue()) {
                            vVertex.setDistance(uVertex.getDistance() + e.getPrice().doubleValue());
                            vVertex.setParent(new Parent(uVertex, e));
                        }
                    }
                }
            }
        }

        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph.entrySet()) {
            Vertex uVertex = this.listOfVertices.stream().filter(x -> x.equals(entry.getKey())).findAny().orElse(null);
            for (ResidualEdge e : entry.getValue()) {
                if (e.getFlow() > 0) {
                    Vertex vVertex = this.listOfVertices.stream().filter(x -> x.equals(e.getTo())).findAny().orElse(null);
                    if (vVertex.getDistance() > uVertex.getDistance() + e.getPrice().doubleValue()) {
                        vVertex.setParent(new Parent(uVertex, e));
                        return vVertex;
                    }
                }
            }
        }
        return null;
    }

    public void minCostFlowCycleCancel(Vertex from, Vertex to, Integer cargo) throws Exception {
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);
        Vertex end = this.listOfVertices.stream().filter(x -> x.equals(to)).findAny().orElse(null);

        Vertex source = new Vertex("s");
        Vertex dest = new Vertex("t");

        this.addEdge(new Edge(source, start, cargo, BigDecimal.valueOf(0)));
        this.addEdge(new Edge(end, dest, cargo, BigDecimal.valueOf(0)));
        Integer maxFlow = this.maxFlow(source, dest);

        if (maxFlow < cargo) {
            throw new NoFeasibleSolutionException("There is no feasible solution for the supply: " + cargo + "." +
                    "The max flow between the two cities is: " + maxFlow);
        } else {
            this.establishFeasibleFLow(source, dest);
        }

        Vertex vertexInLoopPath = this.findNegativeCycleInResidualGraph(start);
        while (vertexInLoopPath != null) {
            Vertex parent = vertexInLoopPath.getParent().getVertex();
            List<Vertex> cycle = new ArrayList<>();
            cycle.add(vertexInLoopPath);
            while (!cycle.contains(parent)) {
                cycle.add(parent);
                parent = parent.getParent().getVertex();
            }
            cycle = cycle.subList(cycle.indexOf(parent), cycle.size());

            List<ResidualEdge> singleEdges = new ArrayList<>();
            for (Vertex v : cycle) {
                singleEdges.add(v.getParent().getResidualEdge());
            }

            Integer minRFlow = singleEdges.stream().min(Comparator.comparing(ResidualEdge::getFlow)).get().getFlow();
            for (ResidualEdge singleEdge : singleEdges) {
                Integer flowMinus = singleEdge.getFlow();
                singleEdge.setFlow(flowMinus - minRFlow);

                Integer flowPlus = singleEdge.getMirrorEdge().getFlow();
                singleEdge.getMirrorEdge().setFlow(flowPlus + minRFlow);
            }
            vertexInLoopPath = this.findNegativeCycleInResidualGraph(start);
        }
    }

    private void establishFeasibleFLow(Vertex source, Vertex dest) {
        this.removeVertex(source);
        this.removeVertex(dest);

        this.residualGraph.remove(source);
        this.residualGraph.remove(dest);

        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph.entrySet()) {
            List<ResidualEdge> toRemove = new ArrayList<>();
            for (ResidualEdge e : entry.getValue()) {
                if (e.getFrom().equals(source) ||
                        e.getTo().equals(source) ||
                        e.getFrom().equals(dest) ||
                        e.getTo().equals(dest)) {
                    toRemove.add(e);
                }
            }

            for (ResidualEdge e : toRemove) {
                entry.getValue().remove(e);
            }
        }
    }

    private boolean BFS(Vertex from, Vertex to) {
        for (Vertex vertex : this.listOfVertices) {
            vertex.setVisited(false);
            vertex.setParents(new ArrayList<>());
        }

        Queue<Vertex> queue = new ArrayDeque<>();
        ((ArrayDeque<Vertex>) queue).addLast(from);
        from.setVisited(true);

        while (queue.size() != 0) {
            Vertex curr = ((ArrayDeque<Vertex>) queue).removeFirst();
            for (Vertex v : this.listOfVertices) {
                // Creates fake flow for not connected vertices based on the BFS implementation
                double flow = residualGraph.get(curr).stream()
                        .filter(x -> x.getTo().equals(v) && x.getFlow() > 0)
                        .findAny().orElse(new ResidualEdge(curr, v, -1, BigDecimal.ZERO, false)).getFlow();

                if (v.isVisited() == false && flow > 0) {
                    ((ArrayDeque<Vertex>) queue).addLast(v);
                    v.addParent(curr);
                    v.setVisited(true);
                }
            }
        }

        return (to.isVisited() == true);
    }

    private void buildResidualGraph() {
        for (Map.Entry<Vertex, List<Edge>> entry : this.adjacencyList.entrySet()) {
            if (!residualGraph.containsKey(entry.getKey())) {
                residualGraph.put(entry.getKey(), new ArrayList<>());
            }
            for (Edge e : entry.getValue()) {
                ResidualEdge fromTo = new ResidualEdge(e.getFrom(), e.getTo(), e.getCapacity(), e.getPrice(), false);
                ResidualEdge toFrom = new ResidualEdge(e.getTo(), e.getFrom(), 0, e.getPrice().negate(), true);

                fromTo.setMirrorEdge(toFrom);
                toFrom.setMirrorEdge(fromTo);

                residualGraph.get(entry.getKey()).add(fromTo);
                if (!residualGraph.containsKey(e.getTo())) {
                    residualGraph.put(e.getTo(), new ArrayList<>());
                    residualGraph.get(e.getTo()).add(toFrom);
                } else {
                    residualGraph.get(e.getTo()).add(toFrom);
                }
            }
        }
    }

    public List<ResultEdge> getResult() {
        BigDecimal minCostFlow = BigDecimal.valueOf(0);
        List<ResultEdge> result = new ArrayList<>();
        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph.entrySet()) {
            for (ResidualEdge e : entry.getValue()) {
                if (e.isResult()) {
                    BigDecimal price = BigDecimal.valueOf(e.getFlow() * e.getPrice().negate().doubleValue()).setScale(2, RoundingMode.HALF_UP);
                    minCostFlow = minCostFlow.add(price);
                    result.add(new ResultEdge(e.getTo().getName(), e.getFrom().getName(), price, e.getFlow()));
                }
            }
        }

        this.minCostFlow = minCostFlow;
        return result;
    }

    public void printGraphMinCostFlow() {
        BigDecimal minCostFlow = BigDecimal.valueOf(0);
        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph.entrySet()) {
            for (ResidualEdge e : entry.getValue()) {
                if (e.isResult()) {
                    BigDecimal price = BigDecimal.valueOf(e.getFlow() * e.getPrice().negate().doubleValue()).setScale(2, RoundingMode.HALF_UP);
                    minCostFlow = minCostFlow.add(price);

                    System.out.println(e.getTo() + " -> " + e.getFrom() + " - Flow: " + e.getFlow() +
                            " / Price: " + price.doubleValue());
                }

            }
        }
        System.out.println("The min const flow is: " + minCostFlow);
    }
}
