package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Path2D;

import engine.HexagonBoard;


@SuppressWarnings("serial")
public class HexagonPlayPanel extends PlayPanel {

	public HexagonPlayPanel(HexagonBoard board, double height, LevelPanel levelPanel) {
		super(board, height, levelPanel);
	}
	
	protected void initializeFields() {
		width = height / 2.0 / Math.sin(Math.PI / 3) + height / Math.tan(Math.PI / 3);
		panelWidth = (int) (columns * width + height / 2.0 / Math.tan(Math.PI / 3) * (1 - columns) + 0.5);
		panelHeight = (int) (rows * height + 0.5);
		clip = generateHexClip(width);
		nSteps = 25;
	}
	
	protected void drawWall(Graphics2D g2d) {
		fillHexagon(g2d, new Color(200, 137, 91), width);
		drawHexagon(g2d, new GradientPaint(0, 0, new Color(215, 168, 134), widthA, heightA, new Color(116, 72, 39)), width, 2);
	}
	
	public void drawBackground(Graphics2D g2d) {
		fillHexagon(g2d, new Color(228, 228, 228), width);
		drawHexagon(g2d, new GradientPaint(0, 0, new Color(250, 250, 250), widthA, heightA, new Color(184, 184, 184)), width, 2);
	}
		
	protected void drawStartTile(Graphics2D g2d) {
		fillHexagon(g2d, Color.DARK_GRAY, width);
		double stroke = 5 * width / 36;
		double hs = (width - 2 * stroke) * Math.cos(Math.PI / 6);
		g2d.translate(stroke, (height - hs) / 2);
		fillHexagon(g2d, getColor(board.getCurrentPlayer()), width - 2 * stroke);
		g2d.translate(-stroke, (hs - height) / 2);
	}
		
	protected void drawCurrrentFrame(Graphics2D g2d) {
		float x = (float) (width / 18);
		g2d.setColor(getColor(board.getCurrentPlayer()));
		drawHexagon(g2d, getColor(board.getCurrentPlayer()), width, x);
	}
	
	protected double[] getCoordinates(int row, int column) {
		double x = column * (width - height / 2.0 / Math.tan(Math.PI / 3));
		double y = row * height - (((rows - 1) % 4 == 0)? 0 : height / 2.0) + column % 2 * height / 2.0;
		return new double[]{x, y};
	}
	
	
	public static void drawHexagon(Graphics2D g2d, Paint p, double width, float stroke) {
		double sideLength = (width - stroke) / 2.0;
		Path2D hex = new Path2D.Double();
		hex.moveTo(sideLength / 2.0 + stroke / 2.0, stroke / 2.0);
		hex.lineTo(3 * sideLength / 2.0 + stroke / 2.0, stroke / 2.0);
		hex.lineTo(2 * sideLength + stroke / 2.0, sideLength * Math.cos(Math.PI / 6) + stroke / 2.0);
		hex.lineTo(3 * sideLength / 2.0 + stroke / 2.0, 2 * sideLength * Math.cos(Math.PI / 6) + stroke / 2.0);
		hex.lineTo(sideLength / 2.0 + stroke / 2.0, 2 * sideLength * Math.cos(Math.PI / 6) + stroke / 2.0);
		hex.lineTo(stroke / 2.0, sideLength * Math.cos(Math.PI / 6) + stroke / 2.0);
		hex.lineTo(sideLength / 2.0 + stroke / 2.0, stroke / 2.0);
		g2d.setStroke(new BasicStroke(stroke));
		g2d.setPaint(p);
		g2d.draw(hex);
	}
		
	public static void fillHexagon(Graphics2D g2d, Paint p, double width) {
		g2d.setPaint(p);
		g2d.fill(generateHexClip(width));
	}
		
	public static Path2D generateHexClip(double width) {
		double sideLength = width / 2.0;
		Path2D hex = new Path2D.Double();
		hex.moveTo(sideLength / 2.0, 0);
		hex.lineTo(3 * sideLength / 2.0 , 0);
		hex.lineTo(2 * sideLength, sideLength * Math.cos(Math.PI / 6));
		hex.lineTo(3 * sideLength / 2.0, 2 * sideLength * Math.cos(Math.PI / 6));
		hex.lineTo(sideLength / 2.0, 2 * sideLength * Math.cos(Math.PI / 6));
		hex.lineTo(0, sideLength * Math.cos(Math.PI / 6));
		hex.closePath();
		return hex;
	}

}
