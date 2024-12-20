package zeugs4D;

public class Face {
	Edge edge1;
	Edge edge2;
	Edge edge3;
	Edge edge4;
	
	public Face(Edge e1, Edge e2, Edge e3, Edge e4) {
		edge1 = e1;
		edge2 = e2;
		edge3 = e3;
		edge4 = e4;
	}
	
	public void print() {
		System.out.println("Edge1: ");
		edge1.print();
		System.out.println("Edge2: ");
		edge2.print();
		System.out.println("Edge3: ");
		edge3.print();
		System.out.println("Edge4: ");
		edge4.print();
	}
}