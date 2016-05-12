package fr.isn.bbq.eleve.frames;

import java.awt.Container;
import java.awt.Toolkit;

import javax.swing.JFrame;

import fr.isn.bbq.eleve.ProjetBBQEleve;

import javax.swing.JLabel;

import java.awt.BorderLayout;

public class MessageFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Première méthode exécutée par la fenêtre.
	 */
	
	public MessageFrame(final String message, final int time) {
		this.setTitle("Message reçu");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ProjetBBQEleve.class.getResource("/fr/isn/bbq/eleve/res/app_icon.png")));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		final Container content = this.getContentPane();
		final JLabel lblMessage = new JLabel("<html>" + message + "<br></html>");
		content.add(lblMessage, BorderLayout.CENTER);
		final JLabel lblCountdown = new JLabel();
		content.add(lblCountdown, BorderLayout.SOUTH);
		this.pack();
		this.setSize(this.getWidth() + 10, this.getHeight() + 10);
	}

}