package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import engineTester.MainGameLoop;
import renderEngine.DisplayManager;
import zeugs4D.Vector4D;
import zeugs4D.World;

public class Camera {
	static int offset = 0;
	public static double rotationXY = 0;
	public static double rotationXZ = 0;
	public static double rotationYZ = 0;
	public static double rotationXW = 0;
	public static double rotationYW = 0;
	public static double rotationZW = 0;
	public static double rotationXYPI = 0;
	public static double rotationXZPI = 0;
	public static double rotationYZPI = 0;
	public static double rotationXWPI = 0;
	public static double rotationYWPI = 0;
	public static double rotationZWPI = 0;
	public static double fakeXPosition = 0;
	public static double fakeXSpeed = 0;
	public static double crossW = 0;
	public static double previousPosX = 32;
	public static double previousPosW = 32;
	public static double posX = MainGameLoop.xzwSize * 4 - offset;
	public static double posY = MainGameLoop.height * 8;
	public static double posZ = MainGameLoop.xzwSize * 4 - offset;
	public static double posW = MainGameLoop.xzwSize * 4 - offset;
	public static double velX = 0;
	public static double velY = 0;
	public static double velZ = 0;
	public static double velW = 0;
	public static double eyeHeight = 1;
	public static boolean onGround = true;
	public static boolean sprint = false;
	public static boolean sneak = false;
	public static boolean forwards = false;
	public static boolean backwards = false;
	public static boolean left = false;
	public static boolean right = false;
	public static boolean ana = false;
	public static boolean kata = false;
	public static int oldChunkX = (int) (posX / 8);
	public static int oldChunkY = (int) (MainGameLoop.height - posY / 8);
	public static int oldChunkZ = (int) (posZ / 8);
	public static int oldChunkW = (int) (posW / 8);
	public static double xw = 0;
	public static boolean breakeBlock = false;
	public static int blockPlaceId = 0;
	public static long lastMouseBlockPlaced = 0;
	
	public Camera(){
		
	}
	
	public static void controlMovement() {
		if(ana && kata) {
			ana = false;
			kata = false;
		}
		if(right && left) {
			right = false;
			left = false;
		}
		if(forwards && backwards) {
			forwards = false;
			backwards = false;
		}
		if(sneak && sprint) {
			sneak = false;
			sprint = false;
		}
		if(forceSneak()) {
			sneak = true;
			sprint = false;
		}
	}
	
	public static void move() {
		previousPosX = posX;
		previousPosW = posW;
		updatePIRotation();
		xw = 0;
		input();
		controlMovement();
		//new Vector4D(posX, posY, posZ, posW).print();
		//System.out.println(rotationXW);
		double xz = rotationXZ;
		double yz = rotationYZ;
		rotationXZ -= (double)((double)(Mouse.getDX()) / 10D);
		rotationYZ -= (double)((double)(Mouse.getDY()) / 10D);
		if(xz != rotationXZ && yz != rotationYZ) {
			MainGameLoop.updateCrosssection = true;
		}
		double dings = Mouse.getDWheel() + (double)(xw);
		if(dings != 0) {
			World.calculateCrosssectionFromSurface();
			MainGameLoop.updateCrosssection = true;
			fakeXPosition = 0;
		}
		rotationXW -= (double)(dings / 24);
		Mouse.setGrabbed(true);
		
		if(Mouse.isButtonDown(0) && (System.currentTimeMillis() - lastMouseBlockPlaced) > 150) {
			placeBlock();
			lastMouseBlockPlaced = System.currentTimeMillis();
		}
		
		float d = DisplayManager.getFrameTimeSeconds();
		
		if(rotationYZ > 90) {
			rotationYZ = 90;
		}
		if(rotationYZ < -90) {
			rotationYZ = -90;
		}
		
		double moveSpeed = 4;
		if(sprint) {
			moveSpeed = 7;
		}
		if(sneak) {
			moveSpeed = 2;
		}

		Vector4D fb = getForwardsDirectionFly();
		velX += moveSpeed * fb.v0;
		velZ += moveSpeed * fb.v2;
		velW += moveSpeed * fb.v3;
		Vector4D lr = getSidewardsDirectionFly();
		velX += moveSpeed * lr.v0;
		velZ += moveSpeed * lr.v2;
		velW += moveSpeed * lr.v3;
		Vector4D ak = getPerpendicularDirectionFly();
		velX += moveSpeed * ak.v0;
		velZ += moveSpeed * ak.v2;
		velW += moveSpeed * ak.v3;
		
		if(!xBlocked(d)) {
			posX += velX * d;
		}
		else {
			velX = 0;
			World.calculateCrosssectionFromSurface();
			MainGameLoop.updateCrosssection = true;
			fakeXPosition = 0;
		}
		
		if(!zBlocked(d)) {
			posZ += velZ * d;
		}
		else {
			velZ = 0;
		}
		
		if(!wBlocked(d)) {
			posW += velW * d;
		}
		else {
			velW = 0;
			World.calculateCrosssectionFromSurface();
			MainGameLoop.updateCrosssection = true;
			fakeXPosition = 0;
		}
		
		velX *= 0;
		velZ *= 0;
		velW *= 0;
		fakeXSpeed = calculateFakeXSpeed(posX - previousPosX, posW - previousPosW);
		fakeXPosition += fakeXSpeed;
		
		if(yBlocked(d)) {
			velY = 0;
			onGround = true;
		}
		else {
			velY -= 0.0015;
			posY += velY;
		}
		
		int newChunkX = (int) (posX / 8);
		int newChunkY = (int) (8 - posY / 8);
		int newChunkZ = (int) (posZ / 8);
		int newChunkW = (int) (posW / 8);
		
		if(newChunkX != oldChunkX || newChunkY != oldChunkY || newChunkZ != oldChunkZ || newChunkW != oldChunkW) {
			oldChunkX = newChunkX;
			oldChunkY = newChunkY;
			oldChunkZ = newChunkZ;
			oldChunkW = newChunkW;
			MainGameLoop.updateCrosssection = true;
		}
	}
	
	public static double calculateFakeXSpeed(double x, double w) {
		double d = Math.sqrt(x * x + w * w);
		double rot = rotationXZ;
		boolean f = forwards;
		boolean b = backwards;
		boolean l = left;
		boolean r = right;
		
		if(f && b) {
			f = false;
			b = false;
		}
		if(l && r) {
			r = false;
			l = false;
		}
		if(!l && !b && !r && !f) {
			//System.out.println("no movement");
			return 0;
		}
		
		if(f && !(b || l || r)) {
			//System.out.println("only forwards");
			rot += 0;
		}
		else if(f && r && !(b || l)) {
			//System.out.println("forwards right");
			rot -= 45;
		}
		else if(r && !(b || l || f)) {
			//System.out.println("only right");
			rot -= 90;
		}
		else if(b && r && !(f || l)) {
			//System.out.println("backwards right");
			rot -= 135;
		}
		else if(b && !(f || l || r)) {
			//System.out.println("only backwards");
			rot -= 180;
		}
		else if(b && l && !(f || r)) {
			//System.out.println("backwards left");
			rot -= 225;
		}
		else if(l && !(f || b || r)) {
			//System.out.println("only left");
			rot -= 270;
		}
		else if(f && l && !(b || r)) {
			//System.out.println("forwards left");
			rot -= 315;
		}
		
		rot = clampRotation(rot);
		
		if(rot < 0) {
			return -d;
		}
		else {
			return d;
		}
	}
	
	public static double clampRotation(double rotation) {
		double r = rotation % 360D;
		if(r > 0) {
			r -= 180;
		}
		else {
			r += 180;
		}
		return r;
	}
	
	public static void input() {
		if(!Keyboard.isKeyDown(Keyboard.KEY_W)){
			forwards = false;
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_S)){
			backwards = false;
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_D)){
			right = false;
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_A)){
			left = false;
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_E)){
			ana = false;
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_Q)){
			kata = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			forwards = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			backwards = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			right = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			left = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_E)){
			ana = true;
			World.calculateCrosssectionFromSurface();
			MainGameLoop.updateCrosssection = true;
			fakeXPosition = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			kata = true;
			World.calculateCrosssectionFromSurface();
			MainGameLoop.updateCrosssection = true;
			fakeXPosition = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			if(onGround) {
				velY = 0.07;
				onGround = false;
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_G)){
			long xw = Math.round(rotationXW / 90);
			rotationXW = xw * 90;
			World.calculateCrosssectionFromSurface();
			MainGameLoop.updateCrosssection = true;
			fakeXPosition = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			sprint = true;
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			sprint = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
			sneak = true;
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
			sneak = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
			rotationXZ += 2;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
			rotationXZ -= 2;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
			rotationYZ -= 2;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
			rotationYZ += 2;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_R)){
			xw = -75;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_T)){
			posX = MainGameLoop.xzwSize * 4;
			posY = MainGameLoop.height * 8;
			posZ = MainGameLoop.xzwSize * 4;
			posW = MainGameLoop.xzwSize * 4;
			World.calculateCrosssectionFromSurface();
			MainGameLoop.updateCrosssection = true;
			fakeXPosition = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_F)){
			xw = 75;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			MainGameLoop.closeRequested = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_L)) {
			MainGameLoop.updateCrosssection = true;
			MainGameLoop.recalculate = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_0)){
			blockPlaceId = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_1)){
			blockPlaceId = 1;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_2)){
			blockPlaceId = 2;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_3)){
			blockPlaceId = 3;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_4)){
			blockPlaceId = 4;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_5)){
			blockPlaceId = 5;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_6)){
			blockPlaceId = 6;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_7)){
			blockPlaceId = 7;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_8)){
			blockPlaceId = 8;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_9)){
			blockPlaceId = 9;
		}
	}
	
	public static void placeBlock() {
		int yOffset = 0;
		double accuracy = 24;
		if(sneak) {
			yOffset = 1;
		}
		if(blockPlaceId == 0) {
			Vector4D dir = getFacingDir();
			int c = 0;
			for(int i = 0; i < 128; i++) {
				if(MainGameLoop.map.getBlock((int)(posX + dir.v0 * (i / accuracy) + 0.5), (int)(((MainGameLoop.height * 8) - posY) + dir.v1 * (i / accuracy) - 1.6 + yOffset), (int)(posZ + dir.v2 * (i / accuracy)), (int)(posW + dir.v3 * (i / accuracy) + 0.5)) == 7) {
					return;
				}
				else if(MainGameLoop.map.getBlock((int)(posX + dir.v0 * (i / accuracy) + 0.5), (int)(((MainGameLoop.height * 8) - posY) + dir.v1 * (i / accuracy) - 1.6 + yOffset), (int)(posZ + dir.v2 * (i / accuracy)), (int)(posW + dir.v3 * (i / accuracy) + 0.5)) != 0) {
					c = i;
					break;
				}
				if(i == 127) {
					return;
				}
			}
			MainGameLoop.map.setBlock((int)(posX + dir.v0 * (c / accuracy) + 0.5), (int)(((MainGameLoop.height * 8) - posY) + dir.v1 * (c / accuracy) - 1.6 + yOffset), (int)(posZ + dir.v2 * (c / accuracy)), (int)(posW + dir.v3 * (c / accuracy) + 0.5), 0, true);
		}
		else {
			Vector4D dir = getFacingDir();
			int c = 0;
			for(int i = 0; i < 128; i++) {
				if(MainGameLoop.map.getBlock((int)(posX + dir.v0 * (i / accuracy) + 0.5), (int)(((MainGameLoop.height * 8) - posY) + dir.v1 * (i / accuracy) - 1.6 + yOffset), (int)(posZ + dir.v2 * (i / accuracy)), (int)(posW + dir.v3 * (i / accuracy) + 0.5)) != 0) {
					c = i - 1;
					break;
				}
				if(i == 127) {
					return;
				}
			}
			MainGameLoop.map.setBlock((int)(posX + dir.v0 * (c / accuracy) + 0.5), (int)(((MainGameLoop.height * 8) - posY) + dir.v1 * (c / accuracy) - 1.6 + yOffset), (int)(posZ + dir.v2 * (c / accuracy)), (int)(posW + dir.v3 * (c / accuracy) + 0.5), blockPlaceId, true);
		}
	}
	
	public static void updatePIRotation() {
		rotationXYPI = (rotationXY / 180) * Math.PI;
		rotationXZPI = (rotationXZ / 180) * Math.PI;
		rotationYZPI = (rotationYZ / 180) * Math.PI;
		rotationXWPI = (rotationXW / 180) * Math.PI;
		rotationYWPI = (rotationYW / 180) * Math.PI;
		rotationZWPI = (rotationZW / 180) * Math.PI;
	}
	
	public static Vector4D getFacingDir() {
		return new Vector4D(-Math.sin(rotationXZPI) * Math.cos(rotationXWPI) * Math.cos(rotationYZPI), Math.sin(rotationYZPI), -Math.cos(rotationXZPI) * Math.cos(rotationYZPI), -Math.sin(rotationXWPI) * -Math.sin(rotationXZPI) * Math.cos(rotationYZPI));
	}
	
	public static Vector4D getPerpendicularDirectionFly() {
		if(ana) {
			return new Vector4D(Math.sin(rotationXWPI), 0, 0, Math.cos(rotationXWPI));
		}
		else if(kata) {
			return new Vector4D(-Math.sin(rotationXWPI), 0, 0, -Math.cos(rotationXWPI));
		}
		else {
			return new Vector4D(0, 0, 0, 0);
		}
	}
	
	public static Vector4D getSidewardsDirectionFly() {
		if(left) {
			return new Vector4D(-Math.sin(rotationXZPI + Math.PI / 2) * Math.cos(rotationXWPI), 0, -Math.cos(rotationXZPI + Math.PI / 2), -Math.sin(rotationXWPI) * -Math.sin(rotationXZPI + Math.PI / 2));
		}
		else if(right) {
			return new Vector4D(Math.sin(rotationXZPI + Math.PI / 2) * Math.cos(rotationXWPI), 0, Math.cos(rotationXZPI + Math.PI / 2), Math.sin(rotationXWPI) * -Math.sin(rotationXZPI + Math.PI / 2));
		}
		else {
			return new Vector4D(0, 0, 0, 0);
		}
	}
	
	public static Vector4D getForwardsDirectionFly() {
		if(backwards) {
			return new Vector4D(Math.sin(rotationXZPI) * Math.cos(rotationXWPI), 0, Math.cos(rotationXZPI), Math.sin(rotationXWPI) * -Math.sin(rotationXZPI));
		}
		else if(forwards) {
			return new Vector4D(-Math.sin(rotationXZPI) * Math.cos(rotationXWPI), 0, -Math.cos(rotationXZPI), -Math.sin(rotationXWPI) * -Math.sin(rotationXZPI));
		}
		else {
			return new Vector4D(0, 0, 0, 0);
		}
	}
	
	public static double fakeXSpeedSidewards() {
		if(left) {
			return -Math.cos(rotationXZPI);
		}
		else if(right) {
			return Math.cos(rotationXZPI);
		}
		else {
			return 0;
		}
	}
	
	public static double fakeXSpeedForwards() {
		if(forwards) {
			return Math.cos(rotationXZPI + Math.PI / 2);
		}
		else if(backwards) {
			return -Math.cos(rotationXZPI + Math.PI / 2);
		}
		else {
			return 0;
		}
	}

	public Vector3f getPosition() {
		float height = 1.8f;
		if(sneak) {
			height = 0.8f;
		}
		return new Vector3f((float)(fakeXPosition), (float) posY + height + 1, (float) posZ);
	}

	public float getPitch() {
		return (float) rotationYZ;
	}

	public float getYaw() {
		return (float) -rotationXZ;
	}

	public float getRoll() {
		return (float)(rotationXY);
	}
	
	public static boolean xBlocked(double d) {
		int ymin = (int)((MainGameLoop.height * 8 - 0.2) - posY);
		int ymax = (int)((MainGameLoop.height * 8 - 1.8) - posY);
		int zmin = (int)(posZ - 0.2);
		int zmax = (int)(posZ + 0.2);
		int wmin = (int)(posW + 0.3);
		int wmax = (int)(posW + 0.7);
		if(sneak) {
			ymax = ymax + 1;
		}
		World map = MainGameLoop.map;
		
		if(velX <= 0) {
			int x = (int)(posX + 0.3 + velX * d);
			if(map.getBlock(x, ymin, zmin, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymax, zmin, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymin, zmax, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymax, zmax, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymin, zmin, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymax, zmin, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymin, zmax, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymax, zmax, wmax) != 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			int x = (int)(posX + 0.7 + velX * d);
			if(map.getBlock(x, ymin, zmin, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymax, zmin, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymin, zmax, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymax, zmax, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymin, zmin, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymax, zmin, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymin, zmax, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(x, ymax, zmax, wmax) != 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public static boolean yBlocked(double d) {
		int xmin = (int)(posX + 0.3);
		int xmax = (int)(posX + 0.7);
		int zmin = (int)(posZ - 0.2);
		int zmax = (int)(posZ + 0.2);
		int wmin = (int)(posW + 0.3);
		int wmax = (int)(posW + 0.7);
		World map = MainGameLoop.map;
		
		if(velY <= 0) {
			int y = (int)((MainGameLoop.height * 8) - posY + velY * d);
			if(map.getBlock(xmin, y, zmin, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, y, zmin, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, y, zmax, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, y, zmax, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, y, zmin, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, y, zmin, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, y, zmax, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, y, zmax, wmax) != 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			int y = (int)((MainGameLoop.height * 8 - 2) - posY + velY * d);
			if(sneak) {
				y = y + 1;
			}
			if(map.getBlock(xmin, y, zmin, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, y, zmin, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, y, zmax, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, y, zmax, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, y, zmin, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, y, zmin, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, y, zmax, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, y, zmax, wmax) != 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public static boolean zBlocked(double d) {
		int xmin = (int)(posX + 0.7);
		int xmax = (int)(posX + 0.3);
		int ymin = (int)((MainGameLoop.height * 8 - 0.2) - posY);
		int ymax = (int)((MainGameLoop.height * 8 - 1.8) - posY);
		int wmin = (int)(posW + 0.3);
		int wmax = (int)(posW + 0.7);
		if(sneak) {
			ymax = ymax + 1;
		}
		World map = MainGameLoop.map;
		
		if(velZ <= 0) {
			int z = (int)(posZ - 0.2 + velZ * d);
			if(map.getBlock(xmin, ymin, z, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymin, z, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymax, z, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymax, z, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymin, z, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymin, z, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymax, z, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymax, z, wmax) != 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			int z = (int)(posZ + 0.2 + velZ * d);
			if(map.getBlock(xmin, ymin, z, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymin, z, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymax, z, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymax, z, wmin) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymin, z, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymin, z, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymax, z, wmax) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymax, z, wmax) != 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public static boolean wBlocked(double d) {
		int xmin = (int)(posX + 0.7);
		int xmax = (int)(posX + 0.3);
		int ymin = (int)((MainGameLoop.height * 8 - 0.2) - posY);
		int ymax = (int)((MainGameLoop.height * 8 - 1.8) - posY);
		int zmin = (int)(posZ - 0.2);
		int zmax = (int)(posZ + 0.2);
		if(sneak) {
			ymax = ymax + 1;
		}
		World map = MainGameLoop.map;
		
		if(velW <= 0) {
			int w = (int)(posW + 0.3 + velW * d);
			if(map.getBlock(xmin, ymin, zmin, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymin, zmin, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymax, zmin, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymax, zmin, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymin, zmax, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymin, zmax, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymax, zmax, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymax, zmax, w) != 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			int w = (int)(posW + 0.7 + velW * d);
			if(map.getBlock(xmin, ymin, zmin, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymin, zmin, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymax, zmin, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymax, zmin, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymin, zmax, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymin, zmax, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmin, ymax, zmax, w) != 0) {
				return true;
			}
			else if(map.getBlock(xmax, ymax, zmax, w) != 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public static boolean forceSneak() {
		int xmin = (int)(posX + 0.3);
		int xmax = (int)(posX + 0.7);
		int zmin = (int)(posZ - 0.2);
		int zmax = (int)(posZ + 0.2);
		int wmin = (int)(posW + 0.3);
		int wmax = (int)(posW + 0.7);
		World map = MainGameLoop.map;
		
		int y = (int)((MainGameLoop.height * 8 - 1.5) - posY);
		if(map.getBlock(xmin, y, zmin, wmin) != 0) {
			return true;
		}
		else if(map.getBlock(xmax, y, zmin, wmin) != 0) {
			return true;
		}
		else if(map.getBlock(xmin, y, zmax, wmin) != 0) {
			return true;
		}
		else if(map.getBlock(xmax, y, zmax, wmin) != 0) {
			return true;
		}
		else if(map.getBlock(xmin, y, zmin, wmax) != 0) {
			return true;
		}
		else if(map.getBlock(xmax, y, zmin, wmax) != 0) {
			return true;
		}
		else if(map.getBlock(xmin, y, zmax, wmax) != 0) {
			return true;
		}
		else if(map.getBlock(xmax, y, zmax, wmax) != 0) {
			return true;
		}
		else {
			return false;
		}
	}
}