using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MinCostFlow
{
    public class Graph
    {
        private const int adjMatrixSize = 10;

        private List<Edge> listOfEdges;
        private List<Vertex> listOfVertices;
        private Dictionary<Vertex, List<Edge>> adjacencyList;
        private double[,] adjacencyMatrix;
        // TODO implement it with secound instance of Graph
        private double[,] residualGraph;

        public Graph()
        {

            this.listOfEdges = new List<Edge>();
            this.listOfVertices = new List<Vertex>();
            this.adjacencyList = new Dictionary<Vertex, List<Edge>>();
            // TODO make method to take care of the size of the matrix
            this.adjacencyMatrix = new double[adjMatrixSize, adjMatrixSize];
        }

        public void addEdge(Edge newEdge)
        {
            // TODO Validations:
            // 1. Check for parallel arcs
            this.updateEdges(newEdge);
            this.updateVertices(newEdge);
            if (this.adjacencyList.ContainsKey(newEdge.From))
            {
                this.adjacencyList[newEdge.From].Add(newEdge);
            }
            else
            {
                this.adjacencyList.Add(newEdge.From, new List<Edge>());
                this.adjacencyList[newEdge.From].Add(newEdge);
            }

            this.updateAdjacencyMatrix();
        }

        private void updateEdges(Edge edge)
        {
            if (!listOfEdges.Contains(edge))
            {
                listOfEdges.Add(edge);
            }
        }

        private void updateVertices(Edge edge)
        {
            if (!listOfVertices.Contains(edge.From))
            {
                listOfVertices.Add(edge.From);
                edge.From.Seq = Vertex.NextSeq;
                Vertex.NextSeq++;
            }
            else
            {
                edge.From.Seq = this.listOfVertices.Find(x => x.Equals(edge.From)).Seq;
            }

            if (!listOfVertices.Contains(edge.To))
            {
                listOfVertices.Add(edge.To);
                edge.To.Seq = Vertex.NextSeq;
                Vertex.NextSeq++;
            }
            else
            {
                edge.To.Seq = this.listOfVertices.Find(x => x.Equals(edge.To)).Seq;
            }
        }

        public bool removeEdge(Edge rmEdge)
        {
            // TODO check what happens if you want to remove an Edge which is not in Graph
            var statusEdges = this.listOfEdges.Remove(rmEdge);
            var statusVertices = this.adjacencyList.Remove(rmEdge.From);

            return statusEdges && statusVertices;
        }
        
        public double maxFlow(Vertex from, Vertex to)
        {
            residualGraph = new double[adjMatrixSize, adjMatrixSize];
            Vertex start = this.listOfVertices.Find(x => x.Equals(from));
            Vertex end = this.listOfVertices.Find(x => x.Equals(to));

            // TODO make the size more reasonable 
            for (int i = 0; i < adjMatrixSize; i++)
            {
                for (int j = 0; j < adjMatrixSize; j++)
                {
                    this.residualGraph[i,j] = this.adjacencyMatrix[i, j];
                }
            }
            
            var maxFlow = 0d;
            while(BFS(start, end))
            {
                double minFlow = double.MaxValue;
                // TODO if you use only parenst for BFS you can use only one Vertex
                var path = end;
                while (path.Parents.Count != 0)
                {
                    var u = path.Parents[path.Parents.Count - 1];
                    var v = path;
                    minFlow = Math.Min(minFlow, residualGraph[u.Seq, v.Seq]);

                    path = path.Parents[path.Parents.Count - 1];
                }

                path = end;
                while (path.Parents.Count != 0)
                {
                    var u = path.Parents[path.Parents.Count - 1];
                    var v = path;
                    residualGraph[u.Seq, v.Seq] -= minFlow;
                    residualGraph[v.Seq, u.Seq] += minFlow;

                    path = path.Parents[path.Parents.Count - 1];
                }

                maxFlow += minFlow;
            }

            return maxFlow;
        }

        private bool BFS(Vertex from, Vertex to) {
            var verticesCnt = this.listOfVertices.Count;
            foreach (Vertex vertex in this.listOfVertices)
            {
                vertex.IsVisited = false;
            }
            
            Queue<Vertex> queue = new Queue<Vertex>();
            queue.Enqueue(from);
            from.IsVisited = true;
            
            while (queue.Count != 0)
            {
                Vertex curr = queue.Dequeue();

                foreach (Vertex v in this.listOfVertices)
                {
                    if (v.IsVisited == false && this.residualGraph[curr.Seq, v.Seq] > 0)
                    {
                        queue.Enqueue(v);
                        v.addParent(curr);
                        v.IsVisited = true;
                    }
                }
            }

            return (to.IsVisited == true);
        }

        private void updateAdjacencyMatrix()
        {
            foreach (Vertex v in this.adjacencyList.Keys)
            {
                foreach(Edge e in this.adjacencyList[v])
                {
                    if (adjMatrixSize > e.From.Seq && adjMatrixSize > e.To.Seq)
                    {
                        adjacencyMatrix[e.From.Seq, e.To.Seq] = e.Capacity;
                    }
                    else
                    {
                        this.updatadjMatrixSize();
                    }
                }
            }
        }

        // TODO
        private void updatadjMatrixSize()
        {
            throw new NotImplementedException();
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