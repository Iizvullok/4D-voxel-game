package generators;

import java.util.Random;

import zeugs4D.World;

public class Tree {
	public static void generate(int posX, int posY, int posZ, int posW, World world) {
		Random random = new Random();
		random.setSeed((posX + 4369) * (posY - 2592) * (posZ + 346) + (posW * 34987) * world.seed);
		int height = random.nextInt(3) + 5;
		double width = random.nextDouble() * 2 + 2;
		int widthi = (int)(width);
		for(int i = -widthi; i <= widthi; i++) {
			for(int j = -widthi; j <= widthi; j++) {
				for(int k = -widthi; k <= widthi; k++) {
					for(int l = -widthi; l <= widthi; l++) {
						double d = Math.sqrt(i * i + j * j + k * k + l * l);
						if(d < width) {
							if(world.getBlock(posX + i, (posY - height) + j, posZ + k, posW + l) == 0) {
								if(random.nextDouble() > d - widthi) {
									world.setBlock(posX + i, (posY - height) + j, posZ + k, posW + l, 6, false);
								}
							}
						}
					}
				}
			}
		}
		for(int i = 0; i < height; i++) {
			world.setBlock(posX, posY - i, posZ, posW, 5, false);
		}
	}
}