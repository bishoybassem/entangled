package engine;

import java.util.Arrays;


public class SquareBoard extends Board {
	
	public SquareBoard(int rows, int columns, int openings, Player[] players) {
		super(rows, columns, 4, openings, players);
	}
	
	protected void initializeBoard() {
		tiles = new Tile[rows][columns];
		Tile wall = new Tile();
		Arrays.fill(tiles[0], wall);
		Arrays.fill(tiles[tiles.length - 1], wall);
		for (int i = 1; i < tiles.length - 1; i++) {
			tiles[i][0] = wall;
			tiles[i][tiles[0].length - 1] = wall;
			for (int j = 1; j < tiles[0].length - 1; j++) {
				if (!isStartTile(i, j)) {
					tiles[i][j] = new Tile(openings, 4);
				}
			}
		}
	}
	
	protected void getNextTile(int[] position) {
		switch(position[2] / openings) {
			case 0: position[0]--; break;
			case 1: position[1]++; break;
			case 2: position[0]++; break;
			case 3: position[1]--; break;
		}
		position[2] = Tile.getOppositePoint(position[2], openings, sides);
	}

	protected int[] getNextTile(int row, int column, int direction) {
		switch (direction) {
			case 0: row--; break;
			case 1: column++; break;
			case 2: row++; break;
			case 3: column--; break;
		}
		return new int[]{row, column};
	}
		
}
