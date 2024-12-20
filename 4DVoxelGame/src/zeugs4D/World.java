package zeugs4D;

import org.lwjgl.util.vector.Vector2f;
import engineTester.MainGameLoop;
import entities.Camera;

public class World {
	public long seed = 687343457360L;
	public static ScreenLine [][] surface;
	public ChunkColumn [][][] map;
	
	public World(long seed) {
		this.seed = seed;
		map = new ChunkColumn[MainGameLoop.xzwSize][MainGameLoop.xzwSize][MainGameLoop.xzwSize];
		calculateCrosssectionFromSurface();
	}
	
	public void setBlock(int x, int y, int z, int w, int id, boolean requiresReRender) {
		int ChunkPositionX = x / 8;
		int ChunkPositionY = y / 8;
		int ChunkPositionZ = z / 8;
		int ChunkPositionW = w / 8;
		if(ChunkPositionX > MainGameLoop.xzwSize - 1 || ChunkPositionX < 0 || ChunkPositionY > MainGameLoop.height - 1 || ChunkPositionY < 0 || ChunkPositionZ > MainGameLoop.xzwSize - 1 || ChunkPositionZ < 0 || ChunkPositionW > MainGameLoop.xzwSize - 1 || ChunkPositionW < 0) {
			return;
		}
		else {
			
			if(map[ChunkPositionX][ChunkPositionZ][ChunkPositionW] == null) {
				return;
			}
			if(map[ChunkPositionX][ChunkPositionZ][ChunkPositionW].chunks[ChunkPositionY] == null) {
				return;
			}
			
			int positionInChunkX = x % 8;
			int positionInChunkY = y % 8;
			int positionInChunkZ = z % 8;
			int positionInChunkW = w % 8;
			if(positionInChunkX > 7 || positionInChunkX < 0 || positionInChunkY > 7 || positionInChunkY < 0 || positionInChunkZ > 7 || positionInChunkZ < 0 || positionInChunkW > 7 || positionInChunkW < 0) {
				return;
			}
			map[ChunkPositionX][ChunkPositionZ][ChunkPositionW].chunks[ChunkPositionY].blocks[positionInChunkX][positionInChunkY][positionInChunkZ][positionInChunkW] = id;
			MainGameLoop.updateCrosssection = true;
		}
		if(requiresReRender) {
			map[ChunkPositionX][ChunkPositionZ][ChunkPositionW].save = true;
			map[ChunkPositionX][ChunkPositionZ][ChunkPositionW].chunks[ChunkPositionY].changed = true;
			try {
				map[ChunkPositionX][ChunkPositionZ][ChunkPositionW].chunks[ChunkPositionY].enclosed(this);
				if(x % 8 == 0) {
					map[ChunkPositionX - 1][ChunkPositionZ][ChunkPositionW].chunks[ChunkPositionY].enclosed(this);
				}
				if(x % 8 == 7) {
					map[ChunkPositionX + 1][ChunkPositionZ][ChunkPositionW].chunks[ChunkPositionY].enclosed(this);
				}
				if(y % 8 == 0) {
					map[ChunkPositionX][ChunkPositionZ][ChunkPositionW].chunks[ChunkPositionY - 1].enclosed(this);
				}
				if(y % 8 == 7) {
					map[ChunkPositionX][ChunkPositionZ][ChunkPositionW].chunks[ChunkPositionY + 1].enclosed(this);
				}
				if(z % 8 == 0) {
					map[ChunkPositionX][ChunkPositionZ - 1][ChunkPositionW].chunks[ChunkPositionY].enclosed(this);
				}
				if(z % 8 == 7) {
					map[ChunkPositionX][ChunkPositionZ + 1][ChunkPositionW].chunks[ChunkPositionY].enclosed(this);
				}
				if(w % 8 == 0) {
					map[ChunkPositionX][ChunkPositionZ][ChunkPositionW - 1].chunks[ChunkPositionY].enclosed(this);
				}
				if(w % 8 == 7) {
					map[ChunkPositionX][ChunkPositionZ][ChunkPositionW + 1].chunks[ChunkPositionY].enclosed(this);
				}
			}
			catch(Exception e){
				
			}
		}
	}
	
	public int getBlock(int x, int y, int z, int w) {
		int ChunkPositionX = x / 8;
		int ChunkPositionY = y / 8;
		int ChunkPositionZ = z / 8;
		int ChunkPositionW = w / 8;
		if(ChunkPositionX > MainGameLoop.xzwSize - 1 || ChunkPositionX < 0 || ChunkPositionY > MainGameLoop.height - 1 || ChunkPositionY < 0 || ChunkPositionZ > MainGameLoop.xzwSize - 1 || ChunkPositionZ < 0 || ChunkPositionW > MainGameLoop.xzwSize - 1 || ChunkPositionW < 0) {
			return 0;
		}
		else {
			if(map[ChunkPositionX][ChunkPositionZ][ChunkPositionW] == null) {
				return 0;
			}
			if(map[ChunkPositionX][ChunkPositionZ][ChunkPositionW].chunks[ChunkPositionY] == null) {
				return 0;
			}
			int positionInChunkX = x % 8;
			int positionInChunkY = y % 8;
			int positionInChunkZ = z % 8;
			int positionInChunkW = w % 8;
			if(positionInChunkX > 7 || positionInChunkX < 0 || positionInChunkY > 7 || positionInChunkY < 0 || positionInChunkZ > 7 || positionInChunkZ < 0 || positionInChunkW > 7 || positionInChunkW < 0) {
				return 0;
			}
			return map[ChunkPositionX][ChunkPositionZ][ChunkPositionW].chunks[ChunkPositionY].blocks[positionInChunkX][positionInChunkY][positionInChunkZ][positionInChunkW];
		}
	}
	
	public static void calculateCrosssectionFromSurface() {
		double rotation = Camera.rotationXWPI;
		int size = MainGameLoop.xzwSize * 8;
		surface = new ScreenLine[size][size];
		Vector2f v1 = rotatePoint(new Vector2f(-0.5f, -0.5f), rotation);
		Vector2f v2 = rotatePoint(new Vector2f(-0.5f, 0.5f), rotation);
		Vector2f v3 = rotatePoint(new Vector2f(0.5f, 0.5f), rotation);
		Vector2f v4 = rotatePoint(new Vector2f(0.5f, -0.5f), rotation);
		
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				Vector2f position = rotatePoint(new Vector2f(i - (float)(Camera.posX), j - (float)(Camera.posW)), rotation);
				if(position.y > -0.708 && position.y < 0.708) {
					Edge edge1 = new Edge(new Vector4D(v1.x + position.x, 0, 0, v1.y + position.y), new Vector4D(v2.x + position.x, 0, 0, v2.y + position.y));
					Edge edge2 = new Edge(new Vector4D(v2.x + position.x, 0, 0, v2.y + position.y), new Vector4D(v3.x + position.x, 0, 0, v3.y + position.y));
					Edge edge3 = new Edge(new Vector4D(v3.x + position.x, 0, 0, v3.y + position.y), new Vector4D(v4.x + position.x, 0, 0, v4.y + position.y));
					Edge edge4 = new Edge(new Vector4D(v4.x + position.x, 0, 0, v4.y + position.y), new Vector4D(v1.x + position.x, 0, 0, v1.y + position.y));
					Face face = new Face(edge1, edge2, edge3, edge4);
					surface[i][j] = crosssection(face);
				}
			}
		}
	}
	
	public static Vector2f rotatePoint(Vector2f vector, double rotation) {
		return new Vector2f((float)(Math.cos(rotation) * vector.x - Math.sin(rotation) * vector.y), (float)(Math.sin(rotation) * vector.x + Math.cos(rotation) * vector.y));
	}
	
	public static ScreenLine crosssection(Face face) {
		boolean printInfo = false;
		
		if(printInfo) {
			System.out.println("Starting to calculate crossection!");
		}
		
		if(face.edge1.doesLineExist() || face.edge2.doesLineExist() || face.edge3.doesLineExist() || face.edge4.doesLineExist()) {
			if(printInfo) {
				System.out.println("Crosssection does exist!");
				System.out.println("Face: ");
				face.print();
			}
			boolean firstFaceFound = false;
			Vector4D vector1 = null;
			Vector4D vector2 = null;
			if(face.edge1.doesLineExist()) {
				if(printInfo) {
					System.out.println("face.edge1.getCrosspoint: ");
					face.edge1.getCrosspoint().print();
				}
				vector1 = face.edge1.getCrosspoint();
				firstFaceFound = true;
			}
			if(face.edge2.doesLineExist()) {
				if(printInfo) {
					System.out.println("face.edge2.getCrosspoint: ");
					face.edge2.getCrosspoint().print();
				}
				if(firstFaceFound) {
					vector2 = face.edge2.getCrosspoint();
				}
				else {
					vector1 = face.edge2.getCrosspoint();
				}
				firstFaceFound = true;
			}
			if(face.edge3.doesLineExist()) {
				if(printInfo) {
					System.out.println("face.edge3.getCrosspoint: ");
					face.edge3.getCrosspoint().print();
				}
				if(firstFaceFound) {
					vector2 = face.edge3.getCrosspoint();
				}
				else {
					vector1 = face.edge3.getCrosspoint();
				}
				firstFaceFound = true;
			}
			if(face.edge4.doesLineExist()) {
				if(printInfo) {
					System.out.println("face.edge4.getCrosspoint: ");
					face.edge4.getCrosspoint().print();
				}
				vector2 = face.edge4.getCrosspoint();
			}
			if(printInfo) {
				System.out.println("Vector1: ");
				vector1.print();
				System.out.println("Vector2: ");
				vector2.print();
				System.out.println("Dings");
				System.out.println("");
				System.out.println("");
				System.out.println("");
			}
			return ScreenLine.createScreenLine(vector1, vector2);
		}
		else {
			return null;
		}
	}
}