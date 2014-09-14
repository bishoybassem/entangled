package gui;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

import engine.Board;
import engine.HexagonBoard;
import engine.Player;
import engine.SquareBoard;


@SuppressWarnings("serial")
public class LevelPanel extends JPanel {

	private Entanglement entanglement;
	private NewGameDialog newGameDialog;
	private Board board;
	private PlayPanel playPanel;
	private MessageDialog controlsDialog;
	private MessageDialog aboutDialog;
	private JPanel sidePanel;
	private JPanel swapTilePanel;
	private JPanel scorePanel;
	
	private AudioClip cheers;
	private AudioClip fix;

	private boolean stop;
	
	public LevelPanel(Entanglement entanglement, NewGameDialog newGameDialog, int size, int openings, boolean square, Player[] players, double height) {
		super(new BorderLayout());
		this.entanglement = entanglement;
		this.newGameDialog = newGameDialog;
		controlsDialog = new MessageDialog(entanglement, false);
		aboutDialog = new MessageDialog(entanglement, true);
		
		if (square) {
			board = new SquareBoard(size, size, openings, players);
			playPanel = new SquarePlayPanel((SquareBoard) board, height, this);
		} else {
			board = new HexagonBoard(size, size, openings, players);
			playPanel = new HexagonPlayPanel((HexagonBoard) board, height, this);
		}
		
		cheers = Applet.newAudioClip(getClass().getResource("resources/cheers.wav"));
		fix = Applet.newAudioClip(getClass().getResource("resources/fix.au"));
		setSidePanel();
		setActions();
		
		JPanel p1 = new JPanel();
		p1.setOpaque(false);
		p1.add(playPanel);
		p1.add(sidePanel);
		p1.add(Box.createRigidArea(new Dimension(10, 10)));
		
		scorePanel = new JPanel() {
			
			public Dimension getPreferredSize() {
				return new Dimension(0, 40);
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Player[] players = board.getPlayers();
				String s = "Scores :    ";
				for (int i = 0; i < players.length; i++) {
					if (i < players.length - 1) {
						s += players[i].getScore() + "    ";
					} else {
						s += players[i].getScore() + "";
					}
				}
				
				BufferedImage scores = new BufferedImage(getWidth(), 40, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = (Graphics2D) scores.getGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setFont(new Font("Comic sans ms", Font.PLAIN, 20));
				
				FontMetrics fm = g2d.getFontMetrics();
				int x = (scores.getWidth() - fm.stringWidth(s)) / 2;
				int y = (scores.getHeight() + fm.getAscent()) / 2;
				
				g2d.setColor(Color.BLACK);
				g2d.drawString("Scores :    ", x, y);
				x += fm.stringWidth("Scores :    ");
				for (int i = 0; i < players.length; i++) {
					if (i < players.length - 1) {
						s = players[i].getScore() + "    ";
					} else {
						s = players[i].getScore() + "";
					}
					g2d.setColor(SquarePlayPanel.getColor(players[i].getNumber()));
					g2d.drawString(s, x, y);
					x += fm.stringWidth(s);
				}
				g.drawImage(scores, 0,0,null);
			}
			
		};
		scorePanel.setOpaque(false);
		
		add(scorePanel, BorderLayout.NORTH);
		add(p1);
		setBorder(new LineBorder(Color.GRAY, 4));
		setFocusable(true);
	}
	
	private void setActions() {
		addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {
				if (playPanel.isAnimating()) {
					return;
				}
				if (e.getWheelRotation() > 0) {
					playPanel.rotateCurrentTile(1);
				} else {
					playPanel.rotateCurrentTile(-1);
				}
			}
			
		});
			
		getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
		getActionMap().put("left", new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				if (!playPanel.isAnimating()) {
					playPanel.rotateCurrentTile(-1);
				}
			}
			
		});
		
		getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
		getActionMap().put("right", new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				if (!playPanel.isAnimating()) {
					playPanel.rotateCurrentTile(1);
				}
			}
			
		});
		
		getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "select");
		getActionMap().put("select", new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				fixTile();
			}
			
		});
		
		getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
		getActionMap().put("exit", new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
			
		});
	}
	
	private void setSidePanel() {
		swapTilePanel = new JPanel() {
			
			private Shape clip1 = HexagonPlayPanel.generateHexClip(108);
			private Shape clip2 = new Rectangle(0, 0, 108, 108);
			
			public Dimension getPreferredSize() {
				return new Dimension(108, 108);
			}
			
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				if (playPanel instanceof HexagonPlayPanel) {
					double h = 108 * Math.cos(Math.PI / 6);
					g2d.translate(0, 108 - h);
					HexagonPlayPanel.fillHexagon(g2d, new Color(228, 228, 228), 108);
					HexagonPlayPanel.drawHexagon(g2d, new GradientPaint(0, 0, new Color(250, 250, 250), 108, (int) h, new Color(184, 184, 184)), 108, 2);
					PlayPanel.drawTile(g2d, 0, 0, board.getSwitchTile(), 108, clip1);
				} else {
					g2d.setPaint(new Color(228, 228, 228));
					g2d.fillRect(0, 0, 108, 108);
					g2d.setPaint(new GradientPaint(0, 0, new Color(250, 250, 250), 108, 108, new Color(184, 184, 184)));
					g2d.setStroke(new BasicStroke(2));
					g2d.drawRect(1, 1, 106, 106);
					PlayPanel.drawTile(g2d, 0, 0, board.getSwitchTile(), 108, clip2);
				}
			}

		};
		swapTilePanel.setOpaque(false);
		
		JButton reset = new JButton("Reset Game");
		reset.setFocusable(false);
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				entanglement.resetLevel();
			}
			
		});
		
		JButton newGame = new JButton("New Game");
		newGame.setFocusable(false);
		newGame.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				newGameDialog.setVisible(true);
			}
			
		});
		
		JButton exit = new JButton("Exit");
		exit.setFocusable(false);
		exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
			
		});
		
		JButton howToPlay = new JButton("How To Play");
		howToPlay.setFocusable(false);
		howToPlay.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				controlsDialog.setVisible(true);
			}
			
		});
		
		JButton swap = new JButton("Swap");
		swap.setFocusable(false);
		swap.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (!playPanel.isAnimating() && !board.isGameOver()) {
					board.switchTile();
					swapTilePanel.repaint();
					playPanel.drawCurrentTile();
					playPanel.repaint();
				}
			}
			
		});
		
		JButton about = new JButton("About");
		about.setFocusable(false);
		about.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				aboutDialog.setVisible(true);
			}
			
		});
		
		JPanel p1 = new JPanel();
		p1.add(swapTilePanel);
		p1.setOpaque(false);

		JPanel p2 = new JPanel(new BorderLayout(5, 5));
		p2.setOpaque(false);
		p2.add(p1, BorderLayout.NORTH);
		p2.add(swap);
		p2.add(reset, BorderLayout.SOUTH);
		
		JPanel p3 = new JPanel(new BorderLayout(5, 5));
		p3.setOpaque(false);
		p3.add(newGame, BorderLayout.NORTH);
		p3.add(howToPlay);
		p3.add(about, BorderLayout.SOUTH);
		
		sidePanel = new JPanel(new BorderLayout(5, 5));
		sidePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		sidePanel.setOpaque(false);
		sidePanel.add(p2, BorderLayout.NORTH);
		sidePanel.add(p3);
		sidePanel.add(exit, BorderLayout.SOUTH);
	}
	
	public void fixTile() {
		if (stop) {
			return;
		}
		if (!board.isGameOver() && !playPanel.isAnimating()) {
			playPanel.fixCurrentTile();
			scorePanel.repaint();
			if (board.isGameOver()) {
				if (board.getWinner() > -1) {
					cheers.play();
				}
				playPanel.displayResult();
			} else if (board.getPlayers()[board.getCurrentPlayer()].isComputer()) {
				int[] move = board.getBestMove();
				if (move[0] == 1) {
					board.switchTile();
					playPanel.drawCurrentTile();
					playPanel.repaint();
				}
				swapTilePanel.repaint();
				int angle = move[1];
				if (angle > board.getSides() / 2) {
					angle = angle - board.getSides();
				}
				playPanel.rotateCurrentTile(angle);
			} else {
				swapTilePanel.repaint();
			}
			fix.play();
		}
	}
	
	public void stop() {
		stop = true;
		playPanel.stop();
		playPanel = null;
	}
	
	public void paintComponent(Graphics g) {
	    if (!isOpaque()) {
	        super.paintComponent(g);
	        return;
	    }
	    
	    Graphics2D g2d = (Graphics2D) g;
	    int w = getWidth();
	    int h = getHeight();
	    g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h, new Color(255, 232, 100)));
	    g2d.fillRect(0, 0, w, h);
	 
	    setOpaque(false);
	    super.paintComponent(g);
	    setOpaque(true);
	}
	
}
