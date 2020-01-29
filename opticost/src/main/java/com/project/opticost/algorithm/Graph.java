package com.project.opticost.algorithm;

import com.project.opticost.utils.exceptions.NoFeasibleSolutionException;

import java.math.BigDecimal;
import java.util.*;

public class Graph {
    private int adjMatrixSize = 5;

    private List<Edge> listOfEdges;
    private List<Vertex> listOfVertices;
    private Map<Vertex, List<Edge>> adjacencyList;
    private double[][] adjacencyMatrix;
    // TODO implement it with secound instance of Graph
    private double[][] residualGraph;
    // TODO think is it better to be Map<?, List<Edge>> instead of List<List<Edge>> - not so valid
    // TODO add validation for parallel arcs
    private Map<Vertex, List<ResidualEdge>> residualGraph1;
    private double[][] priceGraph;
    private double epsilon;
    private double minCostFlow;

    public Graph() {

        this.listOfEdges = new ArrayList<>();
        this.listOfVertices = new ArrayList<>();
        this.adjacencyList = new HashMap<>();
        this.adjacencyMatrix = new double[adjMatrixSize][adjMatrixSize];
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
//        residualGraph = new double[adjMatrixSize][adjMatrixSize];
        residualGraph1 = new HashMap<>();
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);
        Vertex end = this.listOfVertices.stream().filter(x -> x.equals(to)).findAny().orElse(null);

        for (Map.Entry<Vertex, List<Edge>> entry : this.adjacencyList.entrySet()) {
            if(!residualGraph1.containsKey(entry.getKey())){
                residualGraph1.put(entry.getKey(), new ArrayList<>());
            }
            for (Edge e : entry.getValue()) {
                residualGraph1.get(entry.getKey()).add(new ResidualEdge(e.getFrom(), e.getTo(), e.getCapacity(), e.getPrice(), false));
                if(!residualGraph1.containsKey(e.getTo())){
                    residualGraph1.put(e.getTo(), new ArrayList<>());
                    residualGraph1.get(e.getTo()).add(new ResidualEdge(e.getTo(), e.getFrom(), 0,  e.getPrice().negate(), true));
                }else{
                    residualGraph1.get(e.getTo()).add(new ResidualEdge(e.getTo(), e.getFrom(), 0,  e.getPrice().negate(), true));
                }
            }
        }

//        for (int i = 0; i < adjMatrixSize; i++) {
//            for (int j = 0; j < adjMatrixSize; j++) {
//                this.residualGraph[i][j] = this.adjacencyMatrix[i][j];
//            }
//        }

        double maxFlow = 0d;
        while (BFS(start, end)) {
            double minFlow = Double.MAX_VALUE;
            // TODO if you use only parenst for BFS you can use only one Vertex
            Vertex path = end;
            while (path.getParents().size() != 0) {
                Vertex u = path.getParents().get(path.getParents().size() - 1);
                Vertex v = path;

//                minFlow = Math.min(minFlow, residualGraph[u.getSeq()][v.getSeq()]);

                minFlow = Math.min(minFlow, residualGraph1.get(u).stream()
                        .filter(x -> x.getTo().equals(v))
                        .findAny().orElseThrow(NoSuchElementException::new).getFlow());

                path = path.getParents().get(path.getParents().size() - 1);
            }

            path = end;
            while (path.getParents().size() != 0) {
                Vertex u = path.getParents().get(path.getParents().size() - 1);
                Vertex v = path;

                double flowUV = residualGraph1.get(u).stream()
                        .filter(x -> x.getTo().equals(v))
                        .findAny().orElseThrow(NoSuchElementException::new).getFlow();
                double flowVU = residualGraph1.get(v).stream()
                        .filter(x -> x.getTo().equals(u))
                        .findAny().orElseThrow(NoSuchElementException::new).getFlow();

                residualGraph1.get(u).stream()
                        .filter(x -> x.getTo().equals(v))
                        .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowUV - minFlow);
                residualGraph1.get(v).stream()
                        .filter(x -> x.getTo().equals(u))
                        .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowVU + minFlow);

//                residualGraph[u.getSeq()][v.getSeq()] -= minFlow;
//                residualGraph[v.getSeq()][u.getSeq()] += minFlow;

                path = path.getParents().get(path.getParents().size() - 1);
            }

            maxFlow += minFlow;
        }

        return maxFlow;
    }

    public Vertex findNegativeCycle(Vertex from) {
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);
        ;

        for (Vertex v : this.listOfVertices) {
            v.setDistance(Integer.MAX_VALUE);
            v.setParents(new ArrayList<>());
        }
        start.setDistance(0);

        for (int i = 0; i < this.listOfVertices.size() - 1; i++) {
            for (Edge e : this.listOfEdges) {
                // TOTO check if it is ok to make the bigdecimal a double here
                if (e.getTo().getDistance() > e.getFrom().getDistance() + e.getPrice().doubleValue()) {
                    e.getTo().setDistance(e.getFrom().getDistance() + e.getPrice().doubleValue());
                    e.getTo().getParents().add(e.getFrom());
                }
            }
        }

        for (Edge e : this.listOfEdges) {
            if (e.getTo().getDistance() > e.getFrom().getDistance() + e.getPrice().doubleValue()) {
                return e.getTo();
            }
        }

        return null;
    }

    public Vertex findNegativeCycleInResidualGraph(Vertex from) {
        Vertex start = this.listOfVertices.stream().filter(x -> x.equals(from)).findAny().orElse(null);
//        double[] parent = new double[adjMatrixSize];
//
//        parent[start.getSeq()] = -1;
        for (Vertex v : this.listOfVertices) {
            v.setDistance(Integer.MAX_VALUE);
            v.setParents(new ArrayList<>());
        }
        start.setDistance(0);

        for (int i = 0; i < this.listOfVertices.size() - 1; i++) {
            for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph1.entrySet()) {
                Vertex uVertex = this.listOfVertices.stream().filter(x -> x.equals(entry.getKey())).findAny().orElse(null);
                for(ResidualEdge e : entry.getValue()){
                    if(e.getFlow() > 0){
                        Vertex vVertex = this.listOfVertices.stream().filter(x -> x.equals(e.getTo())).findAny().orElse(null);
                        if (vVertex.getDistance() > uVertex.getDistance() + e.getPrice().doubleValue()) {
                            vVertex.setDistance(uVertex.getDistance() + e.getPrice().doubleValue());
                            vVertex.getParents().add(uVertex);
                            vVertex.setParent(uVertex);
//                            parent[vVertex.getSeq()] = uVertex.getSeq();
                        }
                    }
                }
            }

//            for (int u = 0; u < adjMatrixSize; u++) {
//                for (int v = 0; v < adjMatrixSize; v++) {
//                    if (this.residualGraph[u][v] > 0) {
//                        //TODO could lead to bugs because I am searching vertex from Seq
//                        int searchU = u;
//                        int searchV = v;
//                        Vertex uVertex = this.listOfVertices.stream().filter(x -> x.getSeq() == searchU).findAny().orElse(null);
//                        Vertex vVertex = this.listOfVertices.stream().filter(x -> x.getSeq() == searchV).findAny().orElse(null);
//                        if (vVertex.getDistance() > uVertex.getDistance() + this.priceGraph[u][v]) {
//                            vVertex.setDistance(uVertex.getDistance() + this.priceGraph[u][v]);
//                            vVertex.getParents().add(uVertex);
//                            vVertex.setParent(uVertex);
////                            parent[vVertex.getSeq()] = uVertex.getSeq();
//                        }
//                    }
//                }
//            }
        }

        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph1.entrySet()) {
            Vertex uVertex = this.listOfVertices.stream().filter(x -> x.equals(entry.getKey())).findAny().orElse(null);
            for(ResidualEdge e : entry.getValue()){
                if(e.getFlow() > 0){
                    Vertex vVertex = this.listOfVertices.stream().filter(x -> x.equals(e.getTo())).findAny().orElse(null);
                    if (vVertex.getDistance() > uVertex.getDistance() + e.getPrice().doubleValue()) {
                        vVertex.getParents().add(uVertex);
                        return vVertex;
                    }
                }
            }
        }

//        for (int u = 0; u < adjMatrixSize; u++) {
//            for (int v = 0; v < adjMatrixSize; v++) {
//                if (this.residualGraph[u][v] > 0) {
//                    int searchU = u;
//                    int searchV = v;
//                    Vertex uVertex = this.listOfVertices.stream().filter(x -> x.getSeq() == searchU).findAny().orElse(null);
//                    Vertex vVertex = this.listOfVertices.stream().filter(x -> x.getSeq() == searchV).findAny().orElse(null);
//                    if (vVertex.getDistance() > uVertex.getDistance() + this.priceGraph[u][v]) {
//                        vVertex.getParents().add(uVertex);
//                        return vVertex;
//                    }
//                }
//            }
//        }

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
            this.generatePriceGraph();
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

            List<Double> rFlows = new ArrayList<>();
            for (int i = 0; i < cycle.size(); i++) {
                if (i == cycle.size() - 1) {
                    double flow = residualGraph1.get(cycle.get(0)).stream()
                            .filter(x -> x.getTo().equals(cycle.get(cycle.size() - 1)))
                            .findAny().orElseThrow(NoSuchElementException::new).getFlow();
                    rFlows.add(flow);
//                    rFlows.add(this.residualGraph[cycle.get(0).getSeq()][cycle.get(cycle.size() - 1).getSeq()]);
                } else {
                    // For the lambda
                    int finalI = i;
                    double flow = residualGraph1.get(cycle.get(i + 1)).stream()
                            .filter(x -> x.getTo().equals(cycle.get(finalI)))
                            .findAny().orElseThrow(NoSuchElementException::new).getFlow();
                    rFlows.add(flow);
//                    rFlows.add(this.residualGraph[cycle.get(i + 1).getSeq()][cycle.get(i).getSeq()]);
                }
            }

            double minRFlow = rFlows.stream().mapToDouble(v -> v).min().orElseThrow(NoSuchElementException::new);

            for (int i = 0; i < cycle.size(); i++) {
                if (i == cycle.size() - 1) {
                    double flowMinus = residualGraph1.get(cycle.get(0)).stream()
                            .filter(x -> x.getTo().equals(cycle.get(cycle.size() - 1)))
                            .findAny().orElseThrow(NoSuchElementException::new).getFlow();
                    residualGraph1.get(cycle.get(0)).stream()
                            .filter(x -> x.getTo().equals(cycle.get(cycle.size() - 1)))
                            .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowMinus - minRFlow);

                    double flowPlus = residualGraph1.get(cycle.get(cycle.size() - 1)).stream()
                            .filter(x -> x.getTo().equals(cycle.get(0)))
                            .findAny().orElseThrow(NoSuchElementException::new).getFlow();
                    residualGraph1.get(cycle.get(cycle.size() - 1)).stream()
                            .filter(x -> x.getTo().equals(cycle.get(0)))
                            .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowPlus + minRFlow);


//                    this.residualGraph[cycle.get(0).getSeq()][cycle.get(cycle.size() - 1).getSeq()] -= minRFlow;
//                    this.residualGraph[cycle.get(cycle.size() - 1).getSeq()][cycle.get(0).getSeq()] += minRFlow;
                } else {
                    int finalI = i;
                    double flowMinus = residualGraph1.get(cycle.get(i + 1)).stream()
                            .filter(x -> x.getTo().equals(cycle.get(finalI)))
                            .findAny().orElseThrow(NoSuchElementException::new).getFlow();
                    residualGraph1.get(cycle.get(i + 1)).stream()
                            .filter(x -> x.getTo().equals(cycle.get(finalI)))
                            .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowMinus - minRFlow);

                    double flowPlus = residualGraph1.get(cycle.get(i)).stream()
                            .filter(x -> x.getTo().equals(cycle.get(finalI + 1)))
                            .findAny().orElseThrow(NoSuchElementException::new).getFlow();
                    residualGraph1.get(cycle.get(i)).stream()
                            .filter(x -> x.getTo().equals(cycle.get(finalI + 1)))
                            .findAny().orElseThrow(NoSuchElementException::new).setFlow(flowPlus + minRFlow);

//                    this.residualGraph[cycle.get(i + 1).getSeq()][cycle.get(i).getSeq()] -= minRFlow;
//                    this.residualGraph[cycle.get(i).getSeq()][cycle.get(i + 1).getSeq()] += minRFlow;
                }
            }

            vertexInLoop = this.findNegativeCycleInResidualGraph(start);
        }

        return 0;
    }

    public void generatePriceGraph() {
        //TODO Clean it also when you are deleteing vertices or edges
        priceGraph = new double[adjMatrixSize][adjMatrixSize];

        for (Edge e : this.listOfEdges) {
            //TODO check if you can use double Value for the bigdecimal
            priceGraph[e.getFrom().getSeq()][e.getTo().getSeq()] = e.getPrice().doubleValue();
            priceGraph[e.getTo().getSeq()][e.getFrom().getSeq()] = -e.getPrice().doubleValue();
        }
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
            for(ResidualEdge e: entry.getValue()){
                if(e.getFrom().equals(source) ||
                        e.getTo().equals(source) ||
                        e.getFrom().equals(dest) ||
                        e.getTo().equals(dest)){
                    toRemove.add(e);
                }
            }

            for(ResidualEdge e: toRemove){
                entry.getValue().remove(e);
            }
        }

//        }
//        for (int i = 0; i < adjMatrixSize; i++) {
//            // The the indexes are reversed because in the residual network there is no edge "s" -> start and end -> "t"
//            // because we add artificial edges with the desired capacity of the supply and demand.
//            // That is why these edges are with full capacity and we have only the reversed edges in the Residual network
//            this.residualGraph[i][source.getSeq()] = 0;
//            this.residualGraph[dest.getSeq()][i] = 0;
//        }
    }

    private boolean BFS(Vertex from, Vertex to) {
        int verticesCnt = this.listOfVertices.size();
        for (Vertex vertex : this.listOfVertices) {
            vertex.setVisited(false);
        }

        Queue<Vertex> queue = new ArrayDeque<>();
        ((ArrayDeque<Vertex>) queue).addLast(from);
        from.setVisited(true);

        while (queue.size() != 0) {
            Vertex curr = ((ArrayDeque<Vertex>) queue).removeFirst();

            for (Vertex v : this.listOfVertices) {
//                if (v.isVisited() == false && this.residualGraph[curr.getSeq()][v.getSeq()] > 0) {
                // Creates fake flow for not connected vertices based on the BFS implementation
                double flow =  residualGraph1.get(curr).stream()
                        .filter(x -> x.getTo().equals(v))
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

    private void updateAdjacencyMatrix() {
        for (Vertex v : this.adjacencyList.keySet()) {
            for (Edge e : this.adjacencyList.get(v)) {
                if (adjMatrixSize > e.getFrom().getSeq() && adjMatrixSize > e.getTo().getSeq()) {
                    adjacencyMatrix[e.getFrom().getSeq()][e.getTo().getSeq()] = e.getCapacity();
                } else {
                    this.adjMatrixSize = this.adjMatrixSize * 2;
                    double[][] tmp = new double[adjMatrixSize][adjMatrixSize];
                    copyMatrix(tmp, this.adjacencyMatrix);
                    this.adjacencyMatrix = new double[adjMatrixSize][adjMatrixSize];
                    copyMatrix(this.adjacencyMatrix, tmp);
                    updateAdjacencyMatrix();
//                    this.updatadjMatrixSize();
                }
            }
        }
    }

    public void printGraphMinCostFlow() {
        double minCostFlow = 0;
//        for (Edge e : this.listOfEdges) {
//            minCostFlow += this.residualGraph[e.getTo().getSeq()][e.getFrom().getSeq()] *
//                    this.priceGraph[e.getFrom().getSeq()][e.getTo().getSeq()];
//
//            System.out.println(e.getFrom() + " -> " + e.getTo() + " - Flow: " +
//                    this.residualGraph[e.getTo().getSeq()][e.getFrom().getSeq()] +
//                    " / Price: " + this.residualGraph[e.getTo().getSeq()][e.getFrom().getSeq()] *
//                    this.priceGraph[e.getFrom().getSeq()][e.getTo().getSeq()]);
//        }

        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph1.entrySet()) {
            for(ResidualEdge e : entry.getValue()){
                if(e.isResult()){
                    minCostFlow += e.getFlow() * e.getPrice().negate().doubleValue() ;

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
//        for (Edge e : this.listOfEdges) {
//            minCostFlow += this.residualGraph[e.getTo().getSeq()][e.getFrom().getSeq()] *
//                    this.priceGraph[e.getFrom().getSeq()][e.getTo().getSeq()];
//            BigDecimal price = BigDecimal.valueOf(this.residualGraph[e.getTo().getSeq()][e.getFrom().getSeq()] *
//                    this.priceGraph[e.getFrom().getSeq()][e.getTo().getSeq()]);
//            double flow = this.residualGraph[e.getTo().getSeq()][e.getFrom().getSeq()];
//
//            result.add(new ResultEdge(e.getFrom().getName(), e.getTo().getName(), price, flow));
//        }

        for (Map.Entry<Vertex, List<ResidualEdge>> entry : this.residualGraph1.entrySet()) {
            for(ResidualEdge e : entry.getValue()){
                if(e.isResult()){
                    minCostFlow += e.getFlow() * e.getPrice().negate().doubleValue() ;
                    BigDecimal price =  BigDecimal.valueOf(e.getFlow() * e.getPrice().negate().doubleValue());
                    double flow = e.getFlow();
                    result.add(new ResultEdge(e.getTo().getName(), e.getFrom().getName(), price, flow));
                }
            }
        }

        this.minCostFlow = minCostFlow;
        return result;
    }

    public void printGraphMaxFlow() {
        for (Edge e : this.listOfEdges) {
            System.out.println(e.getFrom() + " -> " + e.getTo() + " = " +
                    this.residualGraph[e.getTo().getSeq()][e.getFrom().getSeq()]);
        }
    }

    public void printVertexSeq() {
        for (Vertex v : this.listOfVertices) {
            System.out.println(v + " -> " + v.getSeq());
        }
    }

    private void copyMatrix(double[][] dest, double[][] source) {
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[i].length; j++) {
                dest[i][j] = source[i][j];
            }
        }
    }

    // TODO implement the method
    private void updatadjMatrixSize() {
        // Probably you need to rebuild Seq of vertex
//        throw new NotImplementedException();
        System.out.println("Not implemented");
    }

    //TODO: What happens if the goal destination has an outgoing arc

}
