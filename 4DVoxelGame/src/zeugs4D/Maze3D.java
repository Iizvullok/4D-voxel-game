package zeugs4D;

public class Maze3D{
    
    public static int [] getNewPosition (boolean [][][] maze) {
    	int size = maze.length / 2;
    	while(true) {
    		int x = (int)(Math.random() * size) * 2 + 1;
        	int y = (int)(Math.random() * size) * 2 + 1;
        	int z = (int)(Math.random() * size) * 2 + 1;
        	if(maze[x][y][z] && anyLegalMoves(maze, x, y, z)) {
        		return new int [] {x, y, z};
        	}
    	}
    }
    
    public static boolean [][][] generate(int size){
    	boolean [][][] maze = new boolean[size][size][size];
    	makePath(maze, 1, 1, 1);
    	while(canMakeMorePaths(maze)) {
    		int [] pos = getNewPosition(maze);
	    	makePath(maze, pos[0], pos[1], pos[2]);
    	}
    	boolean [][][] maze2 = new boolean[size + 1][size + 1][size + 1];
    	for(int i = 0; i < size; i++) {
    		for(int j = 0; j < size; j++) {
    			for(int k = 0; k < size; k++) {
        			maze2[i][j][k] = maze[i][j][k];
            	}
        	}
    	}
    	maze2[0][1][1] = true;
    	maze2[size][size - 1][size - 1] = true;
    	return maze2;
    }
    
    public static boolean canMakeMorePaths(boolean [][][] maze) {
    	int size = maze.length / 2;
    	for(int i = 0; i < size; i++) {
    		for(int j = 0; j < size; j++) {
    			for(int k = 0; k < size; k++) {
            		int x = i * 2 + 1;
            		int y = j * 2 + 1;
            		int z = k * 2 + 1;
            		if(anyLegalMoves(maze, x, y, z)) {
            			return true;
            		}
            	}
        	}
    	}
    	return false;
    }
    
    public static boolean [][][] makePath(boolean [][][] maze, int i, int j, int k){
    	maze[i][j][k] = true;
    	while(anyLegalMoves(maze, i, j, k)) {
    		boolean legalMoveFound = false;
    		int m = 0;
    		while(!legalMoveFound) {
    			m = (int)(Math.random() * 6);
    			legalMoveFound = legalMove(maze, i, j, k, m);
    		}
    		if(m == 0) {
    			maze[i + 1][j][k] = true;
    			maze[i + 2][j][k] = true;
    			i = i + 2;
    		}
    		else if(m == 1) {
    			maze[i - 1][j][k] = true;
    			maze[i - 2][j][k] = true;
    			i = i - 2;
    		}
    		else if(m == 2) {
    			maze[i][j + 1][k] = true;
    			maze[i][j + 2][k] = true;
    			j = j + 2;
    		}
    		else if(m == 3){
    			maze[i][j - 1][k] = true;
    			maze[i][j - 2][k] = true;
    			j = j - 2;
    		}
    		else if(m == 4) {
    			maze[i][j][k + 1] = true;
    			maze[i][j][k + 2] = true;
    			k = k + 2;
    		}
    		else{
    			maze[i][j][k - 1] = true;
    			maze[i][j][k - 2] = true;
    			k = k - 2;
    		}
    	}
    	
    	return maze;
    }
    
    public static boolean anyLegalMoves(boolean [][][] maze, int i, int j, int k) {
    	return legalMove(maze, i, j, k, 0) || legalMove(maze, i, j, k, 1) || legalMove(maze, i, j, k, 2) || legalMove(maze, i, j, k, 3) || legalMove(maze, i, j, k, 4) || legalMove(maze, i, j, k, 5);
    }
    
    public static boolean inArray(boolean [][][] maze, int i, int j, int k) {
    	return (i > 0 && j > 0 && k > 0 && i < maze.length && j < maze.length && k < maze.length);
    }
    
    public static boolean legalMove(boolean [][][] maze, int i, int j, int k, int m) {
    	if(m == 0) {
    		if(inArray(maze, i + 2, j, k)) {
    			if(!maze [i + 2][j][k]) {
        			return true;
        		}
    			else {
    				return false;
    			}
    		}
    		else {
    			return false;
    		}
    	}
    	else if(m == 1) {
    		if(inArray(maze, i - 2, j, k)) {
    			if(!maze [i - 2][j][k]) {
        			return true;
        		}
    			else {
    				return false;
    			}
    		}
    		else {
    			return false;
    		}
    	}
    	else if(m == 2) {
    		if(inArray(maze, i, j + 2, k)) {
    			if(!maze [i][j + 2][k]) {
        			return true;
        		}
    			else {
    				return false;
    			}
    		}
    		else {
    			return false;
    		}
    	}
    	else if(m == 3){
    		if(inArray(maze, i, j - 2, k)) {
    			if(!maze [i][j - 2][k]) {
        			return true;
        		}
    			else {
    				return false;
    			}
    		}
    		else {
    			return false;
    		}
    	}
    	else if(m == 4) {
    		if(inArray(maze, i, j, k + 2)) {
    			if(!maze [i][j][k + 2]) {
        			return true;
        		}
    			else {
    				return false;
    			}
    		}
    		else {
    			return false;
    		}
    	}
    	else{
    		if(inArray(maze, i, j, k - 2)) {
    			if(!maze [i][j][k - 2]) {
        			return true;
        		}
    			else {
    				return false;
    			}
    		}
    		else {
    			return false;
    		}
    	}
    }
}