package gui;

import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import engine.Player;

@SuppressWarnings("serial")
public class Entangled extends JFrame {
	
	private int size;
	private int openings;
	private boolean square;
	private boolean[] playersTypes;
	private LevelPanel current;
	private NewGameDialog newGameDialog;
	
	public Entangled() {
		super("Entanglement");
		
		UIManager.put("Label.font", new Font("Comic sans ms", Font.PLAIN, 17));
		UIManager.put("Button.font", new Font("Comic sans ms", Font.PLAIN, 17));
		UIManager.put("ComboBox.font", new Font("Comic sans ms", Font.PLAIN, 15));
		UIManager.put("TextPane.font", new Font("Comic sans ms", Font.PLAIN, 17));
		
		setIconImage(new ImageIcon(getClass().getResource("resources/icon2.png")).getImage());
		setResizable(false);
		setUndecorated(true);
		
		newGameDialog = new NewGameDialog(this);
		newGameDialog.setVisible(true);
	}
	
	public void resetLevel() {
		setLevel(size, openings, square, playersTypes);
	}
	
	public void setLevel(int size, int openings, boolean square, boolean[] playersTypes) {
		setVisible(false);
		this.size = size;
		this.square = square;
		this.openings = openings;
		this.playersTypes = playersTypes;
		Player[] players = new Player[playersTypes.length];
		for (int i = 0; i < players.length; i++) {
			players[i] = new Player(i, playersTypes[i]);
		}
		try {
			current.stop();
			remove(current);
		} catch (Exception ex) {

		}
		double height = (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 150) / size;
		current = new LevelPanel(this, newGameDialog, size, openings, square, players, height);
		add(current);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		current.requestFocusInWindow();
	}
	
	public static void main(String[] args) {
		new Entangled();
	}

}
