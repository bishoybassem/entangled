package engine;

public class Player implements Comparable<Player> {
	
	private int score;
	private boolean gameOver;
	private int number;
	private boolean computer;
	
	public Player(int number, boolean computer) {
		this.number = number;
		this.computer = computer;
	}

	public int compareTo(Player player) {
		if (score == player.score) {
			return 0;
		} else if (score > player.score) {
			return -1;
		} else {
			return 1;
		}
	}

	public int getScore() {
		return score;
	}
	
	public void incrementScore(int length) {
		score += (length * (length + 1) / 2);
	}

	public boolean isComputer() {
		return computer;
	}

	public boolean isGameOver() {
		return gameOver;
	}
	
	public void setGameOver() {
		gameOver = true;
	}

	public int getNumber() {
		return number;
	}
	
}
