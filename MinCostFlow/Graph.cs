using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MinCostFlow
{
    public class Graph
    {
        private int numVertices;
        private int numEdges;
        private List<Edge> listOfEdges;
        private Dictionary<Vertex, List<Edge>> adjacencyList;
        private double[,] adjacencyMatrix;

        public Graph()
        {
            this.numEdges = 0;
            this.numVertices = 0;
            this.listOfEdges = new List<Edge>();
            this.adjacencyList = new Dictionary<Vertex, List<Edge>>();
        }

        public int NumVertices { get => numVertices; set => numVertices = value; }
        public int NumEdges { get => numEdges; set => numEdges = value; }

        public void addEdge(Edge newEdge)
        {
            // TODO Validations:
            // 1. Check for parallel arcs
            this.listOfEdges.Add(newEdge);
            if (this.adjacencyList.ContainsKey(newEdge.From))
            {
                this.adjacencyList[newEdge.From].Add(newEdge);
            }
            else
            {
                this.adjacencyList.Add(newEdge.From, new List<Edge>());
                this.adjacencyList[newEdge.From].Add(newEdge);
            }
            numEdges++;
            numVertices += 2;
        }

        public bool removeEdge(Edge rmEdge)
        {
            // TODO check what happens if you want to remove an Edge which is not in Graph
            var statusEdges = this.listOfEdges.Remove(rmEdge);
            var statusVertices = this.adjacencyList.Remove(rmEdge.From);
            numEdges--;
            numVertices -= 2;

            return statusEdges && statusVertices;
        }
        
        public int maxFlow()
        {

            return 0;
        }

        private void BFS() {

        }
    }
}


//const int V = 6; //Number of vertices in graph

///* Returns true if there is a path from source 's' to sink
//  't' in residual graph. Also fills parent[] to store the
//  path */
//static bool bfs(int[,] rGraph, int s, int t, int[] parent)
//{
//    // Create a visited array and mark all vertices as not visited
//    bool[] visited = new bool[V];

//    for (int i = 0; i < V; ++i)
//    {
//        visited[i] = false;
//    }

//    // Create a queue, enqueue source vertex and mark source vertex as visited
//    Queue<int> queue = new Queue<int>();
//    queue.Enqueue(s);

//    visited[s] = true;
//    parent[s] = -1;

//    // Standard BFS Loop
//    while (queue.Count != 0)
//    {
//        int u = queue.Dequeue();

//        for (int v = 0; v < V; v++)
//        {
//            if (visited[v] == false && rGraph[u, v] > 0)
//            {
//                queue.Enqueue(v);
//                parent[v] = u;
//                visited[v] = true;
//            }
//        }
//    }

//    // If we reached sink in BFS starting from source, then
//    // return true, else false
//    return (visited[t] == true);
//}

//// Returns tne maximum flow from s to t in the given graph
//static int fordFulkerson(int[,] graph, int s, int t)
//{
//    int u, v;

//    int[,] rGraph = new int[V, V];

//    for (u = 0; u < V; u++)
//    {
//        for (v = 0; v < V; v++)
//        {
//            rGraph[u, v] = graph[u, v];
//        }
//    }

//    // This array is filled by BFS and to store path
//    int[] parent = new int[V];

//    int max_flow = 0;  // There is no flow initially

//    // Augment the flow while tere is path from source to sink
//    // Theorem 6.4 (Augmenting Path Theorem) on page 202(pdf) 184(book)
//    while (bfs(rGraph, s, t, parent))
//    {
//        // Find minimum residual capacity of the edhes
//        // along the path filled by BFS. Or we can say
//        // find the maximum flow through the path found.
//        int path_flow = int.MaxValue;
//        for (v = t; v != s; v = parent[v])
//        {
//            u = parent[v];
//            path_flow = Math.Min(path_flow, rGraph[u, v]);
//        }

//        // update residual capacities of the edges and
//        // reverse edges along the path
//        for (v = t; v != s; v = parent[v])
//        {
//            u = parent[v];
//            rGraph[u, v] -= path_flow;
//            rGraph[v, u] += path_flow;
//        }

//        max_flow += path_flow;
//    }

//    return max_flow;
//}