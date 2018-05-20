using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BellmanFord
{
    class Program
    {
        const int V = 5; //Number of vertices in graph

        static bool BellmanFord(int[,] graph, int startNode)
        {

            double[] distance = Enumerable.Repeat(Double.PositiveInfinity, V).ToArray();
            double[] parent = new double[V];

            parent[startNode] = -1;
            distance[startNode] = 0;

            for (int i = 0; i < V - 1; i++)
            {
                for (int u = 0; u < V; u++)
                {
                    for (int v = 0; v < V; v++)
                    {
                        if (distance[v] > distance[u] + graph[u, v])
                        {
                            distance[v] = distance[u] + graph[u, v];
                            parent[v] = u;
                        }
                    }
                }
            }

            for (int u = 0; u < V; u++)
            {
                for (int v = 0; v < V; v++)
                {
                    if (distance[v] > distance[u] + graph[u, v])
                    {
                        return false;
                    }
                }
            }

            return true;
        }

        static void Main(string[] args)
        {
            int[,] graph = new int[,] {
                {0, 7, 0, 13, 0, 28, 0},
                {0, 0, 25, 4, 10, 0, 0},
                {0, 0, 0, 0, 0, 0, 5},
                {0, 0, 6, 0, 0, 5, 0},
                {0, 0, 0, 0, 0, 0, 16},
                {0, 0, 0, 0, 3, 0, 7},
                {0, 0, 0, 0, 0, 0, 0}
            };

            Console.WriteLine("The graph contains negative cycle: " + !BellmanFord(graph, 0));
        }
    }
}
