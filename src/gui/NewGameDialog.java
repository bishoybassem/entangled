package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class NewGameDialog extends JDialog {
	
	private JComboBox<String> numberOfOpenings;
	private JComboBox<String> boardSize;
	private JComboBox<String> playersNumber;
	private JComboBox<String> tileShape;
	private ArrayList<JComboBox<String>> playersTypes;
	private JPanel settingsPanel;
	private boolean first;
	private boolean square;
	private JPanel mainPanel;
		
	public NewGameDialog(final Entanglement entanglement) {
		super(entanglement, true);
		first = true;
		
		JButton startGame = new JButton("Start Game");
		startGame.setFocusable(false);
		startGame.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				int size = 7 + boardSize.getSelectedIndex() * 2;
				boolean[] players = new boolean[playersNumber.getSelectedIndex() + 1];
				for (int i = 0; i < players.length; i++) {
					players[i] = playersTypes.get(i).getSelectedIndex() == 1;
				}
				int openings = numberOfOpenings.getSelectedIndex() + 1;
				first = false;
				entanglement.setLevel(size, openings, square, players);
			}
			
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.setFocusable(false);
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (first) {
					System.exit(0);
				} else {
					setVisible(false);
				}
			}
			
		});
	
		final JPanel p1 = new JPanel() {
			
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setFont(new Font("Comic sans ms", Font.PLAIN, 50));
				FontMetrics fm = g2d.getFontMetrics();
				
		        Rectangle2D r = fm.getStringBounds("ENTANGLEMENT", g2d);
		        int x = (int) (getWidth() - r.getWidth()) / 2;
		        int y = (int) (getHeight() - r.getHeight()) / 2 + fm.getAscent();

		        g.setColor(Color.WHITE);
		        g.setColor(new Color(255, 232, 100));
		        g.drawString("ENTANGLEMENT", x - 1, y + 2);
		        g.setColor(Color.DARK_GRAY);
		        g.drawString("ENTANGLEMENT", x, y);
			}
			
			public Dimension getPreferredSize() {
				return new Dimension(0, 65);
			}
			
		};
		
		JPanel p2 = new JPanel();
		p2.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
		p2.setOpaque(false);
		p2.add(startGame);
		p2.add(cancel);
				
		setSettingsPanel();
		
		mainPanel = new JPanel(new BorderLayout()) {
			
			public void paintComponent(Graphics g) {
			    if (!isOpaque()) {
			        super.paintComponent(g);
			        return;
			    }
			    
			    Graphics2D g2d = (Graphics2D) g;
			    int w = getWidth();
			    int h = getHeight();
			    GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, h, new Color(255, 232, 100));
			    g2d.setPaint(gp);
			    g2d.fillRect(0, 0, w, h);

			    setOpaque(false);
			    super.paintComponent(g);
			    setOpaque(true);
			}
			
		};
		mainPanel.setBorder(new LineBorder(Color.GRAY, 4));
		mainPanel.setBackground(new Color(255, 245, 185));
		mainPanel.add(p1, BorderLayout.NORTH);
		mainPanel.add(settingsPanel);
		mainPanel.add(p2, BorderLayout.SOUTH);
		
		add(mainPanel);
		setResizable(false);
		setUndecorated(true);
		pack();
		setLocationRelativeTo(null);
	}
			
	private void setSettingsPanel() {
		if (square) {
			playersNumber = new JComboBox<String>(new String[]{"1 Player", "2 Players", "3 Players", "4 Players"});
		} else {
			playersNumber = new JComboBox<String>(new String[]{"1 Player", "2 Players", "3 Players", "4 Players", "5 Players", "6 Players"});
		}
		playersNumber.setFocusable(false);
		playersNumber.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				for (int i = 1; i < playersTypes.size(); i++){
					playersTypes.get(i).setEnabled(i < playersNumber.getSelectedIndex() + 1);
				}
			}
			
		});
		
		boardSize = new JComboBox<String>(new String[]{"7 x 7", "9 x 9", "11 x 11", "13 x 13", "15 x 15"});
		boardSize.setFocusable(false);
		
		numberOfOpenings = new JComboBox<String>(new String[]{"1 Opening", "2 Openings", "3 Openings", "4 Openings"});
		numberOfOpenings.setFocusable(false);
		
		tileShape = new JComboBox<String>(new String[]{"Hexagon", "Square"});
		tileShape.setSelectedIndex((square)? 1 : 0);
		tileShape.setFocusable(false);
		tileShape.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (isVisible()) {
					setVisible(false);
					square = tileShape.getSelectedIndex() == 1;
					mainPanel.remove(settingsPanel);
					setSettingsPanel();
					mainPanel.add(settingsPanel);
					pack();
					setLocationRelativeTo(null);
					setVisible(true);
				}
			}
			
		});
		
		JPanel p1 = new JPanel(new GridLayout(4, 2, 5, 5));
		p1.setOpaque(false);
		p1.add(new JLabel("Shape"));
		p1.add(tileShape);
		p1.add(new JLabel("Size"));
		p1.add(boardSize);
		p1.add(new JLabel("Openings"));
		p1.add(numberOfOpenings);
		p1.add(new JLabel("Players"));
		p1.add(playersNumber);
		
		int r = (square)? 4 : 6;
		
		JPanel p2 = new JPanel(new GridLayout(r, 2, 5, 5));
		p2.setOpaque(false);
		
		JLabel[] labels = new JLabel[r];
		playersTypes = new ArrayList<JComboBox<String>>();
		for (int i = 0; i < labels.length; i++) {
			labels[i] = new JLabel("Player " + (i + 1));
			labels[i].setForeground(PlayPanel.getColor(i));
			JComboBox<String> playerType = new JComboBox<String>(new String[]{"Human"});
			if (i != 0) {
				playerType.addItem("Computer");
				playerType.setEnabled(false);
			}
			playerType.setFocusable(false);
			p2.add(labels[i]);
			p2.add(playerType);
			playersTypes.add(playerType);
		}
		
		JPanel p3 = new JPanel();
		p3.setOpaque(false);
		p3.add(p1);
		p3.add(Box.createRigidArea(new Dimension(30, 0)));
		p3.add(p2);
		
		settingsPanel = new JPanel();
		settingsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		settingsPanel.setOpaque(false);
		settingsPanel.add(p3);
	}
	
}
