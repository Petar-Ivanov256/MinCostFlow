using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MinCostFlow
{
    public class Vertex : IEquatable<Vertex>
    {
        private static int nextSeq = 0;
        private string name;
        private int seq;
        private bool isVisited;
        private double distance;
        private List<Vertex> parents;
        private List<Vertex> children;
        private Vertex parent;
        private int pi;
        private double nodeBalance;
        private double nodeImbalance;

        public Vertex(string name)
        {
            this.name = name;
            this.parents = new List<Vertex>();
            this.children = new List<Vertex>();
            this.nodeBalance = 0;
        }

        public string Name { get => name; set => name = value; }
        public List<Vertex> Children { get => children; set => children = value; }
        public List<Vertex> Parents { get => parents; set => parents = value; }
        public int Seq { get => seq; set => seq = value; }
        public static int NextSeq { get => nextSeq; set => nextSeq = value; }
        public bool IsVisited { get => isVisited; set => isVisited = value; }
        public double Distance { get => distance; set => distance = value; }
        public int Pi { get => pi; set => pi = value; }
        public double NodeBalance { get => nodeBalance; set => nodeBalance = value; }
        public double NodeImbalance { get => nodeImbalance; set => nodeImbalance = value; }
        public Vertex Parent { get => parent; set => parent = value; }

        public bool removeChild(Vertex child)
        {
            return this.children.Remove(child);
        }

        public void addChild(Vertex child)
        {
            this.children.Add(child);
        }

        public bool removeParent(Vertex parent)
        {
            return this.parents.Remove(parent);
        }

        public void addParent(Vertex parent)
        {
            this.parents.Add(parent);
        }

        public override bool Equals(object obj)
        {
            return Equals(obj as Vertex);
        }

        public bool Equals(Vertex other)
        {
            return other != null &&
                   Name == other.Name;
        }

        public override int GetHashCode()
        {
            return 539060726 + EqualityComparer<string>.Default.GetHashCode(Name);
        }

        public override string ToString()
        {
            return this.name;
        }
    }
}
