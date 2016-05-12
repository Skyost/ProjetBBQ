package fr.isn.bbq.eleve.frames;

import java.awt.Toolkit;

import javax.swing.JFrame;

import fr.isn.bbq.eleve.ProjetBBQEleve;

public class MessageFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Première méthode exécutée par la fenêtre.
	 */
	
	public MessageFrame() {
		this.setTitle("Message reçu");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ProjetBBQEleve.class.getResource("/fr/isn/bbq/eleve/res/app_icon.png")));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}

}