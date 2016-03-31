package fr.isn.bbq.prof;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.UIManager;

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
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			final File settings = new File(Utils.getParentFolder(), "settings.xml");
			ProjetBBQProf.settings = new AppSettings();
			if(!settings.exists()) {
				ProjetBBQProf.settings.roomDir = new File(Utils.getParentFolder(), "Salles").getPath();
				Files.write(settings.toPath(), ProjetBBQProf.settings.toXML().getBytes());
			}
			else {
				ProjetBBQProf.settings.load(new String(Files.readAllBytes(settings.toPath())));
			}
			if(ProjetBBQProf.settings.addSample) {
				final Room room = new Room();
				room.name = "Salle test";
				room.computers = new ArrayList<Computer>(Arrays.asList(new Computer("PC 1", "192.168.0.1"), new Computer("PC 2", "192.168.0.2"), new Computer("PC 3", "192.168.0.3")));
				Files.write(new File(getRoomDirectory(), "exemple.xml.test").toPath(), room.toXML().getBytes());
			}
			new MainFrame().setVisible(true); // On créé la première fenêtre du programme et on la rend visible.
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static final File getRoomDirectory() throws URISyntaxException {
		final File rooms = new File(settings.roomDir);
		if(!rooms.exists()) {
			rooms.mkdir();
		}
		return rooms;
	}
	
}