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
                    if(e.To.Distance > e.From.Distance + e.Price)
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
