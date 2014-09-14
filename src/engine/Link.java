package engine;

public class Link {

	private int[] points;
	private int selector;
	private int[] clash;

	public Link() {
		points = new int[2];
		selector = -1;
	}
	
	public Link(int point1, int point2) {
		points = new int[]{point1, point2};
		selector = -1;
	}
	
	public int getPoint(int index) {
		return points[index];
	}
	
	public int[] getPoints() {
		return points;
	}
	
	public void setPoint(int index, int value) {
		points[index] = value;
	}

	public int getOtherPoint(int point) {
		if (point == points[0]) {
			return points[1];
		}
		if (point == points[1]) {
			return points[0];
		}
		return -1;
	}

	public void rotate(int angle, int openings, int sides) {
		int x = angle * openings;
		for (int i = 0; i < 2; i++) {
			points[i] += x;
			if (points[i] > sides * openings - 1) {
				points[i] %= (sides * openings);
			}
			while (points[i] < 0) {
				points[i] += (sides * openings);
			}
		}
	}

	public int getSelector() {
		return selector;
	}

	public void setSelector(int player) {
		selector = player;
	}
	
	public boolean isClash() {
		return clash != null;
	}

	public int[] getClash() {
		return clash;
	}

	public void setClash(int[] clash) {
		this.clash = clash;
	}

	public boolean equals(Object o) {
		Link l = (Link) o;
		return (l.points[0] == points[0] && l.points[1] == points[1])
				|| (l.points[0] == points[1] && l.points[1] == points[0]);
	}

}
