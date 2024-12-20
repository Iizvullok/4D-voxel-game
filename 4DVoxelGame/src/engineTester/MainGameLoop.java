package engineTester;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.WorldFile;
import terrains.Terrain;
import textures.ModelTexture;
import zeugs4D.ChunkColumn;
import zeugs4D.Quad;
import zeugs4D.Vector4D;
import zeugs4D.World;
import entities.Camera;
import entities.Light;
import guis.GuiTexture;

public class MainGameLoop {
	//348563958639568
	public static boolean closeRequested = false;
	public static World map;
	public static boolean updateCrosssection = false;
	public static boolean renderAll = false;
	public static int distance = 2;
	public static int frame = 0;
	public static long seed = 348563958639568L;
	public static boolean recalculate = false;
	public static int xzwSize = 32;
	public static int height = 16;
	//Now coding on a dark background like people are supposed to do. :)
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		Light light = new Light(new Vector3f(100,200,200),new Vector3f(1,1,1));
		Camera camera = new Camera();	
		MasterRenderer renderer = new MasterRenderer();
		
		//long before = System.currentTimeMillis();
		boolean loaded = true;
		map = new World(seed);
		
		try {
			seed = WorldFile.loadSeed();
			int a[] = WorldFile.loadMapSize();
			xzwSize = a[0];
			height = a[1];
		}
		catch(Exception e) {
			try {
				WorldFile.createDefaultMapInfoFiles();
			}
			catch(Exception e1) {
				e1.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(0);
			map = WorldFile.loadMisc();
			//map = WorldFile.load();
		}
		catch(Exception e) {
			System.out.println("Failed to load functioning world! Generating new world instead!");
			/*for(int i = 0; i < xzwSize; i++) {
				//for(int j = 0; j < 8; j++) {
					for(int k = 0; k < xzwSize; k++) {
						for(int l = 0; l < xzwSize; l++) {
							//map.makeChunkColumn(i, k, l);
							//map.map[i][k][l] = new ChunkColumn(i, k, l, map);
							//map.makeChunk(i, j, k, l);
						}
					}
				//}
			}
			for(int i = 0; i < xzwSize; i++) {
				for(int j = 0; j < 8; j++) {
					for(int k = 0; k < xzwSize; k++) {
						for(int l = 0; l < xzwSize; l++) {
							//map.makeChunkColumn(i, k, l);
							//map.map[i][k][l].chunks[j].enclosed(map);
							//map.makeChunk(i, j, k, l);
						}
					}
				}
			}*/
			e.printStackTrace();
			loaded = false;
		}
		
		if(!loaded) {
			//map.generateSurface();
			//map.generateOres();
			//map.generateTrees();
		}
		//map.enclosed();
		System.out.println("World loaded/generated successfully!");
		//System.out.println(System.currentTimeMillis() - before);
		
		ModelTexture textures =  new ModelTexture(loader.loadTexture("terrain"));
		Terrain[] terrain = updateTerrain(textures, loader);
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("gui"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);
		
		//GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		
		while(!closeRequested){
			frame++;
			if(updateCrosssection) {
				terrain = updateTerrain(textures, loader);
				updateCrosssection = false;
			}
			
			for(int i = 0; i < terrain.length; i++) {
				if(terrain[i] != null) {
					renderer.processTerrain(terrain[i]);
				}
			}
			
			camera.move();
			renderer.render(light, camera);
			
			//guiRenderer.render(guis);
			
			DisplayManager.updateDisplay();
		}
		try {
			Thread.sleep(1);
			WorldFile.save(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	public static Terrain [] updateTerrain(ModelTexture texture, Loader loader) {
			loader.cleanUpHalfways();
			Terrain [] terrain = new Terrain[(distance * 2 + 1) * (distance * 2 + 1) * (distance * 2 + 1) * (distance * 2 + 3)];
			int index = 0;
			
			int xChunkPos = (int) (Camera.posX / 8);
			int yChunkPos = (int) (height - Camera.posY / 8);
			int zChunkPos = (int) (Camera.posZ / 8);
			int wChunkPos = (int) (Camera.posW / 8);
			
			int xStart = xChunkPos - distance;
			int xStop = xChunkPos + distance;
			int yStart = yChunkPos - distance;
			int yStop = yChunkPos + distance + 2;
			int zStart = zChunkPos - distance;
			int zStop = zChunkPos + distance;
			int wStart = wChunkPos - distance;
			int wStop = wChunkPos + distance;
			
			boolean [][][][] really = reallyArr();
			
			for(int i = xStart; i <= xStop; i++) {
				for(int j = yStart; j <= yStop; j++) {
					for(int k = zStart; k <= zStop; k++) {
						for(int l = wStart; l <= wStop; l++) {
							if(i >= 0 && i < xzwSize && j >= 0 && j < height && k >= 0 && k < xzwSize && l >= 0 && l < xzwSize) {
								if(map.map != null) {
									//System.out.println("Map exists!");
									if(map.map[i][k][l] != null){
										//System.out.println("Column exists!");
										if(map.map[i][k][l].chunks[j] != null && really[i][j][k][l]) {
											//System.out.println("Chunk exists!");
											Quad[] crosssection = map.map[i][k][l].chunks[j].getCrossection();
											int count = 0;
											for(int c = 0; c < crosssection.length; c++) {
												if(crosssection[c] != null) {
													count++;
												}
											}
											Quad [] crosssection2 = new Quad[count];
											count = 0;
											for(int c = 0; c < crosssection.length; c++) {
												if(crosssection[c] != null) {
													crosssection2[count] = crosssection[c];
													count++;
												}
											}
											terrain [index] = new Terrain(0,0,loader, texture, crosssection2);
											index++;
										}
									}
									else {
										try {
											//System.out.println("Trying to load!");
											map.map[i][k][l] = WorldFile.loadChunkColumn(i, k, l, map);
											for(int d = 0; d < height; d++) {
												map.map[i][k][l].chunks[d].enclosed(map);
											}
										}
										catch(Exception e) {
											//System.out.println("Failed to load!");
											map.map[i][k][l] = new ChunkColumn(i, k, l, map);
											map.map[i][k][l].generate();
										}
									}
								}
							}
						}
					}
				}
			}
			return terrain;
	}
	
	public static boolean[][][][] reallyArr() {
		Vector4D dir2 = getFacingDir(Camera.rotationXZPI, Camera.rotationXWPI, Camera.rotationYZPI);
		boolean [][][][] really = new boolean [xzwSize][height][xzwSize][xzwSize];
		double cpx = Camera.posX;
		double cpy = (height * 8 - Camera.posY);
		double cpz = Camera.posZ;
		double cpw = Camera.posW;
		
		if(isValidChunk((int)(cpx / 8), (int)(cpy / 8), (int)(cpz / 8), (int)(cpw / 8))) {
			really [(int)(cpx / 8)][(int)(cpy / 8)][(int)(cpz / 8)][(int)(cpw / 8)] = true;
		}
		
		cpx -= dir2.v0 * 4;
		cpy -= dir2.v1 * 4;
		cpz -= dir2.v2 * 4;
		cpw -= dir2.v3 * 4;
		
		for(int x = -3; x <= 3; x++) {
			for(int z = -3; z <= 3; z++) {
				Vector4D dir = getFacingDir(Camera.rotationXZPI + x * 0.15, Camera.rotationXWPI, Camera.rotationYZPI + z * 0.15);
				double accuracy = 1;
				
				for(int i = 0; i < 24; i++) {
					if(isValidChunk((int)((cpx + dir.v0 * (i / accuracy) + 0.5) / 8), (int)((cpy + dir.v1 * (i / accuracy) - 1.6) / 8), (int)((cpz + dir.v2 * (i / accuracy)) / 8), (int)((cpw + dir.v3 * (i / accuracy) + 0.5)) / 8)) {
						really [(int)((cpx + dir.v0 * (i / accuracy) + 0.5)) / 8][(int)((cpy + dir.v1 * (i / accuracy) - 1.6) / 8)][(int)((cpz + dir.v2 * (i / accuracy)) / 8)][(int)((cpw + dir.v3 * (i / accuracy) + 0.5) / 8)] = true;
					}
				}
			}
		}
		
		return really;
	}
	
	public static boolean isValidChunk(int x, int y, int z, int w) {
		if(x >= 0 && x < xzwSize && y >= 0 && y < height && z >= 0 && z < xzwSize && w >= 0 && w < xzwSize) {
			return true;
		}
		return false;
	}
	
	public static boolean isInsideChunk(int cX, int cY, int cZ, int cW, double x, double y, double z, double w) {
		if(x > cX * 8 && x < cX * 8 + 8) {
			if(y > cY * 8 && y < cY * 8 + 8) {
				if(z > cZ * 8 && z < cZ * 8 + 8) {
					if(w > cW * 8 && w < cW * 8 + 8) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static Vector4D getFacingDir(double rXZ, double rXW, double rYZ) {
		return new Vector4D(-Math.sin(rXZ) * Math.cos(rXW) * Math.cos(rYZ), Math.sin(rYZ), -Math.cos(rXZ) * Math.cos(rYZ), -Math.sin(rXW) * -Math.sin(rXZ) * Math.cos(rYZ));
	}
}