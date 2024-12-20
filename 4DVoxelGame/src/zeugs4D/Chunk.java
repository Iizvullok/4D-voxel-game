package zeugs4D;

import java.util.ArrayList;
import org.lwjgl.util.vector.Vector3f;

import engineTester.MainGameLoop;

public class Chunk {
	public int [][][][] blocks;
	public boolean [][][][] fullyEnclosed;
	public boolean [][][][][] enclosedFaces;
	public boolean changed = false;
	int posX;
	int posY;
	int posZ;
	int posW;
	
	public Chunk(int x, int y, int z, int w, World world) {
		posX = x;
		posY = y;
		posZ = z;
		posW = w;
		//blocks = generate4DRock(world.heightmap4D);
	}
	
	public int [][][][] generate(byte [][][] heightmap){
		int [][][][] chunk = new int [8][8][8][8];
		int size = MainGameLoop.xzwSize * 8 - 1;
		for(int i = posX * 8; i < 8 + posX * 8; i++) {
			for(int k = posZ * 8; k < 8 + posZ * 8; k++) {
				for(int l = posW * 8; l < 8 + posW * 8; l++) {
					for(int j = posY * 8; j < 8 + posY * 8; j++) {
						if(j > heightmap[i][k][l] + 16) {
							chunk[i % 8][j % 8][k % 8][l % 8] = 3;
						}
						if(j > heightmap[i][k][l] + 17) {
							chunk[i % 8][j % 8][k % 8][l % 8] = 2;
						}
						if(j > heightmap[i][k][l] + 18) {
							chunk[i % 8][j % 8][k % 8][l % 8] = 2;
						}
						if(j > heightmap[i][k][l] + 19) {
							chunk[i % 8][j % 8][k % 8][l % 8] = 1;
						}
						if(i == 0 || i == size || k == 0 || k == size || l == 0 || l == size || j == 63) {
							chunk[i % 8][j % 8][k % 8][l % 8] = 7;
						}
					}
				}
			}
		}
		return chunk;
	}
	
	public int [][][][] generate4DRock(byte [][][][] heightmap){
		int [][][][] chunk = new int [8][8][8][8];
		int size = MainGameLoop.xzwSize * 8 - 1;
		for(int i = posX * 8; i < 8 + posX * 8; i++) {
			for(int k = posZ * 8; k < 8 + posZ * 8; k++) {
				for(int l = posW * 8; l < 8 + posW * 8; l++) {
					for(int j = posY * 8; j < 8 + posY * 8; j++) {
						if(heightmap[i][l][k][j] < j * 1.3 + 32) {
							chunk[i % 8][j % 8][k % 8][l % 8] = 1;
						}
						if(i == 0 || i == size || k == 0 || k == size || l == 0 || l == size || j == 63) {
							chunk[i % 8][j % 8][k % 8][l % 8] = 7;
						}
					}
				}
			}
		}
		return chunk;
	}
	
	public void enclosed(World world){
		fullyEnclosed = new boolean[8][8][8][8];
		enclosedFaces = new boolean[8][8][8][8][5];
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				for(int k = 0; k < 8; k++) {
					for(int l = 0; l < 8; l++) {
						int x = posX * 8 + i;
						int y = posY * 8 + j;
						int z = posZ * 8 + k;
						int w = posW * 8 + l;
						int enclosedSides = 0;
						int xwSidesEnclosed = 0;
						if(world.getBlock(x - 1, y, z, w) != 0) {
							enclosedSides++;
							xwSidesEnclosed++;
						}
						if(world.getBlock(x + 1, y, z, w) != 0) {
							enclosedSides++;
							xwSidesEnclosed++;
						}
						if(world.getBlock(x, y - 1, z, w) != 0) {
							enclosedFaces[i][j][k][l][1] = true;
							enclosedSides++;
						}
						if(world.getBlock(x, y + 1, z, w) != 0) {
							enclosedFaces[i][j][k][l][0] = true;
							enclosedSides++;
						}
						if(world.getBlock(x, y, z - 1, w) != 0) {
							enclosedFaces[i][j][k][l][3] = true;
							enclosedSides++;
						}
						if(world.getBlock(x, y, z + 1, w) != 0) {
							enclosedFaces[i][j][k][l][2] = true;
							enclosedSides++;
						}
						if(world.getBlock(x, y, z, w - 1) != 0) {
							enclosedSides++;
							xwSidesEnclosed++;
						}
						if(world.getBlock(x, y, z, w + 1) != 0) {
							enclosedSides++;
							xwSidesEnclosed++;
						}
						if(xwSidesEnclosed == 4) {
							enclosedFaces[i][j][k][l][4] = true;
						}
						if(enclosedSides == 8) {
							fullyEnclosed[i][j][k][l] = true;
						}
					}
				}
			}
		}
	}
	
	public void fill(int id) {
		this.blocks = new int[8][8][8][8];
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				for(int k = 0; k < 8; k++) {
					for(int l = 0; l < 8; l++) {
						this.blocks[i][j][k][l] = id;
					}
				}
			}
		}
	}
	
	public Quad[] getCrossection() {
		ArrayList<Quad> quadsList = new ArrayList<Quad>();
		for(int i = posX * 8; i < 8 + posX * 8; i++) {
			for(int j = posW * 8; j < 8 + posW * 8; j++) {
				if(World.surface[i][j] != null) {
					for(int k = posZ * 8; k < 8 + posZ * 8; k++) {
						for(int l = posY * 8; l < 8 + posY  * 8; l++) {
							if(blocks[i % 8][l % 8][k % 8][j % 8] != 0 && !fullyEnclosed[i % 8][l % 8][k % 8][j % 8]) {
								int id = blocks[i % 8][l % 8][k % 8][j % 8];
								float x0 = (float) World.surface[i][j].v0.v0;
								float x1 = (float) World.surface[i][j].v1.v0;
									
								Vector3f [] q;
								
								//Y+
								if(!enclosedFaces[i % 8][l % 8][k % 8][j % 8][0]) {
									q = new Vector3f [4];
									q[0] = new Vector3f(x0, -l, k);
									q[1] = new Vector3f(x0, -l, k + 1);
									q[2] = new Vector3f(x1, -l, k);
									q[3] = new Vector3f(x1, -l, k + 1);
									quadsList.add(new Quad(q, new Vector3f(0, -1, 0), id));
								}
									
								//Y-
								if(!enclosedFaces[i % 8][l % 8][k % 8][j % 8][1]) {
									q = new Vector3f [4];
									q[0] = new Vector3f(x0, -l + 1, k);
									q[1] = new Vector3f(x0, -l + 1, k + 1);
									q[2] = new Vector3f(x1, -l + 1, k);
									q[3] = new Vector3f(x1, -l + 1, k + 1);
									quadsList.add(new Quad(q, new Vector3f(0, 1, 0), id));
								}
									
								//Z+
								if(!enclosedFaces[i % 8][l % 8][k % 8][j % 8][2]) {
									q = new Vector3f [4];
									q[0] = new Vector3f(x0, -l, k + 1);
									q[1] = new Vector3f(x0, -l + 1, k + 1);
									q[2] = new Vector3f(x1, -l, k + 1);
									q[3] = new Vector3f(x1, -l + 1, k + 1);
									quadsList.add(new Quad(q, new Vector3f(0, 0, 1), id));
								}
									
								//Z-
								if(!enclosedFaces[i % 8][l % 8][k % 8][j % 8][3]) {
									q = new Vector3f [4];
									q[0] = new Vector3f(x0, -l, k);
									q[1] = new Vector3f(x0, -l + 1, k);
									q[2] = new Vector3f(x1, -l, k);
									q[3] = new Vector3f(x1, -l + 1, k);
									quadsList.add(new Quad(q, new Vector3f(0, 0, -1), id));
								}
									
								//X+-
								if(!enclosedFaces[i % 8][l % 8][k % 8][j % 8][4]) {
									//X-
									q = new Vector3f [4];
									q[0] = new Vector3f(x0, -l, k + 1);
									q[1] = new Vector3f(x0, -l + 1, k + 1);
									q[2] = new Vector3f(x0, -l, k);
									q[3] = new Vector3f(x0, -l + 1, k);
									quadsList.add(new Quad(q, new Vector3f(-1, 0, 0), id));
										
									//X+
									q = new Vector3f [4];
									q[0] = new Vector3f(x1, -l, k + 1);
									q[1] = new Vector3f(x1, -l + 1, k + 1);
									q[2] = new Vector3f(x1, -l, k);
									q[3] = new Vector3f(x1, -l + 1, k);
									quadsList.add(new Quad(q, new Vector3f(1, 0, 0), id));
								}
							}
						}
					}
				}
			}
		}
		Quad[] quads = new Quad[0];
		quads = quadsList.toArray(quads);
		return quads;
	}
}