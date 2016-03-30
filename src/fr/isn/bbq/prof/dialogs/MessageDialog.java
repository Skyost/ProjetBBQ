package fr.isn.bbq.prof.dialogs;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MessageDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private final JLabel message = new JLabel();

	/**
	 * Première méthode exécutée par le dialogue.
	 */
	
	public MessageDialog(final JFrame parent, final String message) {
		this.setTitle("Patientez...");
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
		this.message.setText(message);
		this.message.setHorizontalAlignment(SwingConstants.CENTER);
		this.message.setFont(this.message.getFont().deriveFont(Font.ITALIC));
		this.pack();
		this.setSize(this.getWidth() + 50, this.getHeight() + 30);
	}

}