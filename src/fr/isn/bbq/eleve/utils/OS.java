package fr.isn.bbq.eleve.utils;

import javax.swing.JOptionPane;

public class OS {
	
	private static OSName name;
	static {
		final String name = System.getProperty("os.name").toUpperCase();
		if(name.contains("WINDOWS")) {
			OS.name = OSName.WINDOWS;
		}
		else if(name.contains("MAC")) {
			OS.name = OSName.MAC;
		}
		else if(name.contains("LINUX")) {
			OS.name = OSName.LINUX;
		}
		else {
			OS.name = OSName.UNKNOWN;
		}
	}
	
	public static final OSName getOSName() {
		return name;
	}
	
	public static final String getUserName() {
		return System.getProperty("user.name");
	}
	
	public static final void shutdown() {
		try {
			switch(name) {
			case WINDOWS:
				Runtime.getRuntime().exec("shutdown.exe -s -f -t 0"); // -s pour l'arret; -f pour le forcer; -t pour le temps.
				break;
			case MAC:
				Runtime.getRuntime().exec("sudo shutdown now -h");
				break;
			case LINUX:
				Runtime.getRuntime().exec("shutdown now -h -f");
				break;
			case UNKNOWN:
				JOptionPane.showMessageDialog(null, "L'arrêt à distance n'étant pas supporté sur votre ordinateur, merci de le faire de vous-même.", "Arrêt de l'ordinateur", JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erreur lors de l'arrêt à distance.", "Erreur !", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static final void restart() {
		try {
			switch(name) {
			case WINDOWS:
				Runtime.getRuntime().exec("shutdown.exe -r -f -t  0");
				break;
			case MAC:
				Runtime.getRuntime().exec("sudo shutdown now -r");
				break;
			case LINUX:
				Runtime.getRuntime().exec("shutdown now -r -f");
				break;
			case UNKNOWN:
				JOptionPane.showMessageDialog(null, "Le redémarrage à distance n'étant pas supporté sur votre ordinateur, merci de le faire de vous-même.", "Redémarrage de l'ordinateur", JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erreur lors du redémarrage à distance.", "Erreur !", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static final void logout() {
		try {
			switch(name) {
			case WINDOWS:
				Runtime.getRuntime().exec("shutdown.exe -l");
				break;
			case MAC:
				Runtime.getRuntime().exec("osascript -e 'tell app \"System Events\" to  «event aevtrlgo»'");
				break;
			case LINUX:
				new ProcessBuilder(new String[]{"pkill", "-KILL", "-u", getUserName()}).start();
				break;
			case UNKNOWN:
				JOptionPane.showMessageDialog(null, "La déconnexion à distance n'étant pas supporté sur votre ordinateur, merci de le faire de vous-même.", "Déconnexion de l'ordinateur", JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erreur lors de la déconnexion à distance.", "Erreur !", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public enum OSName {
		
		WINDOWS,
		MAC,
		LINUX,
		UNKNOWN;
		
	}

}