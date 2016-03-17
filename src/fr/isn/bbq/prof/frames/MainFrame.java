package fr.isn.bbq.prof.frames;

import javax.swing.JFrame;

import fr.isn.bbq.prof.ProjetBBQProf;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Première méthode exécutée par la fenêtre.
	 */
	
	public MainFrame() {
		this.setTitle(ProjetBBQProf.APP_NAME + " v" + ProjetBBQProf.APP_VERSION);
		this.setSize(100, 100);
	}

}