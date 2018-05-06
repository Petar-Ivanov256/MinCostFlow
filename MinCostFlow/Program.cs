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

            var fEd = new Edge(new Vertex("A"), new Vertex("B"), 10, 1);
            
            graph.addEdge(fEd);
            graph.addEdge(new Edge(new Vertex("B"), new Vertex("C"), 20, 2));
            graph.addEdge(new Edge(new Vertex("B"), new Vertex("D"), 30, 3));

            //graph.removeEdge(new Edge(new Vertex("A"), new Vertex("B"), 10, 1));

            Console.WriteLine("Debug");

        }
    }
}
