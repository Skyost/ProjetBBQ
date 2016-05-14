package fr.isn.bbq.prof.dialogs;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Permet d'afficher un message.
 */

public class MessageDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Le message contenu par ce dialogue.
	 */
	
	private final JLabel message = new JLabel();

	/**
	 * Première méthode exécutée par le dialogue.
	 * 
	 * @param parent Le parent.
	 * @param title Le titre du dialogue.
	 * @param message Le message du dialogue.
	 */
	
	public MessageDialog(final JFrame parent, final String title, final String message) {
		this.setTitle(title);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(parent);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.add(this.message, BorderLayout.CENTER);
		setMessage(message);
	}
	
	/**
	 * Change le message de la boîte de dialogue.
	 * 
	 * @param message Le nouveau message à afficher.
	 */
	
	public final void setMessage(final String message) {
		this.message.setText("<html>" + message + "</html>");
		this.message.setHorizontalAlignment(SwingConstants.CENTER);
		this.message.setFont(this.message.getFont().deriveFont(Font.ITALIC));
		this.pack(); // On adapte la taille du dialogue au message.
		this.setSize(this.getWidth() + 50, this.getHeight() + 30); // Et on ajoute (30 / 2)px sur les côtés (15 en haut, 15 à droite, 15 en bas et 15 à gauche).
	}
	
	/**
	 * Permet d'obtenir le message affiché.
	 * 
	 * @return Le message affiché.
	 */
	
	public final String getMessage() {
		return message.getText();
	}

}