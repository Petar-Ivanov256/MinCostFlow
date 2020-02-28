package com.project.opticost.algorithm;

import com.project.opticost.utils.exceptions.CorruptedDataException;
import com.project.opticost.utils.exceptions.NoFeasibleSolutionException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private List<Edge> listOfEdges;
    private List<Vertex> listOfVertices;
    private Map<Vertex, List<Edge>> adjacencyList;
    //TODO: What happens if the goal destination has an outgoing arc
    private Map<Vertex, List<ResidualEdge>> residualGraph1;
    private double epsilon;
    private double minCostFlow;

    public Graph() {

        this.listOfEdges = new ArrayList<>();
        this.listOfVertices = new ArrayList<>();
        this.adjacencyList = new HashMap<>();
    }

    public double getMinCostFlow() {
        return minCostFlow;
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
        // TODO check what happens if you want to remove an Edge which is not in Graph
        boolean statusEdges = this.listOfEdges.remove(rmEdge);
        boolean statusVertices = this.adjacencyList.get(rmEdge.getFrom()).remove(rmEdge);

        return statusEdges && statusVertices;
    }

    public double maxFlow(Vertex from, Vertex to) {
        residualGraph1 = new HashMap<>();
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);
        Vertex end = this.listOfVertices.stream().filter(x -> x.equals(to)).findAny().orElse(null);

        for (Map.Entry<Vertex, List<Edge>> entry : this.adjacencyList.entrySet()) {
            if (!residualGraph1.containsKey(entry.getKey())) {
                residualGraph1.put(entry.getKey(), new ArrayList<>());
            }
            for (Edge e : entry.getValue()) {
                ResidualEdge fromTo = new ResidualEdge(e.getFrom(), e.getTo(), e.getCapacity(), e.getPrice(), false);
                ResidualEdge toFrom = new ResidualEdge(e.getTo(), e.getFrom(), 0, e.getPrice().negate(), true);

                fromTo.setMirrorEdge(toFrom);
                toFrom.setMirrorEdge(fromTo);

                residualGraph1.get(entry.getKey()).add(fromTo);
                if (!residualGraph1.containsKey(e.getTo())) {
                    residualGraph1.put(e.getTo(), new ArrayList<>());
                    residualGraph1.get(e.getTo()).add(toFrom);
                } else {
                    residualGraph1.get(e.getTo()).add(toFrom);
                }
            }
        }

        double maxFlow = 0d;
        while (BFS(start, end)) {
            double minFlow = Double.MAX_VALUE;
            // TODO if you use only parenst for BFS you can use only one Vertex
            Vertex path = end;
            while (path.getParents().size() != 0) {
                Vertex u = path.getParents().get(path.getParents().size() - 1);
                Vertex v = path;

                minFlow = Math.min(minFlow, residualGraph1.get(u).stream()
                        .filter(x -> x.getTo().equals(v) && x.getFlow() > 0)
                        .findAny().orElseThrow(NoSuchElementException::new).getFlow());

                path = path.getParents().get(path.getParents().size() - 1);
            }

            path = end;
            while (path.getParents().size() != 0) {
                Vertex u = path.getParents().get(path.getParents().size() - 1);
                Vertex v = path;

                double flowUV = residualGraph1.get(u).stream()
                        .filter(x -> x.getTo().equals(v) && (x.isResult() == false))
                        .findAny().orElseThrow(NoSuchElementException::new).getFlow();
                double flowVU = residualGraph1.get(v).stream()
                        .filter(x -> x.getTo().equals(u) && (x.isResult() == true))
                        .findAny().orElseThrow(NoSuchElementException::new).getFlow();

                residualGraph1.get(u).stream()
                        .filter(x -> x.getTo().equals(v) && (x.isResult() == false))
                        .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowUV - minFlow);
                residualGraph1.get(v).stream()
                        .filter(x -> x.getTo().equals(u) && (x.isResult() == true))
                        .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowVU + minFlow);

                path = path.getParents().get(path.getParents().size() - 1);
            }

            maxFlow += minFlow;
        }

        return maxFlow;
    }

    //TODO clear all variables before run
    public Vertex findNegativeCycleInResidualGraph(Vertex from) {
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);
        for (Vertex v : this.listOfVertices) {
            v.setDistance(Integer.MAX_VALUE);
            v.setParents(new ArrayList<>());
        }
        start.setDistance(0);

        for (int i = 0; i < this.listOfVertices.size() - 1; i++) {
            for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph1.entrySet()) {
                Vertex uVertex = this.listOfVertices.stream().filter(x -> x.equals(entry.getKey())).findAny().orElse(null);
                for (ResidualEdge e : entry.getValue()) {
                    if (e.getFlow() > 0) {
                        Vertex vVertex = this.listOfVertices.stream().filter(x -> x.equals(e.getTo())).findAny().orElse(null);
                        if (vVertex.getDistance() > uVertex.getDistance() + e.getPrice().doubleValue()) {
                            vVertex.setDistance(uVertex.getDistance() + e.getPrice().doubleValue());
                            vVertex.getParents().add(uVertex);
                            vVertex.setParent(uVertex);
                        }
                    }
                }
            }
        }

        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph1.entrySet()) {
            Vertex uVertex = this.listOfVertices.stream().filter(x -> x.equals(entry.getKey())).findAny().orElse(null);
            for (ResidualEdge e : entry.getValue()) {
                if (e.getFlow() > 0) {
                    Vertex vVertex = this.listOfVertices.stream().filter(x -> x.equals(e.getTo())).findAny().orElse(null);
                    if (vVertex.getDistance() > uVertex.getDistance() + e.getPrice().doubleValue()) {
                        vVertex.getParents().add(uVertex);
                        return vVertex;
                    }
                }
            }
        }
        return null;
    }

    public int minCostFlowCycleCancel(Vertex from, Vertex to, int cargo) throws Exception {
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);
        Vertex end = this.listOfVertices.stream().filter(x -> x.equals(to)).findAny().orElse(null);

        Vertex source = new Vertex("s");
        Vertex dest = new Vertex("t");

        this.addEdge(new Edge(source, start, cargo, BigDecimal.valueOf(0)));
        this.addEdge(new Edge(end, dest, cargo, BigDecimal.valueOf(0)));
        //TODO make the flow to be int
        int maxFlow = (int) this.maxFlow(source, dest);

        if (maxFlow < cargo) {
            throw new NoFeasibleSolutionException("There is no feasible solution for the supply: " + cargo);
        } else {
            this.establishFeasibleFLow(source, dest);
        }

        Vertex vertexInLoop = this.findNegativeCycleInResidualGraph(start);
        while (vertexInLoop != null) {
            Vertex parent = vertexInLoop.getParents().get(vertexInLoop.getParents().size() - 1);
            List<Vertex> cycle = new ArrayList<>();
            //cycle.Add(vertexInLoop);
            while (!cycle.contains(parent)/*parent != vertexInLoop*/) {
                cycle.add(parent);
                parent = parent.getParents().get(parent.getParents().size() - 1);
            }

            List<ResidualEdge> singleEdges = new ArrayList<>();
            for (int i = 0; i < cycle.size(); i++) {
                Vertex vFrom = null;
                Vertex vTo = null;
                if (i == cycle.size() - 1) {
                    vFrom = cycle.get(0);
                    vTo = cycle.get(cycle.size() - 1);
                } else {
                    vFrom = cycle.get(i + 1);
                    vTo = cycle.get(i);
                }

                Vertex finalVTo = vTo;
                List<ResidualEdge> rList = residualGraph1.get(vFrom).stream()
                        .filter(x -> x.getTo().equals(finalVTo)).collect(Collectors.toList());

                if (rList.size() == 1) {
                    singleEdges.add(rList.get(0));
                } else if (rList.size() > 1){
                    ResidualEdge minNegative = rList.stream()
                            .min(Comparator.comparing(x -> x.getPrice().multiply(BigDecimal.valueOf(x.getFlow()))))
                            .get();
                    singleEdges.add(minNegative);
                } else {
                    throw new CorruptedDataException("No connection in the negative cycle");
                }
            }

//            double minRFlow = rFlows.stream().mapToDouble(v -> v).min().orElseThrow(NoSuchElementException::new);

            double minRFlow = singleEdges.stream().min(Comparator.comparing(ResidualEdge::getFlow)).get().getFlow();
            // TODO set the reverse flow
            // TODO add reference for a Mirror edge
            for (ResidualEdge singleEdge : singleEdges) {
                double flowMinus = singleEdge.getFlow();
                singleEdge.setFlow(flowMinus - minRFlow);

                double flowPlus = singleEdge.getMirrorEdge().getFlow();
                singleEdge.getMirrorEdge().setFlow(flowPlus + minRFlow);
            }
//            for (int i = 0; i < cycle.size(); i++) {
//                if (i == cycle.size() - 1) {
//                    double flowMinus = residualGraph1.get(cycle.get(0)).stream()
//                            .filter(x -> x.getTo().equals(cycle.get(cycle.size() - 1)))
//                            .findAny().orElseThrow(NoSuchElementException::new).getFlow();
//                    residualGraph1.get(cycle.get(0)).stream()
//                            .filter(x -> x.getTo().equals(cycle.get(cycle.size() - 1)))
//                            .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowMinus - minRFlow);
//
//                    double flowPlus = residualGraph1.get(cycle.get(cycle.size() - 1)).stream()
//                            .filter(x -> x.getTo().equals(cycle.get(0)))
//                            .findAny().orElseThrow(NoSuchElementException::new).getFlow();
//                    residualGraph1.get(cycle.get(cycle.size() - 1)).stream()
//                            .filter(x -> x.getTo().equals(cycle.get(0)))
//                            .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowPlus + minRFlow);
//                } else {
//                    int finalI = i;
//                    double flowMinus = residualGraph1.get(cycle.get(i + 1)).stream()
//                            .filter(x -> x.getTo().equals(cycle.get(finalI)))
//                            .findAny().orElseThrow(NoSuchElementException::new).getFlow();
//                    residualGraph1.get(cycle.get(i + 1)).stream()
//                            .filter(x -> x.getTo().equals(cycle.get(finalI)))
//                            .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowMinus - minRFlow);
//
//                    double flowPlus = residualGraph1.get(cycle.get(i)).stream()
//                            .filter(x -> x.getTo().equals(cycle.get(finalI + 1)))
//                            .findAny().orElseThrow(NoSuchElementException::new).getFlow();
//                    residualGraph1.get(cycle.get(i)).stream()
//                            .filter(x -> x.getTo().equals(cycle.get(finalI + 1)))
//                            .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowPlus + minRFlow);
//                }
//            }

            vertexInLoop = this.findNegativeCycleInResidualGraph(start);
        }

        return 0;
    }

    private void establishFeasibleFLowCostScaling(Vertex source, Vertex dest) {
        this.removeVertex(source);
        this.removeVertex(dest);

//        for (int i = 0; i < adjMatrixSize; i++) {
//            // The the indexes are reversed because in the residual network there is no edge "s" -> start and end -> "t"
//            // because we add artificial edges with the desired capacity of the supply and demand.
//            // That is why these edges are with full capacity and we have only the reversed edges in the Residual network
//            this.residualGraph[i][source.getSeq()] = 0;
//            this.residualGraph[dest.getSeq()][i] = 0;
//        }
    }

    private void establishFeasibleFLow(Vertex source, Vertex dest) {
        this.removeVertex(source);
        this.removeVertex(dest);

        this.residualGraph1.remove(source);
        this.residualGraph1.remove(dest);

        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph1.entrySet()) {
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
        }

        Queue<Vertex> queue = new ArrayDeque<>();
        ((ArrayDeque<Vertex>) queue).addLast(from);
        from.setVisited(true);

        while (queue.size() != 0) {
            Vertex curr = ((ArrayDeque<Vertex>) queue).removeFirst();

            for (Vertex v : this.listOfVertices) {
                // Creates fake flow for not connected vertices based on the BFS implementation=
                double flow = residualGraph1.get(curr).stream()
                        .filter(x -> x.getTo().equals(v) && x.getFlow() > 0)
                        .findAny().orElse(new ResidualEdge(curr, v, -1, BigDecimal.ZERO, false)).getFlow();

                if (v.isVisited() == false && flow > 0) {
                    ((ArrayDeque<Vertex>) queue).addLast(v);
                    // TODO multiple parents probably should be removed
                    v.addParent(curr);
                    v.setVisited(true);
                }
            }
        }

        return (to.isVisited() == true);
    }

    public void printGraphMinCostFlow() {
        double minCostFlow = 0;
        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph1.entrySet()) {
            for (ResidualEdge e : entry.getValue()) {
                if (e.isResult()) {
                    minCostFlow += e.getFlow() * e.getPrice().negate().doubleValue();

                    System.out.println(e.getTo() + " -> " + e.getFrom() + " - Flow: " + e.getFlow() +
                            " / Price: " + e.getFlow() * e.getPrice().negate().doubleValue());
                }
            }
        }
        System.out.println("The min const flow is: " + minCostFlow);
    }

    public List<ResultEdge> getResult() {
        double minCostFlow = 0;
        List<ResultEdge> result = new ArrayList<>();
        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph1.entrySet()) {
            for (ResidualEdge e : entry.getValue()) {
                if (e.isResult()) {
                    minCostFlow += e.getFlow() * e.getPrice().negate().doubleValue();
                    BigDecimal price = BigDecimal.valueOf(e.getFlow() * e.getPrice().negate().doubleValue());
                    double flow = e.getFlow();
                    result.add(new ResultEdge(e.getTo().getName(), e.getFrom().getName(), price, flow));
                }
            }
        }

        this.minCostFlow = minCostFlow;
        return result;
    }
}
