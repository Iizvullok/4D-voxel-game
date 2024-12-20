package terrains;

import org.lwjgl.util.vector.Vector3f;

import engineTester.MainGameLoop;
import models.RawModel;
import renderEngine.Loader;
import textures.ModelTexture;
import zeugs4D.Quad;

public class Terrain {
	public static boolean firstFrame = true;
	private RawModel model;
	private ModelTexture texture;
	
	public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture, Quad [] polygons){
		this.texture = texture;
		this.model = generateTerrain(loader, polygons);
	}

	public RawModel getModel() {
		return model;
	}

	public ModelTexture getTexture() {
		return texture;
	}

	private RawModel generateTerrain(Loader loader, Quad[] polygons){
		int n = polygons.length;
		int count = n;
		
		float[] vertices = new float[count * 12];
		float[] normals = new float[count * 12];
		float[] textureCoords = new float[count * 8];
		int[] indices = new int[6 * count];
		float yOffset = MainGameLoop.height * 8;
		
		for(int i = 0; i < count; i++) {
			Vector3f [] poly = polygons[i].v;
			int id = polygons[i].i;
			int s = 4;
			int x = id % s;
			int y = id / s;
			float smallNumber = 0.002f;
			float textureXMin = x / ((float)(s)) + smallNumber;
			float textureXMax = (x + 1f) / ((float)(s)) - smallNumber;
			float textureYMin = y / ((float)(s)) + smallNumber;
			float textureYMax = (y + 1f) / ((float)(s)) - smallNumber;
			
			vertices[(i * 12) + 0] = poly[0].x;
			vertices[(i * 12) + 1] = poly[0].y + yOffset;
			vertices[(i * 12) + 2] = poly[0].z;
			normals[(i * 12) + 0] = polygons[i].n.x;
			normals[(i * 12) + 1] = polygons[i].n.y;
			normals[(i * 12) + 2] = polygons[i].n.z;
			textureCoords[(i * 8) + 0] = textureXMin;
			textureCoords[(i * 8) + 1] = textureYMin;
			
			vertices[(i * 12) + 3] = poly[1].x;
			vertices[(i * 12) + 4] = poly[1].y + yOffset;
			vertices[(i * 12) + 5] = poly[1].z;
			normals[(i * 12) + 3] = polygons[i].n.x;
			normals[(i * 12) + 4] = polygons[i].n.y;
			normals[(i * 12) + 5] = polygons[i].n.z;
			textureCoords[(i * 8) + 2] = textureXMin;
			textureCoords[(i * 8) + 3] = textureYMax;
			
			vertices[(i * 12) + 6] = poly[2].x;
			vertices[(i * 12) + 7] = poly[2].y + yOffset;
			vertices[(i * 12) + 8] = poly[2].z;
			normals[(i * 12) + 6] = polygons[i].n.x;
			normals[(i * 12) + 7] = polygons[i].n.y;
			normals[(i * 12) + 8] = polygons[i].n.z;
			textureCoords[(i * 8) + 4] = textureXMax;
			textureCoords[(i * 8) + 5] = textureYMin;
			
			vertices[(i * 12) + 9] = poly[3].x;
			vertices[(i * 12) + 10] = poly[3].y + yOffset;
			vertices[(i * 12) + 11] = poly[3].z;
			normals[(i * 12) + 9] = polygons[i].n.x;
			normals[(i * 12) + 10] = polygons[i].n.y;
			normals[(i * 12) + 11] = polygons[i].n.z;
			textureCoords[(i * 8) + 6] = textureXMax;
			textureCoords[(i * 8) + 7] = textureYMax;
			
			indices[(i * 6) + 0] = i * 4 + 0;
			indices[(i * 6) + 1] = i * 4 + 1;
			indices[(i * 6) + 2] = i * 4 + 2;
			
			indices[(i * 6) + 3] = i * 4 + 3;
			indices[(i * 6) + 4] = i * 4 + 2;
			indices[(i * 6) + 5] = i * 4 + 1;
		}
		firstFrame = false;
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
}