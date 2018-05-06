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
        private List<Vertex> parents;
        private List<Vertex> children;

        public Vertex(string name)
        {
            this.name = name;
            this.parents = new List<Vertex>();
            this.children = new List<Vertex>();
        }

        public string Name { get => name; set => name = value; }
        public List<Vertex> Children { get => children; set => children = value; }
        public List<Vertex> Parents { get => parents; set => parents = value; }
        public int Seq { get => seq; set => seq = value; }
        public static int NextSeq { get => nextSeq; set => nextSeq = value; }

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
            return this.children.Remove(parent);
        }

        public void addParent(Vertex parent)
        {
            this.children.Add(parent);
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
    }
}
