package fr.isn.bbq.prof;

import java.io.File;

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
	 * Premi�re m�thode ex�cut�e par le programme.
	 * 
	 * @param args Arguments � passer.
	 */
	
	public static final void main(final String[] args) {
		try {
			settings = new AppSettings(new File(Utils.getParentFolder(), "settings.xml"));
			settings.load();
			new MainFrame().setVisible(true); // On cr�� la premi�re fen�tre du programme et on la rend visible.
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}

}