package zeugs4D;

import java.util.Random;

import engineTester.MainGameLoop;
import generators.Tree;
import renderEngine.WorldFile;

public class ChunkColumn {
	public boolean save;
	public World world;
	public Chunk [] chunks;
	public int posX;
	public int posZ;
	public int posW;
	
	public ChunkColumn(int x, int z, int w, World wo) {
		this.save = false;
		chunks = new Chunk[MainGameLoop.height];
		posX = x;
		posZ = z;
		posW = w;
		this.world = wo;
	}
	
	public void generate() {
		try {
			//System.out.println("Retry!");
			ChunkColumn column = WorldFile.loadChunkColumn(posX, posZ, posW, world);
			for(int i = 0; i < MainGameLoop.height; i++) {
				this.chunks[i] = column.chunks[i];
			}
		}
		catch(Exception e) {
			byte [][][][] heightmap = generateHeightmap4D();
			for(int i = 0; i < MainGameLoop.height; i++) {
				chunks[i] = new Chunk(posX, i, posZ, posW, this.world);
				chunks[i].blocks = new int [8][8][8][8];
				int px = posX * 8;
				int py = i * 8;
				int pz = posZ * 8;
				int pw = posW * 8;
				for(int x = 0; x < 8; x++) {
					for(int y = 0; y < 8; y++) {
						for(int z = 0; z < 8; z++) {
							for(int w = 0; w < 8; w++) {
								int xx = px + x;
								int yy = py + y;
								int zz = pz + z;
								int ww = pw + w;
								if(xx == 0 || xx == MainGameLoop.xzwSize * 8 - 1 || yy == MainGameLoop.height * 8 - 1 || zz == 0 || zz == MainGameLoop.xzwSize * 8 - 1 || ww == 0 || ww == MainGameLoop.xzwSize * 8 - 1) {
									chunks[i].blocks[x][y][z][w] = 7;
								}
								else {
									if(heightmap[x][z][w][yy] < yy) {
										chunks[i].blocks[x][y][z][w] = 1;
									}
								}
							}
						}
					}
				}
			}
			generateSurface();
			generateTrees();
			generateOres();
		}
		for(int i = 0; i < MainGameLoop.height; i++) {
			chunks[i].enclosed(world);
		}
	}
	
	public byte [][][][] generateHeightmap4D() {
		int size = 8;
		double [][][][] noisePoints_2 = largeNoisePoints4D(size * 4, world.seed, 4);
		double [][][][] noisePoints_1 = largeNoisePoints4D(size * 2, world.seed, 2);
		double [][][][] noisePoints0 = fineNoisePoints4D(size, world.seed);
		double [][][][] noisePoints1 = fineNoisePoints4D(size / 2, world.seed);
		double [][][][] noisePoints2 = fineNoisePoints4D(size / 4, world.seed);
		//double [][][][] noisePoints3 = fineNoisePoints4D(size / 4, world.seed);
		byte [][][][] heightmap = new byte[8][8][8][MainGameLoop.height * 8];
		
		int ii = (posX % 2) * 8;
		int jj = (posZ % 2) * 8;
		int kk = (posW % 2) * 8;
		
		int iii = (posX % 4) * 8;
		int jjj = (posZ % 4) * 8;
		int kkk = (posW % 4) * 8;
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				for(int k = 0; k < 8; k++) {
					for(int h = 0; h < MainGameLoop.height * 8; h++) {
						double d = interpolate4D(size, i, j, k, h, noisePoints0) * 64;
						d += interpolate4D(size / 2, i, j, k, h, noisePoints1) * 32;
						d += interpolate4D(size / 4, i, j, k, h, noisePoints2) * 16;
						d += interpolate4D(size * 2, i + ii, j + jj, k + kk, h, noisePoints_1) * 128;
						d += interpolate4D(size * 4, i + iii, j + jjj, k + kkk, h, noisePoints_2) * 256;
						//d += interpolate4D(size / 4, i, j, k, h, noisePoints3) * 16;
						d /= 4;
						heightmap[i][j][k][h] = (byte)(d);
					}
				}
			}
		}
		return heightmap;
	}
	
	public static double interpolate4D(int size, int x, int y, int z, int h, double [][][][] nP){
		int sx = x / size;
		int sy = y / size;
		int sz = z / size;
		int sh = h / size;
		
		double hh1 = ((nP[sx][sy][sz][sh + 1] - nP[sx][sy][sz][sh]) / size) * (h % size) + nP[sx][sy][sz][sh];
    	double hh2 = ((nP[sx + 1][sy][sz][sh + 1] - nP[sx + 1][sy][sz][sh]) / size) * (h % size) + nP[sx + 1][sy][sz][sh];
    	double hh3 = ((nP[sx][sy + 1][sz][sh + 1] - nP[sx][sy + 1][sz][sh]) / size) * (h % size) + nP[sx][sy + 1][sz][sh];
    	double hh4 = ((nP[sx + 1][sy + 1][sz][sh + 1] - nP[sx + 1][sy + 1][sz][sh]) / size) * (h % size) + nP[sx + 1][sy + 1][sz][sh];
    	double hh5 = ((nP[sx][sy][sz + 1][sh + 1] - nP[sx][sy][sz + 1][sh]) / size) * (h % size) + nP[sx][sy][sz + 1][sh];
    	double hh6 = ((nP[sx + 1][sy][sz + 1][sh + 1] - nP[sx + 1][sy][sz + 1][sh]) / size) * (h % size) + nP[sx + 1][sy][sz + 1][sh];
    	double hh7 = ((nP[sx][sy + 1][sz + 1][sh + 1] - nP[sx][sy + 1][sz + 1][sh]) / size) * (h % size) + nP[sx][sy + 1][sz + 1][sh];
    	double hh8 = ((nP[sx + 1][sy + 1][sz + 1][sh + 1] - nP[sx + 1][sy + 1][sz + 1][sh]) / size) * (h % size) + nP[sx + 1][sy + 1][sz + 1][sh];
    	
    	double hz1 = (((hh5 - hh1) / size) * (z % size)) + hh1;
    	double hz2 = (((hh6 - hh2) / size) * (z % size)) + hh2;
    	double hz3 = (((hh7 - hh3) / size) * (z % size)) + hh3;
    	double hz4 = (((hh8 - hh4) / size) * (z % size)) + hh4;
    	
    	double hx1 = (((hz2 - hz1) / size) * (x % size)) + hz1;
    	double hx2 = (((hz4 - hz3) / size) * (x % size)) + hz3;
    	
    	return (((hx2 - hx1) / size) * (y % size)) + hx1;
    }
	
	public double[][][][] fineNoisePoints4D(int size, long seed){
		int terrainSize = 8;
    	double noisePoints [][][][] = new double [terrainSize / size + 1][terrainSize / size + 1][terrainSize / size + 1][MainGameLoop.height * 8 / size + 1];
    	for(int i = 0; i < terrainSize / size + 1; i++){
    		for(int j = 0; j < terrainSize / size + 1; j++){
    			for(int k = 0; k < terrainSize / size + 1; k++){
    				for(int h = 0; h < MainGameLoop.height * 8 / size + 1; h++){
    	    			Random random = new Random();
    	    			int x = posX * 8 + i * size;
    	    			int y = h;
    	    			int z = posZ * 8 + j * size;
    	    			int w = posW * 8 + k * size;
    	    			random.setSeed(seed * (x + 1) * (x + 1) * (w + 1) / (z + 1) * (y + 53) + seed);
    	    			noisePoints[i][j][k][h] = random.nextDouble();
        			}
    			}
    		}
    	}
    	return noisePoints;
    }
	
	public double[][][][] largeNoisePoints4D(int size, long seed, int factor){
    	double noisePoints [][][][] = new double [2][2][2][MainGameLoop.height * 8 / size + 1];
    	for(int i = 0; i < 2; i++){
    		for(int j = 0; j < 2; j++){
    			for(int k = 0; k < 2; k++){
    				for(int h = 0; h < MainGameLoop.height * 8 / size + 1; h++){
    	    			Random random = new Random();
    	    			int x = posX / factor + i;
    	    			int y = h;
    	    			int z = posZ / factor + j;
    	    			int w = posW / factor + k;
    	    			random.setSeed(seed * (x + 1) * (x + 1) * (w + 1) / (z + 1) * (y + 53) + seed);
    	    			noisePoints[i][j][k][h] = random.nextDouble();
        			}
    			}
    		}
    	}
    	return noisePoints;
    }
	
	public void generateSurface() {
		for(int i = posX * 8; i < posX * 8 + 8; i++) {
			for(int k = posZ * 8; k < posZ * 8 + 8; k++) {
				for(int l = posW * 8; l < posW * 8 + 8; l++) {
					boolean sunlight = true;
					for(int j = 0; j < MainGameLoop.height * 8; j++) {
						if(world.getBlock(i, j, k, l) != 0 && world.getBlock(i, j, k, l) != 7) {
							if(world.getBlock(i, j - 1, k, l) == 0 && sunlight) {
								if(j < 50) {
									world.setBlock(i, j, k, l, 9, false);
								}
								else if(j >= 50 && j < 55) {
									
								}
								else if(j < 60) {
									world.setBlock(i, j, k, l, 2, false);
								}
								else {
									world.setBlock(i, j, k, l, 3, false);
								}
								sunlight = false;
							}
							else if((world.getBlock(i, j - 1, k, l) == 0 || world.getBlock(i, j - 1, k, l) == 3) && !sunlight) {
								world.setBlock(i, j, k, l, 2, false);
							}
						}
					}
				}
			}
		}
	}
	
	public void generateTrees() {
		Random random = new Random();
		random.setSeed(world.seed * (posX + 3495) + (posZ * (posW + 353) + 98346));
		for(int i = -4 + posX * 8; i < 12 + posX * 8; i++) {
			for(int k = -4 + posZ * 8; k < 12 + posZ * 8; k++) {
				for(int l = -4 + posW * 8; l < 12 + posW * 8; l++) {
					if(random.nextDouble() > 0.999) {
						for(int j = 0; j < MainGameLoop.height * 8; j++) {
							if(world.getBlock(i, j, k, l) == 3) {
								world.setBlock(i, j, k, l, 5, false);
								Tree.generate(i, j, k, l, world);
							}
						}
					}
				}
			}
		}
	}
	
	public void generateOres() {
		Random random = new Random();
		random.setSeed(world.seed);
		for(int n = 0; n < 16; n++) {
			int x = random.nextInt(8);
			int y = random.nextInt(80) + 48;
			int z = random.nextInt(8);
			int w = random.nextInt(8);
			double size = random.nextDouble() * 1.5 + 0.5;
			for(int i = -(int)(size + 1); i <= size; i++) {
				for(int j = -(int)(size + 1); j <= size; j++) {
					for(int k = -(int)(size + 1); k <= size; k++) {
						for(int l = -(int)(size + 1); l <= size; l++) {
							if(Math.sqrt(i * i + j * j + k * k + l * l) < size) {
								if(world.getBlock(x + i + posX * 8, y + j, z + k + posZ * 8, w + l + posW * 8) == 1) {
									world.setBlock(x + i + posX * 8, y + j, z + k + posZ * 8, w + l + posW * 8, 10, false);
								}
							}
						}
					}
				}
			}
		}
		
		for(int n = 0; n < 8; n++) {
			int x = random.nextInt(8);
			int y = random.nextInt(32) + 96;
			int z = random.nextInt(8);
			int w = random.nextInt(8);
			double size = random.nextDouble() * 1.5 + 0.5;
			for(int i = -(int)(size + 1); i <= size; i++) {
				for(int j = -(int)(size + 1); j <= size; j++) {
					for(int k = -(int)(size + 1); k <= size; k++) {
						for(int l = -(int)(size + 1); l <= size; l++) {
							if(Math.sqrt(i * i + j * j + k * k + l * l) < size) {
								if(world.getBlock(x + i + posX * 8, y + j, z + k + posZ * 8, w + l + posW * 8) == 1) {
									world.setBlock(x + i + posX * 8, y + j, z + k + posZ * 8, w + l + posW * 8, 11, false);
								}
							}
						}
					}
				}
			}
		}
	}
}