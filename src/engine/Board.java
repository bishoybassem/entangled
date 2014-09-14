package engine;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Board {

	protected Tile[][] tiles;
	protected int openings;
	protected int sides;
	protected int startPos;
	protected int rows;
	protected int columns;
	private Tile switchTile;
	
	private int cRow;
	private int cColumn;
	private int cInput;
	
	private Player[] players;
	private int cPlayer;
	
	private int winner;
	private int test;
	
	public Board(int rows, int columns, int sides, int openings, Player[] players) {
		this.rows = rows;
		this.columns = columns;
		this.openings = openings;
		this.players = players;
		this.sides = sides;
		winner = -2;
		test = -2;
		startPos = (rows * columns - 1) / 2;
		initializeBoard();
		setCurrentTile();
	}
	
	protected abstract void initializeBoard();
	
	public boolean isStartTile(int row, int column) {
		return row == startPos / columns && column == startPos % columns;
	}
	
	public boolean isCurrentTile(int row, int column) {
		return row == cRow && column == cColumn;
	}
	
	private void setCurrentTile() {
		int[] position = getStartTile(cPlayer);
		Tile current = tiles[position[0]][position[1]];
		while (current.isShowed()) {
			position[2] = current.getOtherPoint(position[2]);
			getNextTile(position);
			current = tiles[position[0]][position[1]];
		}
		cRow = position[0];
		cColumn = position[1];
		cInput = position[2];
		tiles[cRow][cColumn].setShowed();
		do {
			switchTile = new Tile(openings, sides);
		} while(switchTile.equals(tiles[cRow][cColumn]));
		switchTile.setShowed();
	}
	
	private int[] getStartTile(int player) {
		int[] position = {startPos / columns,
				startPos % columns, player * openings};
		getNextTile(position);
		return position;
	}
	
	protected abstract void getNextTile(int[] position);

	protected abstract int[] getNextTile(int row, int column, int direction);
	
	private void selectNextPlayer() {
		int i = 0;
		do {
			cPlayer = (cPlayer + 1) % players.length;
			i++;
		} while (players[cPlayer].isGameOver() && i != players.length + 1);
		if (i == players.length + 1) {
			cPlayer = -1;
			cRow = -1;
			cColumn = -1;
			Player[] copy = players.clone();
			Arrays.sort(copy);
			if (copy.length != 1) {
				if (copy[0].getScore() == copy[1].getScore()) {
					winner = -1;
				} else {
					winner = copy[0].getNumber();
				}
			}
		} else {
			setCurrentTile();
		}
	}
	
	public boolean isGameOver() {
		return cPlayer == -1;
	}
	
	public void rotateTile(int angle) {
		if (!isGameOver()) {
			tiles[cRow][cColumn].rotate(angle);
		}
	}
	
	public void switchTile() {
		if (!isGameOver()) {
			Tile temp = tiles[cRow][cColumn];
			tiles[cRow][cColumn] = switchTile;
			switchTile = temp;
		}
	}
	
	public void fixTile() {
		if (!isGameOver()) {
			continueAllLinks();
			selectNextPlayer();
		}
	}
	
	private void continueAllLinks() {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		int[] position;
		int oppSide;
		for (int i = 0; i < sides; i++) {
			position = getNextTile(cRow, cColumn, i);
			oppSide = (i + sides / 2) % sides;
			if (isStartTile(position[0], position[1])) {
				int inPoint = Tile.getOppositePoint(oppSide * openings, openings, sides);
				moves.add(new int[]{inPoint, oppSide});
			} else if (tiles[position[0]][position[1]] != null 
					&& tiles[position[0]][position[1]].isShowed()) {
				moves.addAll(tiles[position[0]][position[1]].getOutput(oppSide));
			}
		}
		int[] move;
		for (int i = 0; i < moves.size(); i++) {
			move = moves.get(i);
			if (move[1] < players.length && !players[move[1]].isGameOver()) {
				continueLink(cRow, cColumn, move[0], move[1]);
			}
		}
	}
	
	private void continueLink(int row, int column, int inPoint, int player) {
		int length = 0;
		int[] position = {row, column, inPoint};
		Tile t1;
		Tile t2;
		do {
			t1 = tiles[position[0]][position[1]];
			position[2] = t1.fix(position[2], player);
			length++;
			getNextTile(position);
			t2 = tiles[position[0]][position[1]];
			if (t2 == null || t2.isWall()) {
				players[player].incrementScore(length);
				players[player].setGameOver();
				break;
			} else if (t2.isShowed()) {
				int by = t2.getSelector(position[2]);
				if (by >= 0) {
					players[player].incrementScore(length - 1);
					players[player].setGameOver();
					players[by].setGameOver();
					int p2 = Tile.getOppositePoint(position[2], openings, sides);
					int p1 = t1.getOtherPoint(p2);
					t1.getLink(p1).setClash(new int[] {p1, player, p2, by});
					break;
				}
			} else {
				players[player].incrementScore(length);
				break;
			}
		} while (true);
	}
	
	public int[] getBestMove() {
		int[][] moves = new int[sides * 2][];
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < sides; i++) {
				int[] a = tryLink(cRow, cColumn, cInput);
				moves[i + sides * j] = new int[]{a[0], a[1], a[2], i, j};
				tiles[cRow][cColumn].rotate(1);
				test--;
			}
			switchTile();
		}
		boolean[] b = new boolean[5];
		for (int i = 0; i < moves.length - 1; i++) {
			for (int j = 0; j < moves.length - i - 1; j++) {
				b[0] = moves[j][0] < moves[j + 1][0];
				b[1] = moves[j][0] == moves[j + 1][0];
				b[2] = moves[j][1] < moves[j + 1][1];
				b[3] = moves[j][1] == moves[j + 1][1];
				b[4] = moves[j][2] < moves[j + 1][2];
				if (b[0] || (b[1] && b[2]) || (b[1] && b[3] && b[4])) {
					int[] temp = moves[j];
					moves[j] = moves[j + 1];
					moves[j + 1] = temp;
				}
			}
		}
		return new int[]{moves[0][4], moves[0][3]};
	}
	
	private int[] tryLink(int row, int column, int inPoint) {
		int length = 0;
		int[] position = {row, column, inPoint};
		Tile t1;
		Tile t2;
		do {
			t1 = tiles[position[0]][position[1]];
			position[2] = t1.fix(position[2], test);
			length++;
			getNextTile(position);
			t2 = tiles[position[0]][position[1]];
			if (t2 == null || t2.isWall()) {
				return new int[]{-1, length, -1};
			} else if (t2.isShowed()) {
				int by = t2.getSelector(position[2]);
				if (by > -1 || by == test) {
					return new int[]{-1, length, -1};
				}
			} else {
				int count = 0;
				int a = 0;
				int b = tiles.length;
				int c = 0;
				int d = tiles[0].length;
				switch (position[2] / t1.getOpenings()) {
					case 2: b = position[0] + 1; break;
					case 0: a = position[0]; break;
					case 1: d = position[1] + 1; break;
					case 3: c = position[1]; break;
					default: break;
				}
				for (int i = a; i < b; i++) {
					for (int j = c; j < d; j++) {
						if (tiles[i][j] != null && !tiles[i][j].isShowed()) {
							count++;
						}
					}
				}
				return new int[]{1, length, count};
			}
		} while (true);
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public Player[] getPlayers() {
		return players;
	}

	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}

	public int getOpenings() {
		return openings;
	}

	public int getCurrentRow() {
		return cRow;
	}

	public int getCurrentColumn() {
		return cColumn;
	}
	
	public int getStartRow() {
		return startPos / columns;
	}

	public int getStartColumn() {
		return startPos % columns;
	}

	public int getCurrentPlayer() {
		return cPlayer;
	}
	
	public Tile getCurrentTile() {
		return tiles[cRow][cColumn];
	}

	public Tile getSwitchTile() {
		return switchTile;
	}

	public int getSides() {
		return sides;
	}
	
	public int getWinner() {
		return winner;
	}
	
}
