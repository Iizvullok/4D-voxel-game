package zeugs4D;

public class ScreenLine {
	public Vector4D v0;
	public Vector4D v1;
	double size = 1;
	
	public ScreenLine(Vector4D vector0, Vector4D vector1) {
		v0 = new Vector4D(vector0.v0 * size, vector0.v1 * size, vector0.v2 * size, 0);
		v1 = new Vector4D(vector1.v0 * size, vector1.v1 * size, vector1.v2 * size, 0);
	}
	
	public static ScreenLine createScreenLine(Vector4D vector0, Vector4D vector1) {
		return new ScreenLine(vector0, vector1);
	}
	
	public void print() {
		System.out.println("ScreenLine:");
		v0.print();
		v1.print();
	}
}