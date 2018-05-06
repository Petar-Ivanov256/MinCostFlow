using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MinCostFlow
{
    public class Edge : IEquatable<Edge>
    {
        private Vertex from;
        private Vertex to;
        private int capacity;
        private double price;

        public Edge(Vertex from, Vertex to, int capacity, double price)
        {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.price = price;
        }

        public int Capacity { get => capacity; set => capacity = value; }
        public double Price { get => price; set => price = value; }
        public Vertex From { get => from; set => from = value; }
        public Vertex To { get => to; set => to = value; }

        public override bool Equals(object obj)
        {
            return Equals(obj as Edge);
        }

        public bool Equals(Edge other)
        {
            return other != null &&
                   EqualityComparer<Vertex>.Default.Equals(From, other.From) &&
                   EqualityComparer<Vertex>.Default.Equals(To, other.To);
        }

        public override int GetHashCode()
        {
            var hashCode = -1781160927;
            hashCode = hashCode * -1521134295 + EqualityComparer<Vertex>.Default.GetHashCode(From);
            hashCode = hashCode * -1521134295 + EqualityComparer<Vertex>.Default.GetHashCode(To);
            return hashCode;
        }
    }
}
