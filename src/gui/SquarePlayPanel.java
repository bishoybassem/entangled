package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import engine.SquareBoard;


@SuppressWarnings("serial")
public class SquarePlayPanel extends PlayPanel {
	
	public SquarePlayPanel(SquareBoard board, double height, LevelPanel levelPanel) {
		super(board, height, levelPanel);
	}
	
	protected void initializeFields() {
		width = height;
		panelWidth = (int) (columns * height);
		panelHeight = (int) (rows * height);
		clip = new Rectangle2D.Double(0, 0, width, height);
		nSteps = 40;
	}
	
	protected void drawWall(Graphics2D g2d) {
		g2d.setPaint(new Color(200, 137, 91));
		g2d.fillRect(0, 0, widthA, heightA);
		g2d.setPaint(new GradientPaint(0, 0, new Color(215, 168, 134), widthA, heightA, new Color(116, 72, 39)));
		g2d.setStroke(new BasicStroke(2));
		g2d.drawRect(1, 1, widthA - 2, heightA - 2);
	}
	
	protected void drawBackground(Graphics2D g2d) {
		g2d.setPaint(new Color(228, 228, 228));
		g2d.fillRect(0, 0, widthA, heightA);
		g2d.setPaint(new GradientPaint(0, 0, new Color(250, 250, 250), widthA, heightA, new Color(184, 184, 184)));
		g2d.setStroke(new BasicStroke(2));
		g2d.drawRect(1, 1, widthA - 2, heightA - 2);
	}
			
	protected void drawStartTile(Graphics2D g2d) {
		g2d.setColor(getColor(board.getCurrentPlayer()));
		g2d.fillRect(0, 0, widthA, heightA);
		double stroke = 5 * height / 36;
		g2d.setStroke(new BasicStroke((float) stroke));
		g2d.setColor(Color.DARK_GRAY);
		g2d.draw(new Rectangle2D.Double(stroke / 2, stroke / 2, height - stroke, height - stroke));
	}
	
	protected void drawCurrrentFrame(Graphics2D g2d) {
		float x = (float) (height / 18);
		g2d.setColor(getColor(board.getCurrentPlayer()));
		g2d.setStroke(new BasicStroke(x));
		g2d.draw(new Rectangle2D.Double(x / 2, x / 2, height - x, height - x));
	}
	
	protected double[] getCoordinates(int row, int column) {
		double x = column * height;
		double y = row * height;
		return new double[]{x, y};
	}

}
