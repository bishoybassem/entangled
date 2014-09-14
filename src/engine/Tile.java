package engine;
import java.util.ArrayList;
import java.util.Random;

public class Tile {
	
	public ArrayList<Link> links;
	private int openings;
	private int sides;
	private boolean wall;
	private boolean showed;
	
	public Tile() {
		wall = true;
	}
	
	public Tile(int openings, int sides) {
		this.openings = openings;
		this.sides = sides;
		links = new ArrayList<Link>();
		for (int i = 0; i < openings * sides / 2; i++) {
			links.add(new Link());
		}
		Random gen = new Random();
		Link link;
		int index;
		for (int i = 1; i < openings * sides; i++) {
			do {
				link = links.get(gen.nextInt(openings * sides / 2));
				index = gen.nextInt(2);
			} while (link.getPoint(index) != 0);
			link.setPoint(index, i);
		}
	}
	
	public void rotate(int angle) {
		for (int i = 0; i < links.size(); i++) {
			links.get(i).rotate(angle, openings, sides);
		}
	}
	
	public int fix(int point, int player) {
		Link l = getLink(point);
		l.setSelector(player);
		return l.getOtherPoint(point);
	}
	
	public Link getLink(int point) {
		for (int i = 0; i < links.size(); i++) {
			if (links.get(i).getOtherPoint(point) != -1) {
				return links.get(i);
			}
		}
		return null;
	}
	
	public int getSelector(int point) {
		return getLink(point).getSelector();
	}
	
	public int getOtherPoint(int point) {
		return getLink(point).getOtherPoint(point);
	}
	
	public ArrayList<int[]> getOutput(int side) {
		ArrayList<int[]> array = new ArrayList<int[]>();
		int player;
		int point;
		for (int i = 0; i < openings; i++) {
			player = getSelector(side * openings + i);
			if (player > -1) {
				point = getOppositePoint(side * openings + i, openings, sides);
				array.add(new int[]{point, player});
			}
		}
		return array;
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}

	public int getOpenings() {
		return openings;
	}

	public boolean isShowed() {
		return showed;
	}
	
	public void setShowed() {
		showed = true;
	}
	
	public static int getOppositePoint(int point, int openings, int sides) {
		return (point + sides / 2 + (sides / 2 + 1) * (openings - 1) - 2 * (point % openings)) % (sides * openings);
	}

	private boolean contains(Link link) {
		for (int i = 0; i < links.size(); i++) {
			if (link.equals(links.get(i))) {
				return true;
			}
		}
		return false; 
	}
	
	public boolean equals(Object o) {
		Tile t = (Tile) o;
		if (t.openings != openings) {
			return false;
		}
		boolean equals;
		int angle = 0;
		for (int j = 0; j < links.size(); j++) {
			equals = true;
			for (int i = 0; i < t.links.size(); i++) {
				if (!contains(t.links.get(i))) {
					equals = false;
				}
			}
			if (equals) {
				rotate(-angle);
				return true;
			}
			rotate(1);
			angle++;
		}
		return false;
	}

	public boolean isSquare() {
		return sides == 4;
	}
	
	public boolean isWall() {
		return wall;
	}

}
