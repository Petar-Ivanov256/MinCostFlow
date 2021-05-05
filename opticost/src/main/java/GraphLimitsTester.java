import com.project.opticost.algorithm.Graph;
import com.project.opticost.algorithm.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class GraphLimitsTester {
    public static void main(String[] args) throws Exception {
        RandomGraphGenerator rgg = new RandomGraphGenerator();
        int n = 60;
        double prob = 0.05d;

        Graph g = rgg.generateGraph(n, prob);
        System.out.println("Finish generating the graph");
        System.out.println("Number of vertices: " + g.getListOfVertices().size());
        System.out.println("Number of edges: " + g.getListOfEdges().size());
        long startTime = System.nanoTime();
        g.minCostFlowCycleCancel(new Vertex("0"), new Vertex(String.valueOf(n)), 40);
        long stopTime = System.nanoTime();
        long timeElapsed = stopTime - startTime;
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);
        System.out.println("Execution time in seconds : " + ((timeElapsed / 1000000) / 1000) % 60);
        System.out.println("Execution time in minutes : " + ((timeElapsed / 1000000) / (1000 * 60)) % 60);
        System.out.println("Execution time in hours : " + ((timeElapsed / 1000000) / (1000 * 60 * 60)) % 24);

        g.getResult();
        System.out.println("Mincost flow is: " + g.getMinCostFlow());
        System.out.println("Debug");

        createCsv(rgg.getFileEdges());
    }

    public static void createCsv(List<String> data) {
        StringBuilder sb = new StringBuilder();
        for (String line : data) {
            sb.append(line);
            sb.append('\n');
        }

        try (PrintWriter writer = new PrintWriter(new File("plan.csv"))) {
            writer.write(sb.toString());

            System.out.println("done!");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
