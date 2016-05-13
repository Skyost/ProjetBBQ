package fr.isn.bbq.prof;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.jtattoo.plaf.smart.SmartLookAndFeel;

import fr.isn.bbq.prof.frames.MainFrame;
import fr.isn.bbq.prof.utils.Utils;

public class ProjetBBQProf {
	
	/*
	 * Variables globales du projet.
	 */
	
	public static final String APP_NAME = "Projet BBQ";
	public static final String APP_VERSION = "0.1";
	
	public static AppSettings settings;
	
	/**
	 * Première méthode exécutée par le programme.
	 * 
	 * @param args Arguments à passer.
	 */
	
	public static final void main(final String[] args) {
		try {
			final Properties properties = new Properties();
			properties.put("logoString", APP_NAME); // On change la chaîne de caractères dans le menu.
			SmartLookAndFeel.setTheme(properties);
			UIManager.setLookAndFeel(new SmartLookAndFeel()); // Le style par défaut de l'application.
			final File settings = new File(Utils.getParentFolder(), "settings.xml"); // Le fichier de paramètres XML.
			ProjetBBQProf.settings = new AppSettings();
			if(!settings.exists()) { // Si il n'existe pas, on le créé et on applique des paramètres par défaut.
				ProjetBBQProf.settings.roomDir = new File(Utils.getParentFolder(), "Salles").getPath();
				ProjetBBQProf.settings.uuid = UUID.randomUUID().toString();
				Files.write(settings.toPath(), ProjetBBQProf.settings.toXML().getBytes());
			}
			else { // Sinon on le charge.
				ProjetBBQProf.settings.load(new String(Files.readAllBytes(settings.toPath())));
			}
			if(ProjetBBQProf.settings.addSample) { // Paramètres relatifs au fichier d'exemple.
				final File testFile = new File(getRoomDirectory(), "exemple.xml.test");
				if(!testFile.exists()) {
					final Room room = new Room();
					room.name = "Salle test";
					room.computers.addAll(Arrays.asList(
							new Computer("PC 1", "192.168.0.1", 4444),
							new Computer("PC 2", "192.168.0.2", 4444),
							new Computer("PC 3", "192.168.0.3", 4444)));
					Files.write(testFile.toPath(), room.toXML().getBytes());
				}
			}
			new MainFrame().setVisible(true); // On créé la première fenêtre du programme et on la rend visible.
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getClass().getName(), "Erreur durant le d�marrage ! Peut-�tre que la configuration est invalide, veuillez consulter l'aide en ligne.", JOptionPane.ERROR_MESSAGE);
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