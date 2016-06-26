package fr.isn.bbq.eleve.frames;

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import fr.isn.bbq.eleve.ProjetBBQEleve;
import fr.isn.bbq.eleve.utils.LanguageManager;

import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class LockFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Le temps pendant lequel on affiche l'icône de déverrouillage.
	 */
	
	private static final short UNLOCK_SECONDS = 2;
	
	/**
	 * Le screenshot de fond (sans icône de verrouillage).
	 */
	
	private final BufferedImage screenshot;
	
	/**
	 * Permet d'afficher l'écran de verrouillage.
	 */
	
	private final JLabel label = new JLabel();
	
	/**
	 * Première méthode exécutée par la fenêtre.
	 * 
	 * @param screenshot L'image de fond (une capture d'écran de préférence).
	 */
	
	public LockFrame(final BufferedImage screenshot) {
		this.setTitle(LanguageManager.getString("lock.title.locked")); // Titre par défaut de la fenêtre (invisible)
		this.setIconImages(ProjetBBQEleve.icons); // Icône invisible également.
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // On refuse de la fermer.
		this.setLocationRelativeTo(null); // On la centre.
		this.setAlwaysOnTop(true); // On lui indique de rester au dessus des autres fenêtres.
		this.setState(JFrame.NORMAL); // La fenêtre ne doit pas être miniaturisée.
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); // On maximise la fenêtre.
		this.setUndecorated(true); // Et on enlève la barre avec le bouton fermer, réduire, etc...
		final Graphics graphics = screenshot.getGraphics();
		graphics.setColor(new Color(0, 0, 0, (int)(256 - 256 * 0.5f))); // On créé une couleur permettant d'assombrir le screenshot.
		graphics.fillRect(0, 0, screenshot.getWidth(), screenshot.getHeight()); // Et on applique cette couleur.
		this.screenshot = screenshot; // On garde une référence vers le screenshot.
		mergeImageWithScreenshot("/fr/isn/bbq/eleve/res/icons/icon_locked.png"); // On applique l'image de verrouillage au screenshot.
		this.getContentPane().add(label, BorderLayout.CENTER);
		this.setResizable(false); // Et on enlève le redimensionnement.
	}
	
	/**
	 * Débloque et ferme cette fenêtre.
	 */
	
	public final void unlockAndClose() {
		this.setTitle(LanguageManager.getString("lock.title.unlocked"));
		mergeImageWithScreenshot("/fr/isn/bbq/eleve/res/icons/icon_unlocked.png"); // On applique l'image de déverrouillage au screenshot.
		new Timer().schedule(new TimerTask() {

			@Override
			public final void run() {
				LockFrame.this.setVisible(false);
				LockFrame.this.dispose(); // On ferme la fenêtre.
			}
			
		}, UNLOCK_SECONDS * 1000L); // Le délai de fermeture.
	}
	
	/**
	 * Permet d'ajouter une image au centre du screenshot.
	 * 
	 * @param imageUrl Le lien vers cette image.
	 */
	
	private final void mergeImageWithScreenshot(final String imageUrl) {
		try {
			final int height = screenshot.getHeight();
			final int width = screenshot.getWidth();
			final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // On créé une nouvelle image.
			final Graphics graphics = image.getGraphics();
			final BufferedImage icon = ImageIO.read(ProjetBBQEleve.class.getResource(imageUrl)); // On va chercher l'image en fonction de l'url donné.
			final Dimension iconSize = new Dimension(height / 4, height / 4);
			graphics.drawImage(screenshot, 0, 0, this); // On ajoute le screenshot sur cette image.
			graphics.drawImage(icon.getScaledInstance(iconSize.width, iconSize.height, Image.SCALE_SMOOTH), width / 2 - iconSize.width / 2, height / 2 - iconSize.height / 2, this); // Puis on y ajoute l'image donnée au centre.
			graphics.dispose();
			label.setIcon(new ImageIcon(image)); // Enfin, on applique cette image nouvellement créée.
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
}