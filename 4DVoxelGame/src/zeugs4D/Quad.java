package zeugs4D;

import org.lwjgl.util.vector.Vector3f;

public class Quad {
	public Vector3f[] v;
	public Vector3f n;
	public int i;
	
	public Quad(Vector3f [] dings, Vector3f normal, int id) {
		v = dings;
		n = normal;
		i = id;
	}
}