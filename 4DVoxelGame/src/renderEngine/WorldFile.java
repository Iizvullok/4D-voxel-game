package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import engineTester.MainGameLoop;
import entities.Camera;
import zeugs4D.Chunk;
import zeugs4D.ChunkColumn;
import zeugs4D.World;

public class WorldFile {
	public static void save(World world) throws IOException {
		new File("C:\\4Dvoxelgamesaves").mkdir();
		new File("C:\\4Dvoxelgamesaves\\worldV0.2").mkdir();
		saveStringToFile("" + world.seed, "seed");
		saveStringToFile("" + Camera.posX, "posX");
		saveStringToFile("" + Camera.posY, "posY");
		saveStringToFile("" + Camera.posZ, "posZ");
		saveStringToFile("" + Camera.posW, "posW");
		for(int i = 0; i < MainGameLoop.xzwSize; i++) {
			//for(int j = 0; j < 8; j++) {
				for(int k = 0; k < MainGameLoop.xzwSize; k++) {
					for(int l = 0; l < MainGameLoop.xzwSize; l++) {
						if(world.map[i][k][l] != null) {
							for(int j = 0; j < MainGameLoop.height; j++) {
								if(world.map[i][k][l] != null) {
									if(world.map[i][k][l].save) {
										saveChunk(world.map[i][k][l].chunks[j], "" + convertNumber(i) + convertNumber(j) + convertNumber(k) + convertNumber(l));
									}
								}
							}
							//saveChunk(world.map[i][j][k][l], "" + convertNumber(i) + convertNumber(j) + convertNumber(k) + convertNumber(l));
						}
					}
				}
			//}
		}
	}
	
	public static void createDefaultMapInfoFiles() throws IOException{
		new File("C:\\4Dvoxelgamesaves").mkdir();
		new File("C:\\4Dvoxelgamesaves\\mapinfoV0.2").mkdir();
		saveInfoStringToFile("348563958639568", "seed");
		saveInfoStringToFile("32", "xzwsize");
		saveInfoStringToFile("16", "height");
	}
	
	public static int [] loadMapSize() throws NumberFormatException, IOException {
		int [] a = new int [2];
		a[0] = Integer.parseInt(loadInfoStringFromFile("xzwsize"));
		a[1] = Integer.parseInt(loadInfoStringFromFile("height"));
		return a;
	}
	
	public static long loadSeed() throws NumberFormatException, IOException {
		return Long.parseLong(loadInfoStringFromFile("seed"));
	}
	
	public static World loadMisc() throws IOException{
		World world = new World(Long.parseLong(loadStringFromFile("seed")));
		Camera.posX = Double.parseDouble(loadStringFromFile("posX"));
		Camera.posY = Double.parseDouble(loadStringFromFile("posY"));
		Camera.posZ = Double.parseDouble(loadStringFromFile("posZ"));
		Camera.posW = Double.parseDouble(loadStringFromFile("posW"));
		return world;
	}
	
	public static World load() throws IOException {
		World world = new World(Long.parseLong(loadStringFromFile("seed")));
		Camera.posX = Double.parseDouble(loadStringFromFile("posX"));
		Camera.posY = Double.parseDouble(loadStringFromFile("posY"));
		Camera.posZ = Double.parseDouble(loadStringFromFile("posZ"));
		Camera.posW = Double.parseDouble(loadStringFromFile("posW"));
		for(int i = 0; i < MainGameLoop.xzwSize; i++) {
			//for(int j = 0; j < 8; j++) {
				for(int k = 0; k < MainGameLoop.xzwSize; k++) {
					for(int l = 0; l < MainGameLoop.xzwSize; l++) {
						for(int j = 0; j < MainGameLoop.height; j++) {
							world.map[i][k][l].chunks[j] = loadChunk(convertNumber(i) + convertNumber(j) + convertNumber(k) + convertNumber(l), world);
						}
						//world.map[i][j][k][l] = loadChunk(convertNumber(i) + convertNumber(j) + convertNumber(k) + convertNumber(l), world);
					}
				}
			//}
		}
		return world;
	}
	
	public static void saveChunk(Chunk chunk, String name) throws IOException {
		String string = "";
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				for(int k = 0; k < 8; k++) {
					for(int l = 0; l < 8; l++) {
						string += convertNumber(chunk.blocks[i][j][k][l]);
					}
				}
			}
		}
		saveStringToFile(string, name);
	}
	
	public static ChunkColumn loadChunkColumn(int x, int z, int w, World world) throws IOException {
		ChunkColumn column = new ChunkColumn(x, z, w, world);
		for(int i = 0; i < MainGameLoop.height; i++) {
			//System.out.println(convertNumber(x) + convertNumber(i) + convertNumber(z) + convertNumber(w));
			try {
				column.chunks[i] = loadChunk(convertNumber(x) + convertNumber(i) + convertNumber(z) + convertNumber(w), world);
			}
			catch(Exception e) {
				//e.printStackTrace();
				throw e;
			}
		}
		return column;
	}
	
	public static Chunk loadChunk(String name, World world) throws IOException {
		//System.out.println("loading chunk " + name);
		String string = "";
		try {
			string = loadStringFromFile(name);
		}
		catch(Exception e) {
			//if(name.equals("01000001")) {
				//e.printStackTrace();
			//}
		}
		//System.out.println("dings");
		Chunk chunk = new Chunk(Integer.parseInt(name.substring(0, 2)), Integer.parseInt(name.substring(2, 4)), Integer.parseInt(name.substring(4, 6)), Integer.parseInt(name.substring(6, 8)), world);
		//System.out.println("BUILDING CHUNK!" + string);
		//System.out.println(string.substring(0, 100));
		chunk.blocks = new int [8][8][8][8];
		for(int i = 0; i < 4096; i++) {
			//System.out.println(i);
			int id = Integer.parseInt(string.substring(i * 2, i * 2 + 2));
			chunk.blocks[i / 512][(i / 64) % 8][(i / 8) % 8][i % 8] = id;
		}
		//System.out.println("RETURN CHUNK!");
		return chunk;
	}
	
	public static String convertNumber(int b) {
		if(b < 10) {
			return "0" + b;
		}
		else {
			return "" + b;
		}
	}
	
	public static int convertString(String s) {
		return Integer.parseInt(s);
	}
	
	public static void saveStringToFile(String string, String name) throws IOException {
		File file = new File("c://4Dvoxelgamesaves//worldV0.2//" + name + ".txt");
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(string);
		writer.close();
	}
	
	public static String loadStringFromFile(String name) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("c://4Dvoxelgamesaves//worldV0.2//" + name + ".txt"));
		String ret;
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
		    sb.append(line);
		    //sb.append(System.lineSeparator());
		    line = br.readLine();
		}
		ret = sb.toString();
		br.close();
		//System.out.println(ret.charAt(0));
		return ret;
	}
	
	public static void saveInfoStringToFile(String string, String name) throws IOException {
		File file = new File("c://4Dvoxelgamesaves//mapinfoV0.2//" + name + ".txt");
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(string);
		writer.close();
	}
	
	public static String loadInfoStringFromFile(String name) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("c://4Dvoxelgamesaves//mapinfoV0.2//" + name + ".txt"));
		String ret;
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        //sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    ret = sb.toString();
		} finally {
		    br.close();
		}
		return ret;
	}
}