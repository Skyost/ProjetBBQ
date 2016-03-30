package fr.isn.bbq.prof.dialogs;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MessageDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Première méthode exécutée par le dialogue.
	 */
	
	public MessageDialog(final JFrame parent, final String message) {
		this.setTitle("Patientez...");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(parent);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		final JLabel labelMessage = new JLabel(message);
		labelMessage.setHorizontalAlignment(SwingConstants.CENTER);
		labelMessage.setFont(labelMessage.getFont().deriveFont(Font.ITALIC));
		this.add(labelMessage, BorderLayout.CENTER);
		this.pack();
		this.setSize(this.getWidth() + 50, this.getHeight() + 30);
	}

}