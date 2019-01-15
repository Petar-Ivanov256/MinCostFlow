using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MinCostFlow
{
    class Program
    {
        static void Main(string[] args)
        {
            var graph = new Graph();

            var v0 = new Vertex("0");
            var v1 = new Vertex("1");
            var v2 = new Vertex("2");

            graph.addEdge(new Edge(v0, v1, 2, 7));
            graph.addEdge(new Edge(v0, v2, 10, 16));
            graph.addEdge(new Edge(v1, v2, 5, 4));


            graph.minCostFlowCostScaling(new Vertex("0"), new Vertex("2"), 4);
            //graph.minCostFlowCycleCancel(new Vertex("0"), new Vertex("2"), 4);
            graph.printGraphMinCostFlow();

            //var v0 = new Vertex("0");
            //var v1 = new Vertex("1");
            //var v2 = new Vertex("2");
            //var v3 = new Vertex("3");
            //var v4 = new Vertex("4");
            //var v5 = new Vertex("5");
            //var v6 = new Vertex("6");

            ////474
            //graph.addEdge(new Edge(v0, v1, 16, 7));
            //graph.addEdge(new Edge(v0, v3, 11, 13));
            //graph.addEdge(new Edge(v0, v5, 13, 28));
            //graph.addEdge(new Edge(v1, v2, 17, 25));
            //graph.addEdge(new Edge(v1, v3, 18, 4));
            //graph.addEdge(new Edge(v1, v4, 16, 10));
            //graph.addEdge(new Edge(v2, v6, 22, 5));
            //graph.addEdge(new Edge(v3, v2, 12, 6));
            //graph.addEdge(new Edge(v3, v5, 19, 5));
            //graph.addEdge(new Edge(v4, v6, 16, 12));
            //graph.addEdge(new Edge(v5, v4, 10, 3));
            //graph.addEdge(new Edge(v5, v6, 5, 7));
            //graph.printVertexSeq();

            ////graph.minCostFlowCycleCancel(new Vertex("0"), new Vertex("6"), 20);
            //graph.minCostFlowCostScaling(new Vertex("0"), new Vertex("6"), 20);
            //graph.printGraphMinCostFlow();


            //graph.addEdge(new Edge(new Vertex("1"), new Vertex("2"), 50, 3));
            //graph.addEdge(new Edge(new Vertex("1"), new Vertex("3"), 30, 6));
            //graph.addEdge(new Edge(new Vertex("1"), new Vertex("4"), 15, 8));
            //graph.addEdge(new Edge(new Vertex("2"), new Vertex("3"), 50, 2));
            //graph.addEdge(new Edge(new Vertex("2"), new Vertex("5"), 25, 2));
            //graph.addEdge(new Edge(new Vertex("3"), new Vertex("4"), 15, 2));
            //graph.addEdge(new Edge(new Vertex("3"), new Vertex("5"), 45, 1));
            //graph.addEdge(new Edge(new Vertex("3"), new Vertex("6"), 10, 3));
            //graph.addEdge(new Edge(new Vertex("3"), new Vertex("8"), 15, 8));
            //graph.addEdge(new Edge(new Vertex("4"), new Vertex("6"), 10, 1));
            //graph.addEdge(new Edge(new Vertex("4"), new Vertex("9"), 20, 3));
            //graph.addEdge(new Edge(new Vertex("5"), new Vertex("7"), 90, 9));
            //graph.addEdge(new Edge(new Vertex("5"), new Vertex("8"), 10, 8));
            //graph.addEdge(new Edge(new Vertex("6"), new Vertex("8"), 60, 5));
            //graph.addEdge(new Edge(new Vertex("7"), new Vertex("11"), 10, 2));
            //graph.addEdge(new Edge(new Vertex("7"), new Vertex("8"), 10, 1));
            //graph.addEdge(new Edge(new Vertex("8"), new Vertex("11"), 80, 4));
            //graph.addEdge(new Edge(new Vertex("8"), new Vertex("10"), 10, 1));
            //graph.addEdge(new Edge(new Vertex("9"), new Vertex("8"), 20, 2));
            //graph.addEdge(new Edge(new Vertex("9"), new Vertex("10"), 10, 3));
            //graph.addEdge(new Edge(new Vertex("10"), new Vertex("11"), 10, 3));

            ////Console.WriteLine(graph.maxFlow(new Vertex("1"), new Vertex("11")));
            ////graph.printGraphMaxFlow();

            ////graph.minCostFlowCycleCancel(new Vertex("1"), new Vertex("11"), 84);
            //graph.minCostFlowCostScaling(new Vertex("1"), new Vertex("11"), 84);
            //graph.printGraphMinCostFlow();

            Console.WriteLine("Debug");

            
        }
    }
}
