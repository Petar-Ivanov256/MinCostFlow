using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MinCostFlow
{
    public class Path
    {
        private List<Vertex> verticesPath;
        private List<Edge> edgesPath;
        //private Vertex start;
        //private Vertex end;


        public List<Edge> EdgesPath { get => edgesPath; set => edgesPath = value; }
        public List<Vertex> VerticesPath { get => verticesPath; set => verticesPath = value; }

        public Path()
        {
            this.verticesPath = new List<Vertex>();
            this.edgesPath = new List<Edge>();
        }

        public void addVertexToPath(Vertex vertex)
        {
            this.verticesPath.Add(vertex);
        }

        public void addEdgeToPath(Edge edge)
        {
            this.edgesPath.Add(edge);
        }

        public void resetPath()
        {
            this.verticesPath.Clear();
            this.edgesPath.Clear();
        }
    }
}
