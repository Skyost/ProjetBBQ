package fr.isn.bbq.prof;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.jtattoo.plaf.smart.SmartLookAndFeel;

import fr.isn.bbq.prof.frames.MainFrame;
import fr.isn.bbq.prof.utils.Utils;

public class ProjetBBQProf {
	
	/*
	 * Constantes globales du projet :
	 */
	
	public static final String APP_NAME = "Projet BBQ";
	public static final String APP_VERSION = "0.1.2";
	
	/*
	 * Certaines variables comme les paramètres, la liste d'icônes (16, 32, 64, 128, 256, 512) :
	 */
	
	public static AppSettings settings;
	public static final List<Image> icons = new ArrayList<Image>();
	
	/**
	 * Première méthode exécutée par le programme.
	 * 
	 * @param args Arguments à passer.
	 */
	
	public static final void main(final String[] args) {
		try {
			final Properties properties = new Properties();
			properties.put("logoString", APP_NAME); // On change la chaîne de caractères dans les menus.
			SmartLookAndFeel.setTheme(properties);
			UIManager.setLookAndFeel(new SmartLookAndFeel()); // Le style par défaut de l'application.
			final File settings = new File(Utils.getParentFolder(), "settings.xml"); // Le fichier de paramètres XML.
			ProjetBBQProf.settings = new AppSettings();
			if(settings.exists()) { // Si les paramètres existent, on les charge.
				ProjetBBQProf.settings.load(settings);
			}
			else { // Sinon on le créé et on applique des paramètres par défaut.
				ProjetBBQProf.settings.roomDir = new File(Utils.getParentFolder(), "Salles").getPath();
				ProjetBBQProf.settings.uuid = UUID.randomUUID().toString();
				ProjetBBQProf.settings.write(settings);
			}
			Utils.loadMessagesInSettings();
			if(ProjetBBQProf.settings.addSample) { // Paramètres relatifs au fichier d'exemple.
				final File testFile = new File(getRoomDirectory(), "exemple.xml.test");
				if(!testFile.exists()) {
					final Room room = new Room(); // On créé une salle de test.
					room.name = "Salle test";
					room.computers.addAll(Arrays.asList( // On y ajoute des PCs.
							new Computer("PC 1", "192.168.0.1", 4444),
							new Computer("PC 2", "192.168.0.2", 4444),
							new Computer("PC 3", "192.168.0.3", 4444)));
					room.write(testFile); // Et on enregistre cette salle.
				}
			}
			final Image icon = Toolkit.getDefaultToolkit().getImage(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/app_icon.png"));
			icons.addAll(Arrays.asList(
				icon.getScaledInstance(16, 16, Image.SCALE_SMOOTH),
				icon.getScaledInstance(32, 32, Image.SCALE_SMOOTH),
				icon.getScaledInstance(64, 64, Image.SCALE_SMOOTH),
				icon.getScaledInstance(128, 128, Image.SCALE_SMOOTH),
				icon.getScaledInstance(256, 256, Image.SCALE_SMOOTH),
				icon/*.getScaledInstance(512, 512, Image.SCALE_SMOOTH) // L'icône est déjà en 512x512 */
			));
			new MainFrame().setVisible(true); // On créé la première fenêtre du programme et on la rend visible.
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getClass().getName(), "Erreur durant le démarrage ! Peut-être que la configuration est invalide, veuillez consulter l'aide en ligne.", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Permet de retourner le fichier des salles de classe. Le créé si il n'existe pas.
	 * 
	 * @return Le fichier des salles de classe.
	 */
	
	public static final File getRoomDirectory() {
		final File rooms = new File(settings.roomDir);
		if(!rooms.exists()) {
			rooms.mkdir();
		}
		return rooms;
	}
	
}