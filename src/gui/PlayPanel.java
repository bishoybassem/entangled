package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

import engine.Board;
import engine.Link;
import engine.Tile;

@SuppressWarnings("serial")
public abstract class PlayPanel extends JPanel {

	protected Board board;
	private Tile[][] tiles;
	protected int columns;
	protected int rows;
	protected int panelWidth;
	protected int panelHeight;
	
	protected double width;
	protected double height;
	protected int widthA;
	protected int heightA;
	
	private BufferedImage walls;
	private BufferedImage winner;
	protected BufferedImage currentTile;
	private BufferedImage showedTiles;
	
	private LevelPanel levelPanel;
	
	private Timer resultTimer;
	private Timer animator;
	
	private boolean resultDisplayed;
	private boolean animating;
	
	private int angle;
	private int cAngle;
	protected int nSteps;
	
	protected Shape clip;
	
	public PlayPanel(Board board, double height, LevelPanel lp) {
		this.board = board;
		this.height = height;
		levelPanel = lp;
		tiles = board.getTiles();
		columns = board.getColumns();
		rows = board.getRows();
		
		initializeFields();
		widthA = (int) (width + 0.5);
		heightA = (int) (height + 0.5);
		
		setTimers();
		drawWalls();
		drawTiles();
		drawCurrentTile();
		
		setOpaque(false);
		addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				levelPanel.fixTile();
			}
			
		});
	}
	
	protected void setTimers() {
		resultTimer = new Timer(2000, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!resultDisplayed) {
					resultDisplayed = true;
					repaint();
				} else {
					winner = null;
					repaint();
					resultTimer.stop();
				}
			}
			
		});
		
		animator = new Timer(9, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (cAngle == angle * nSteps) {	
					board.rotateTile(angle);
					drawCurrentTile();
					animator.stop();
					animating = false;
					cAngle = 0;
					if (board.getPlayers()[board.getCurrentPlayer()].isComputer()) {
						levelPanel.fixTile();
					}
				} else {
					if (angle > 0) {
						cAngle++;
					} else {
						cAngle--;
					}
				}
				repaint();
			}
			
		});
	}
	
	protected void drawWalls() {
		walls = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) walls.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		double[] p;
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j] != null) {
					p = getCoordinates(i, j);
					g2d.translate(p[0], p[1]);
					if (tiles[i][j].isWall()) {
						drawWall(g2d);
					} else {
						drawBackground(g2d);
					}
					g2d.translate(-p[0], -p[1]);
				}
			}
		}
	}
	
	protected void drawTiles() {
		showedTiles = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) showedTiles.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		double[] p;
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j] != null && !board.isCurrentTile(i, j) && tiles[i][j].isShowed()) {
					p = getCoordinates(i, j);
					drawTile(g2d, p[0], p[1], tiles[i][j], width, clip);	
				}
			}
		}
		p = getCoordinates(board.getStartRow(), board.getStartRow());
		g2d.translate(p[0], p[1]);
		drawStartTile(g2d);
		if (!board.isGameOver()) {
			g2d.setColor(getColor(board.getCurrentPlayer()));
			g2d.setStroke(new BasicStroke((float) ((board.getSides() == 4? 2 : 1.7) * width / 36)));
			Point2D p1 = getPointCoordinates(board.getCurrentPlayer() * board.getOpenings(), board.getOpenings(), width, board.getSides() == 4);
			Point2D p2 = getPerpendicularCoordinates(board.getCurrentPlayer() * board.getOpenings(), board.getOpenings(), width, board.getSides() == 4);
			g2d.draw(new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY()));
		}
		g2d.translate(-p[0], -p[1]);
	}
	
	protected void drawCurrentTile() {
		currentTile = new BufferedImage(widthA, heightA, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) currentTile.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		if (!board.isGameOver()) {
			drawCurrrentFrame(g2d);
		}
		drawTile(g2d, 0, 0, board.getCurrentTile(), width, clip);
	}
	
	public static void drawTile(Graphics2D g2d, double x, double y, Tile tile, double width, Shape clip) {
		g2d.translate(x, y);
		g2d.setClip(clip);
		for (int i = 0; i < tile.getLinks().size(); i++) {
			drawLink(g2d, tile.getLinks().get(i), tile.getOpenings(), width, tile.isSquare());
		}
		g2d.translate(-x, -y);
		g2d.setClip(null);
	}
	
	private static void drawLink(Graphics2D g2d, Link link, int openings, double width, boolean square) {
		double f = (square)? 4 : 3.4;
		float[] stroke = {(float) (f * width / 36), (float) (f * width / 72)};
		int[] points = link.getPoints();
		Paint[] paint = {Color.WHITE, Color.DARK_GRAY};
		if (link.isClash()) {
			int[] x = link.getClash();
			paint[0] = Color.DARK_GRAY;
			paint[1] = new GradientPaint(getPointCoordinates(x[0], openings, width, square), 
					getColor(x[1]), getPointCoordinates(x[2], openings, width, square), 
					getColor(x[3]));
		} else if (link.getSelector() > -1) {
			paint[0] = Color.DARK_GRAY;
			paint[1] = getColor(link.getSelector());
		}
		for (int i = 0; i < 2; i++) {
			g2d.setPaint(paint[i]);
			g2d.setStroke(new BasicStroke(stroke[i]));
			Point2D p1 = getPointCoordinates(points[0], openings, width, square);
			Point2D p2 = getPerpendicularCoordinates(points[0], openings, width, square);
			Point2D p3 = getPerpendicularCoordinates(points[1], openings, width, square);
			Point2D p4 = getPointCoordinates(points[1], openings, width, square);
			g2d.draw(new CubicCurve2D.Double(p1.getX(), p1.getY(), p2.getX(), 
					p2.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY()));
		}
	}
	
	public static Point2D getPointCoordinates(int point, int openings, double width, boolean square) {
		int side = point / openings;
		double d = (point % openings + 1) * width / (openings + 1);
		if (square) {
			switch (side) {
				case 0 : return new Point2D.Double(d, 0);
				case 1 : return new Point2D.Double(width, d);
				case 2 : return new Point2D.Double(width - d, width);
				case 3 : return new Point2D.Double(0, width - d);
				default : return null;
			}
		} else {
			d /= 2;
			double h = width * Math.cos(Math.PI / 6);
			switch (side) {
				case 0 : return new Point2D.Double(width / 4 + d, 0);
				case 1 : return new Point2D.Double(0.75 * width + d * Math.cos(Math.PI / 3), d * Math.sin(Math.PI / 3));
				case 2 : return new Point2D.Double(width - d * Math.cos(Math.PI / 3), d * Math.sin(Math.PI / 3) + h / 2);
				case 3 : return new Point2D.Double(width - d - width / 4, h);
				case 4 : return new Point2D.Double(width / 4 - d * Math.cos(Math.PI / 3), h - d * Math.sin(Math.PI / 3));
				case 5 : return new Point2D.Double(d * Math.sin(Math.PI / 6), h / 2 - d * Math.cos(Math.PI / 6));
				default : return null;
			}
		}
		
	}
	
	public static Point2D getPerpendicularCoordinates(int point, int openings, double width, boolean sq) {
		int side = point / openings;
		Point2D p = getPointCoordinates(point, openings, width, sq);
		double length;
		if (sq) {
			length = width / (openings + 1) + (openings - 1) * (width / 36);
			switch (side) {
				case 0 : p.setLocation(p.getX(), length); break;
				case 1 : p.setLocation(width - length, p.getY()); break;
				case 2 : p.setLocation(p.getX(), width - length); break;
				case 3 : p.setLocation(length, p.getY()); break;
			}
		} else {
			length = width / 2 / (openings + 1) * Math.tan(Math.PI / 3);
			double h = width * Math.cos(Math.PI / 6);
			double hc60 = length * Math.cos(Math.PI / 3);
			double hs60 = length * Math.sin(Math.PI / 3);
			switch (side) {
				case 0 : p.setLocation(p.getX(), length); break;
				case 1 : p.setLocation(p.getX() - hs60, p.getY() + hc60); break;
				case 2 : p.setLocation(p.getX() - hs60, p.getY() - hc60); break;
				case 3 : p.setLocation(p.getX(), h - length); break;
				case 4 : p.setLocation(p.getX() + hs60, p.getY() - hc60); break;
				case 5 : p.setLocation(p.getX() + hs60, p.getY() + hc60); break;
			}
		}
		return p;
	}
	
	public static Color getColor(int player) {
		switch (player) {
			case 0 : return new Color(254, 64, 70);
			case 1 : return new Color(60, 248, 241);
			case 2 : return new Color(205, 101, 245);
			case 3 : return new Color(1, 254, 95);
			case 4 : return new Color(248, 159, 37);
			case 5 : return new Color(100, 100, 255);
			default : return Color.BLACK;
		}
	}
	
	public void fixCurrentTile() {
		if (board.isGameOver()) {
			return;
		}
		board.fixTile();
		drawTiles();
		if (board.isGameOver()) {
			currentTile = null;
		} else {
			drawCurrentTile();
		}
		repaint();
	}
	
	public void rotateCurrentTile(int angle) {
		if (board.isGameOver()) {
			return;
		}
		this.angle = angle;
		animating = true;
		animator.start();
	}
	
	public void displayResult() {
		if (resultDisplayed) {
			return;
		}
		int player = board.getWinner();
		if (player == -2) {
			return;
		}
		winner = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) winner.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(new Font("Comic sans ms", Font.BOLD, 25));
		
		FontMetrics fm = g2d.getFontMetrics();
		int x = (winner.getWidth() - 250) / 2;
		int y = (winner.getHeight() - 70) / 2;
		g2d.setPaint(new GradientPaint(x, y, new Color(210, 210, 210), x, y + 70, new Color(250, 250, 250)));
		g2d.fillRect(x, y, 250, 70);
		g2d.setPaint(new GradientPaint(x, y, new Color(215, 168, 134), x + 250, y + 70, new Color(116, 72, 39)));
		g2d.setStroke(new BasicStroke(4));
		g2d.drawRect(x + 2, y + 2, 246, 66);
		g2d.setStroke(new BasicStroke(1));
		if (board.getWinner() != -1) {
			x = (winner.getWidth() - fm.stringWidth("Player " + (player + 1) + " Wins")) / 2;
			y = (winner.getHeight() + fm.getAscent()) / 2 - 5;
			g2d.setColor(getColor(player));
			g2d.drawString("Player " + (player + 1), x, y);
			x += fm.stringWidth("Player " + (player + 1));
			g2d.setColor(Color.BLACK);
			g2d.drawString(" Wins", x, y);
		} else {
			x = (winner.getWidth() - fm.stringWidth("Tie")) / 2;
			y = (winner.getHeight() + fm.getAscent()) / 2 - 5;
			g2d.setColor(Color.BLACK);
			g2d.drawString("Tie", x, y);
		}	
		resultTimer.start();
	}
	
	public boolean isResultDisplayed() {
		return resultDisplayed;
	}
	
	public boolean isAnimating() {
		return animating;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(panelWidth + 30, panelHeight + 15);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(15, 0);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.drawImage(walls, 0, 0, null);
		g2d.drawImage(showedTiles, 0, 0, null);
		double[] p = getCoordinates(board.getCurrentRow(), board.getCurrentColumn());
		g2d.translate(p[0], p[1]);
		g2d.rotate(cAngle * Math.PI / (board.getSides() / 2 * nSteps), width / 2, height / 2);
		g2d.drawImage(currentTile, 0, 0, null);
		g2d.rotate(-cAngle * Math.PI / (board.getSides() / 2 * nSteps), width / 2, height / 2);
		g2d.translate(-p[0], -p[1]);
		g2d.drawImage(winner, 0, 0, null);
	}
	
	protected abstract void initializeFields();
	
	protected abstract void drawWall(Graphics2D g2d);
	
	protected abstract void drawBackground(Graphics2D g2d);
		
	protected abstract void drawStartTile(Graphics2D g2d);
	
	protected abstract void drawCurrrentFrame(Graphics2D g2d);
	
	protected abstract double[] getCoordinates(int row, int column);
	
	public void stop() {
		animator.stop();
		resultTimer.stop();
		levelPanel = null;
	}
	
}
