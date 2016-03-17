package fr.isn.bbq.prof;

import fr.isn.bbq.prof.frames.MainFrame;

public class ProjetBBQProf {
	
	/*
	 * Variables globales du projet.
	 */
	
	public static final String APP_NAME = "Projet BBQ";
	public static final String APP_VERSION = "0.1";
	
	/**
	 * Premi�re m�thode ex�cut�e par le programme.
	 * 
	 * @param args Arguments � passer .
	 */
	
	public static final void main(final String[] args) {
		new MainFrame().setVisible(true); // On cr�� la premi�re fen�tre du programme et on la rend visible.
	}

}