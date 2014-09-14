package engine;


public class HexagonBoard extends Board {

	public HexagonBoard(int rows, int columns, int openings, Player[] players) {
		super(rows, columns, 6, openings, players);
	}

	protected void initializeBoard() {
		tiles = new Tile[rows][columns];
		int mid = columns / 2;
		int y = (((rows - 1) % 4 == 0)? 0 : 1);
		int x = 0;
		Tile wall = new Tile();
		tiles[0][mid] = wall;
		tiles[tiles.length - 1][mid] = wall;
		for (int i = y; i < rows / 4 + y; i++) {
			tiles[i][mid - 2 * i + y] = wall;
			tiles[i][mid - 2 * i - 1 + y] = wall;
			tiles[i][mid + 2 * i - y] = wall;
			tiles[i][mid + 2 * i + 1 - y] = wall;
			for (int j = mid - 2 * i + y + 1; j < mid + 2 * i - y; j++) {
				tiles[i][j] = new Tile(openings, 6);
			}
		}
		for (int i = rows / 4 + y; i < rows - rows / 4 - 1; i++) {
			tiles[i][0] = wall;
			tiles[i][columns - 1] = wall;
			for (int j = 1; j < columns - 1; j++) {
				if (!isStartTile(i, j)) {
					tiles[i][j] = new Tile(openings, 6);
				}
			}
		}
		for (int i = rows - rows / 4 - 1; i < rows + y - 1; i++) {
			tiles[i][x] = wall;
			tiles[i][x + 1] = wall;
			tiles[i][columns - x - 1] = wall;
			tiles[i][columns - x - 2] = wall;
			for (int j = x + 2; j < columns - x - 2; j++) {
				tiles[i][j] = new Tile(openings, 6);
			}
			x += 2;
		}
	}
		
	protected void getNextTile(int[] position) {
		switch(position[2] / openings) {
			case 0: position[0]--; break;
			case 1: position[0] = position[0] + ((position[1] % 2 == 0)? -1 : 0); position[1]++; break;
			case 2: position[0] = position[0] + ((position[1] % 2 == 0)? 0 : 1); position[1]++; break;
			case 3: position[0]++; break;
			case 4: position[0] = position[0] + ((position[1] % 2 == 0)? 0 : 1); position[1]--; break;
			case 5: position[0] = position[0] + ((position[1] % 2 == 0)? -1 : 0); position[1]--;
		}
		position[2] = Tile.getOppositePoint(position[2], openings, sides);
	}

	protected int[] getNextTile(int row, int column, int direction) {
		switch (direction) {
			case 0: row--; break;
			case 1: row = row + ((column % 2 == 0)? -1 : 0); column++; break;
			case 2: row = row + ((column % 2 == 0)? 0 : 1); column++; break;
			case 3: row++; break;
			case 4: row = row + ((column % 2 == 0)? 0 : 1); column--; break;
			case 5: row = row + ((column % 2 == 0)? -1 : 0); column--;
		}
		return new int[]{row, column};
	}

}
