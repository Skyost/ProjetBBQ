package fr.isn.bbq.eleve.frames;

import java.awt.Container;
import java.awt.Font;

import javax.swing.JDialog;

import fr.isn.bbq.eleve.ProjetBBQEleve;

import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class MessageFrame extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Message de compte à rebours.
	 */
	
	private static final String COUNTDOWN_MESSAGE = "Ce message se fermera dans %d seconde(s)...";
	
	/**
	 * Première méthode exécutée par la fenêtre.
	 * 
	 * @param message Le message.
	 * @param time La durée d'affichage.
	 */
	
	public MessageFrame(final String message, final int time) {
		this.setTitle("Message reçu !"); // Le titre de la fenêtre.
		this.setIconImages(ProjetBBQEleve.icons); // Icône de la fenêtre.
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Impossibilité de la fermer avant la fin du compte à rebours.
		this.setLocationRelativeTo(null); // Méthode pour centrer la fenêtre.
		this.setAlwaysOnTop(true); // Permet de la mettre au dessus de toutes les autres fenêtres.
		final Container content = this.getContentPane();
		final JLabel lblMessage = new JLabel("<html>" + message + "<br></html>");
		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblMessage.setBorder(new CompoundBorder(lblMessage.getBorder(), new EmptyBorder(10, 10, 0, 10))); // 10px en haut, 10 à droite, 0 en bas et 10 à gauche.
		lblMessage.setFont(lblMessage.getFont().deriveFont(Font.BOLD));
		content.add(lblMessage, BorderLayout.CENTER); // Le message au centre.
		final JLabel lblCountdown = new JLabel(String.format(COUNTDOWN_MESSAGE, time));
		lblCountdown.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountdown.setBorder(new CompoundBorder(lblCountdown.getBorder(), new EmptyBorder(20, 10, 10, 10))); // 20px en haut, 10 à droite, 10 en bas et 10 à gauche.
		lblCountdown.setFont(lblCountdown.getFont().deriveFont(Font.ITALIC));
		content.add(lblCountdown, BorderLayout.SOUTH); // Le compte à rebours en dessous.
		this.pack(); // Taille relative à la longueur du message.
		this.setResizable(false); // On enlève la possibilité de redimensionner la fenêtre.
		new Timer().scheduleAtFixedRate(new TimerTask() {
			
			private int currentTime = time;
			
			@Override
			public final void run() {
				lblCountdown.setText(String.format(COUNTDOWN_MESSAGE, currentTime--));
				if(currentTime == 0) { // Quand le compte à rebours est terminé.
					this.cancel(); // On arrête le compte à rebours.
					MessageFrame.this.dispose(); // Et on ferme la fenêtre.
					return;
				}
			}
			
		}, 0L, 1 * 1000L); // On exécute cette action toutes les secondes.
	}

}