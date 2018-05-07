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

            var fEd = new Edge(new Vertex("0"), new Vertex("1"), 5, 10);

            graph.addEdge(fEd);
            graph.addEdge(new Edge(new Vertex("1"), new Vertex("2"), 3, 20));
            //graph.addEdge(new Edge(new Vertex("0"), new Vertex("2"), 2, 30));
            graph.addEdge(new Edge(new Vertex("0"), new Vertex("3"), 4, 40));
            graph.addEdge(new Edge(new Vertex("3"), new Vertex("2"), 2, 30));

            //graph.removeEdge(new Edge(new Vertex("A"), new Vertex("B"), 10, 1));

            //Console.WriteLine(graph.findNegativeCycle(new Vertex("0")));
            Console.WriteLine(graph.minCostFlow(new Vertex("0"), new Vertex("2"), 1));
            Console.WriteLine("Debug");

            
        }
    }
}
