package fr.isn.bbq.eleve.frames;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import fr.isn.bbq.eleve.ProjetBBQEleve;

import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

public class LockFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private static final short UNLOCK_SECONDS = 2;
	
	private final BufferedImage screenshot;
	private final JLabel label = new JLabel();
	
	/**
	 * Première méthode exécutée par la fenêtre.
	 */
	
	public LockFrame(final BufferedImage screenshot) {
		this.setTitle("Locked");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ProjetBBQEleve.class.getResource("/fr/isn/bbq/eleve/res/app_icon.png")));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		final Graphics graphics = screenshot.getGraphics();
		graphics.setColor(new Color(0, 0, 0, (int)(256 - 256 * 0.5f)));
		graphics.fillRect(0, 0, screenshot.getWidth(), screenshot.getHeight());
		this.screenshot = screenshot;
		mergeImageWithScreenshot("/fr/isn/bbq/eleve/res/icons/icon_locked.png");
		this.getContentPane().add(label, BorderLayout.CENTER);
		this.setResizable(false);
	}
	
	public final void unlockAndClose() {
		this.setTitle("Unlocked");
		mergeImageWithScreenshot("/fr/isn/bbq/eleve/res/icons/icon_unlocked.png");
		new Timer().schedule(new TimerTask() {

			@Override
			public final void run() {
				LockFrame.this.setVisible(false);
				LockFrame.this.dispose();
			}
			
		}, UNLOCK_SECONDS * 1000L);
	}
	
	private final void mergeImageWithScreenshot(final String imageUrl) {
		try {
			final int height = screenshot.getHeight();
			final int width = screenshot.getWidth();
			final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			final Graphics graphics = image.getGraphics();
			final BufferedImage icon = ImageIO.read(ProjetBBQEleve.class.getResource(imageUrl));
			graphics.drawImage(screenshot, 0, 0, this);
			graphics.drawImage(icon, width / 2 - icon.getWidth() / 2, height / 2 - icon.getHeight() / 2, this);
			graphics.dispose();
			label.setIcon(new ImageIcon(image));
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
}