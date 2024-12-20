package zeugs4D;

import entities.Camera;

public class Edge {
	public Vector4D vertex1;
	public Vector4D vertex2;
	
	public Edge(Vector4D v1, Vector4D v2) {
		vertex1 = v1;
		vertex2 = v2;
	}
	
	public boolean doesLineExist() {
		if(vertex1.v3 < Camera.crossW && vertex2.v3 < Camera.crossW) {
			return false;
		}
		else if(vertex1.v3 > Camera.crossW && vertex2.v3 > Camera.crossW){
			return false;
		}
		else {
			return true;
		}
	}
	
	public Vector4D getCrosspoint() {
		Vector4D v1 = new Vector4D(vertex1.v0, vertex1.v1, vertex1.v2, vertex1.v3 - Camera.crossW);
		Vector4D v2 = new Vector4D(vertex2.v0, vertex2.v1, vertex2.v2, vertex2.v3 - Camera.crossW);
		Vector4D point = new Vector4D(v1.v0 - v1.v3 * ((v1.v0 - v2.v0) / (v1.v3 - v2.v3)), v1.v1 - v1.v3 * ((v1.v1 - v2.v1) / (v1.v3 - v2.v3)), v1.v2 - v1.v3 * ((v1.v2 - v2.v2) / (v1.v3 - v2.v3)), 0);
		return point;
	}
	
	public void print() {
		System.out.print("vertex1: ");
		vertex1.print();
		System.out.print("vertex2: ");
		vertex2.print();
	}
}