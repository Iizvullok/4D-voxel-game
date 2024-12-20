package zeugs4D;

import org.lwjgl.util.vector.Vector3f;

public class Vector4D {
	public double v0;
	public double v1;
	public double v2;
	public double v3;
	
	public Vector4D(double d0, double d1, double d2, double d3) {
		v0 = d0;
		v1 = d1;
		v2 = d2;
		v3 = d3;
	}
	
	public Vector4D substract(Vector4D v) {
		return new Vector4D(v0 - v.v0, v1 - v.v1, v2 - v.v2, v3 - v.v3);
	}
	
	public Vector4D invert() {
		return new Vector4D(-v0, -v1, -v2, -v3);
	}
	
	public double distance(Vector4D v) {
		v = v.substract(this);
		return Math.sqrt(v.v0 * v.v0 + v.v1 * v.v1 + v.v2 * v.v2 + v.v3 * v.v3);
	}
	
	public Vector4D calculateRenderedPosition() {
		Vector4D temp = new Vector4D(v0 / v3, v1 / v3, v2, 0);
		return new Vector4D(temp.v0 / temp.v2, temp.v1 / temp.v2, 0, 0);
	}
	
	public void print() {
		System.out.println(v0 + " " + v1 + " " + v2 + " " + v3);
	}
	
	public boolean isExactlyTheSame(Vector4D v) {
		return v.v0 == v0 && v.v1 == v1 && v.v2 == v2 && v.v3 == v3;
	}
	
	public Vector3f toVector3f() {
		return new Vector3f((float)(v0), (float)(v1), (float)(v2));
	}
}