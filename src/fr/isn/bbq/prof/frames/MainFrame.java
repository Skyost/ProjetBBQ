package fr.isn.bbq.prof.frames;

import javax.swing.JFrame;

import fr.isn.bbq.prof.ProjetBBQProf;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Premi�re m�thode ex�cut�e par la fen�tre.
	 */
	
	public MainFrame() {
		this.setTitle(ProjetBBQProf.APP_NAME + " v" + ProjetBBQProf.APP_VERSION);
		this.setSize(100, 100);
	}

}