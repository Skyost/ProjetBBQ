package fr.isn.bbq.prof;

import java.io.File;
import java.net.URISyntaxException;

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
			settings = new AppSettings(new File(Utils.getParentFolder(), "settings.xml"));
			settings.load();
			new MainFrame().setVisible(true); // On créé la première fenêtre du programme et on la rend visible.
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static final File getRoomDirectory() throws URISyntaxException {
		return getRoomDirectory(Utils.getParentFolder());
	}
	
	public static final File getRoomDirectory(final File parent) {
		final File rooms = new File(parent, settings.roomDir);
		if(!rooms.exists()) {
			rooms.mkdir();
		}
		return rooms;
	}

}