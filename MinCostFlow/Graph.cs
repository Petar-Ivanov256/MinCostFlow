using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MinCostFlow
{
    public class Graph
    {
        private const int adjMatrixSize = 6;

        private List<Edge> listOfEdges;
        private List<Vertex> listOfVertices;
        private Dictionary<Vertex, List<Edge>> adjacencyList;
        private double[,] adjacencyMatrix;
        // TODO implement it with secound instance of Graph
        private double[,] residualGraph;
        private double[,] priceGraph;

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
            newEdge = this.updateEdges(newEdge);
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

        private Edge updateEdges(Edge edge)
        {
            edge = this.updateVertices(edge);

            if (!listOfEdges.Contains(edge))
            {
                listOfEdges.Add(edge);
            }

            return edge;
        }

        private Edge updateVertices(Edge edge)
        {
            if (!listOfVertices.Contains(edge.From))
            {
                listOfVertices.Add(edge.From);
                edge.From.Seq = Vertex.NextSeq;
                Vertex.NextSeq++;
            }
            else
            {
                edge.From = this.listOfVertices.Find(x => x.Equals(edge.From));
            }

            if (!listOfVertices.Contains(edge.To))
            {
                listOfVertices.Add(edge.To);
                edge.To.Seq = Vertex.NextSeq;
                Vertex.NextSeq++;
            }
            else
            {
                edge.To = this.listOfVertices.Find(x => x.Equals(edge.To));
            }

            return edge;
        }

        public void removeVertex(Vertex rmVertex)
        {
            this.listOfVertices.Remove(rmVertex);
            var edgesToRemove = new List<Edge>();
            foreach (Edge e in this.listOfEdges)
            {
                if (e.From.Equals(rmVertex) || e.To.Equals(rmVertex))
                {
                    edgesToRemove.Add(e);
                }
            }
            foreach (Edge e in edgesToRemove)
            {
                this.removeEdge(e);
            }

            for (int i = 0; i < adjMatrixSize; i++)
            {
                this.adjacencyMatrix[rmVertex.Seq, i] = 0;
                this.adjacencyMatrix[i, rmVertex.Seq] = 0;
            }

            this.adjacencyList.Remove(rmVertex);
        }

        public bool removeEdge(Edge rmEdge)
        {
            // TODO check what happens if you want to remove an Edge which is not in Graph
            var statusEdges = this.listOfEdges.Remove(rmEdge);
            var statusVertices = this.adjacencyList[rmEdge.From].Remove(rmEdge);
            this.adjacencyMatrix[rmEdge.From.Seq, rmEdge.To.Seq] = 0;

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
                    this.residualGraph[i, j] = this.adjacencyMatrix[i, j];
                }
            }

            var maxFlow = 0d;
            while (BFS(start, end))
            {
                double minFlow = double.MaxValue;
                // TODO if you use only parenst for BFS you can use only one Vertex
                var path = end;
                while (path.Parents.Count != 0)
                {
                    Vertex u = path.Parents[path.Parents.Count - 1];
                    Vertex v = path;
                    minFlow = Math.Min(minFlow, residualGraph[u.Seq, v.Seq]);

                    path = path.Parents[path.Parents.Count - 1];
                }

                path = end;
                while (path.Parents.Count != 0)
                {
                    Vertex u = path.Parents[path.Parents.Count - 1];
                    Vertex v = path;

                    residualGraph[u.Seq, v.Seq] -= minFlow;
                    residualGraph[v.Seq, u.Seq] += minFlow;

                    path = path.Parents[path.Parents.Count - 1];
                }

                maxFlow += minFlow;
            }

            return maxFlow;
        }

        public Vertex findNegativeCycle(Vertex from)
        {
            Vertex start = this.listOfVertices.Find(x => x.Equals(from));

            foreach (Vertex v in this.listOfVertices)
            {
                v.Distance = int.MaxValue;
                v.Parents = new List<Vertex>();
            }
            start.Distance = 0;

            for (int i = 0; i < this.listOfVertices.Count - 1; i++)
            {
                foreach (Edge e in this.listOfEdges)
                {
                    if (e.To.Distance > e.From.Distance + e.Price)
                    {
                        e.To.Distance = e.From.Distance + e.Price;
                        e.To.Parents.Add(e.From);
                    }
                }
            }

            foreach (Edge e in this.listOfEdges)
            {
                if (e.To.Distance > e.From.Distance + e.Price)
                {
                    return e.To;
                }
            }

            return null;
        }

        public Vertex findNegativeCycleInResidualGraph(Vertex from)
        {
            Vertex start = this.listOfVertices.Find(x => x.Equals(from));

            foreach (Vertex v in this.listOfVertices)
            {
                v.Distance = int.MaxValue;
                v.Parents = new List<Vertex>();
            }
            start.Distance = 0;

            for (int i = 0; i < this.listOfVertices.Count - 1; i++)
            {
                for (int u = 0; u < adjMatrixSize; u++)
                {
                    for (int v = 0; v < adjMatrixSize; v++)
                    {
                        if (this.residualGraph[u, v] > 0)
                        {
                            //TODO could lead to bugs because I am searching vertex from Seq
                            Vertex uVertex = this.listOfVertices.Find(x => x.Seq == u);
                            Vertex vVertex = this.listOfVertices.Find(x => x.Seq == v);
                            if (vVertex.Distance > uVertex.Distance + this.priceGraph[u, v])
                            {
                                vVertex.Distance = uVertex.Distance + this.priceGraph[u, v];
                                vVertex.Parents.Add(uVertex);
                            }
                        }
                    }
                }
            }

            for (int u = 0; u < adjMatrixSize; u++)
            {
                for (int v = 0; v < adjMatrixSize; v++)
                {
                    if (this.residualGraph[u, v] > 0)
                    {
                        //TODO could lead to bugs because I am searching vertex from Seq
                        Vertex uVertex = this.listOfVertices.Find(x => x.Seq == u);
                        Vertex vVertex = this.listOfVertices.Find(x => x.Seq == v);
                        if (vVertex.Distance > uVertex.Distance + this.priceGraph[u, v])
                        {
                            return vVertex;
                        }
                    }
                }
            }

            return null;
        }

        public int minCostFlow(Vertex from, Vertex to, int cargo)
        {
            Vertex start = this.listOfVertices.Find(x => x.Equals(from));
            Vertex end = this.listOfVertices.Find(x => x.Equals(to));
            Vertex source = new Vertex("s");
            Vertex dest = new Vertex("t");

            this.addEdge(new Edge(source, start, cargo, 0));
            this.addEdge(new Edge(end, dest, cargo, 0));
            //TODO make the flow to be int
            int maxFlow = (int)this.maxFlow(source, dest);

            if (maxFlow < cargo)
            {
                //TODO make custom exception
                throw new ApplicationException("There is no feasible solution for this sypply: " + cargo);
            }
            else
            {
                this.establishFeasibleFLow(source, dest);
                this.generatePriceGraph();
            }

            Vertex vertexInLoop = this.findNegativeCycleInResidualGraph(start);
            while (vertexInLoop != null)
            {


                vertexInLoop = this.findNegativeCycle(start);
            }

            return 0;
        }

        public void generatePriceGraph()
        {
            //TODO Clean it also when you are deleteing vertices or edges
            priceGraph = new double[adjMatrixSize, adjMatrixSize];

            foreach (Edge e in this.listOfEdges)
            {
                priceGraph[e.From.Seq, e.To.Seq] = e.Price;
                priceGraph[e.To.Seq, e.From.Seq] = -e.Price;
            }
        }

        private void establishFeasibleFLow(Vertex source, Vertex dest)
        {
            this.removeVertex(source);
            this.removeVertex(dest);

            for (int i = 0; i < adjMatrixSize; i++)
            {
                // The the indexes are reversed because in the residual network there is no edge "s" -> start and end -> "t"
                // because we add artificial edges with the desired capacity of the supply and demand.
                // That is why these edges are with full capacity and we have only the reversed edges in the Residual network
                this.residualGraph[i, source.Seq] = 0;
                this.residualGraph[dest.Seq, i] = 0;
            }
        }

        private bool BFS(Vertex from, Vertex to)
        {
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
                foreach (Edge e in this.adjacencyList[v])
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

        // TODO implement the method
        private void updatadjMatrixSize()
        {
            // Probably you need to rebuild Seq of vertex
            throw new NotImplementedException();
        }

        //TODO: What happens if the goal destination has an outgoing arc
    }
}
