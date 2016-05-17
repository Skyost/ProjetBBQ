package fr.isn.bbq.eleve.frames;

import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;

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
	
	private static final String COUNTDOWN_MESSAGE = "Ce message se fermera dans %d seconde(s)...";
	
	/**
	 * Première méthode exécutée par la fenêtre.
	 */
	
	public MessageFrame(final String message, final int time) {
		this.setTitle("Message reçu !");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ProjetBBQEleve.class.getResource("/fr/isn/bbq/eleve/res/app_icon.png")));
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		final Container content = this.getContentPane();
		final JLabel lblMessage = new JLabel("<html>" + message + "<br></html>");
		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblMessage.setBorder(new CompoundBorder(lblMessage.getBorder(), new EmptyBorder(10, 10, 0, 10)));
		lblMessage.setFont(lblMessage.getFont().deriveFont(Font.BOLD));
		content.add(lblMessage, BorderLayout.CENTER);
		final JLabel lblCountdown = new JLabel(String.format(COUNTDOWN_MESSAGE, time));
		lblCountdown.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountdown.setBorder(new CompoundBorder(lblCountdown.getBorder(), new EmptyBorder(20, 10, 10, 10)));
		lblCountdown.setFont(lblCountdown.getFont().deriveFont(Font.ITALIC));
		content.add(lblCountdown, BorderLayout.SOUTH);
		this.pack();
		this.setResizable(false);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			
			private int currentTime = time;
			
			@Override
			public final void run() {
				if(currentTime == 0) {
					this.cancel();
					MessageFrame.this.dispose();
					return;
				}
				lblCountdown.setText(String.format(COUNTDOWN_MESSAGE, --currentTime));
			}
			
		}, 0L, 1 * 1000L);
	}

}