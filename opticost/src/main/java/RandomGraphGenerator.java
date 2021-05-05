import com.project.opticost.algorithm.Edge;
import com.project.opticost.algorithm.Graph;
import com.project.opticost.algorithm.Vertex;

import java.math.BigDecimal;
import java.util.*;

public class RandomGraphGenerator {
    private HashMap<Integer, Vertex> vertices = new HashMap<>();
    private HashSet<Edge> edges = new HashSet<>();
    private List<String> fileEdges = new ArrayList<>();
    private String NAME_PREFIX = "";

    public Graph generateGraph(int numberOfVertices, double inclusionProbability) {
        Graph g = new Graph();
        for (int i = 0; i <= numberOfVertices; i++) {
            Vertex from = getVertex(i);
            for (int j = 0; j <= numberOfVertices; j++) {
                Vertex to = getVertex(j);
                Integer capacity = new Random().nextInt(50);
                BigDecimal price = BigDecimal.valueOf(new Random().nextInt(50));
                Edge edge = new Edge(from, to, capacity, price);

                double probability = new Random().nextDouble();

                if (probability < inclusionProbability && i != j && !isParallelEdge(edge)) {
                    g.addEdge(edge);
                    fileEdges.add(NAME_PREFIX + from.getName() + "," + NAME_PREFIX + to.getName() + "," + capacity + "," + price);
                }
            }

        }
        return g;
    }

    public List<String> getFileEdges() {
        return fileEdges;
    }

    private Vertex getVertex(int vertex) {
        if (vertices.containsKey(vertex)) {
            return vertices.get(vertex);
        } else {
            vertices.put(vertex, new Vertex(String.valueOf(vertex)));
            return vertices.get(vertex);
        }
    }

    private Boolean isParallelEdge(Edge edge) {
        if (edges.contains(edge)) {
            return true;
        } else {
            edges.add(edge);
            return false;
        }
    }
}
