package fr.isn.bbq.prof.dialogs;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import fr.isn.bbq.prof.ProjetBBQProf;

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
		this(parent, title, message, null);
	}
	
	/**
	 * Première méthode exécutée par le dialogue.
	 * 
	 * @param parent Le parent.
	 * @param title Le titre du dialogue.
	 * @param message Le message du dialogue.
	 * @param additionalComponent Si un composant additionnel doit être ajouté.
	 */
	
	public MessageDialog(final JFrame parent, final String title, final String message, final Component additionalComponent) {
		this.setTitle(title);
		this.setIconImages(ProjetBBQProf.icons);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(parent);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.getContentPane().setLayout(new GridBagLayout());
		
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		if(additionalComponent == null) {
			this.add(this.message, constraints);
		}
		else {
			this.add(this.message, constraints);
			
			constraints.gridy++;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			this.add(additionalComponent, constraints);
		}
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