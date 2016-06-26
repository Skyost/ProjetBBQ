package fr.isn.bbq.eleve.utils;

import javax.swing.JOptionPane;

/**
 * Permet d'effectuer différentes actions sur le système.
 */

public class OS {
	
	private static OSName name;
	static {
		final String name = System.getProperty("os.name").toUpperCase();
		if(name.contains("WINDOWS")) { // Si c'est Windows, on mémorise windows.
			OS.name = OSName.WINDOWS;
		}
		else if(name.contains("MAC")) { // Si c'est Mac, on mémorise mac.
			OS.name = OSName.MAC;
		}
		else if(name.contains("LINUX")) { // Si c'est Linux, on mémorise linux.
			OS.name = OSName.LINUX;
		}
		else { // Sinon, on ne sait pas.
			OS.name = OSName.UNKNOWN;
		}
	}
	
	/**
	 * Permet de retourner l'OS.
	 * 
	 * @return L'OS.
	 */
	
	public static final OSName getOSName() {
		return name;
	}
	
	/**
	 * Permet de retourner le nom d'utilisateur.
	 * 
	 * @return Le nom d'utilisateur.
	 */
	
	public static final String getUserName() {
		return System.getProperty("user.name");
	}
	
	/**
	 * Permet d'éteindre l'ordinateur.
	 */
	
	public static final void shutdown() {
		try {
			switch(name) {
			case WINDOWS:
				Runtime.getRuntime().exec("shutdown.exe -s -f -t 0"); // -s pour l'arret; -f pour le forcer; -t pour le temps.
				break;
			case MAC:
				Runtime.getRuntime().exec("sudo shutdown now -h"); // On arrête l'ordinateur maintenant.
				break;
			case LINUX:
				Runtime.getRuntime().exec("shutdown now -h -f"); // Idem ici.
				break;
			case UNKNOWN: // Sinon on affiche une boîte de dialogue demandant à l'utilisateur d'effectuer l'action de lui-même.
				JOptionPane.showMessageDialog(null, LanguageManager.getString("os.shutdown.unknown.message"), LanguageManager.getString("os.shutdown.unknown.title"), JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
		catch(final Exception ex) { // Si une erreur se produit, on demande à l'utilisateur d'effectuer l'action de lui-même.
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, LanguageManager.getString("os.shutdown.unknown.error"), LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Permet de redémarrer l'ordinateur.
	 */
	
	public static final void restart() {
		try {
			switch(name) {
			case WINDOWS:
				Runtime.getRuntime().exec("shutdown.exe -r -f -t  0"); // Voir documentation sur l'arrêt plus haut.
				break;
			case MAC:
				Runtime.getRuntime().exec("sudo shutdown now -r");
				break;
			case LINUX:
				Runtime.getRuntime().exec("shutdown now -r -f");
				break;
			case UNKNOWN:
				JOptionPane.showMessageDialog(null, LanguageManager.getString("os.restart.unknown.message"), LanguageManager.getString("os.restart.unknown.title"), JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, LanguageManager.getString("os.restart.unknown.error"), LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Permet de déconnecter l'utilisateur.
	 */
	
	public static final void logout() {
		try {
			switch(name) {
			case WINDOWS:
				Runtime.getRuntime().exec("shutdown.exe -l"); // Voir documentation sur l'arrêt plus haut.
				break;
			case MAC:
				Runtime.getRuntime().exec("osascript -e 'tell app \"System Events\" to  «event aevtrlgo»'"); // On demande au système de retourner à l'écran de connexion.
				break;
			case LINUX:
				new ProcessBuilder(new String[]{"pkill", "-KILL", "-u", getUserName()}).start(); // On kill la session de l'utilisateur.
				break;
			case UNKNOWN:
				JOptionPane.showMessageDialog(null, LanguageManager.getString("os.logout.unknown.message"), LanguageManager.getString("os.logout.unknown.title"), JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, LanguageManager.getString("os.logout.unknown.error"), LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Représente un système.
	 */
	
	public enum OSName {
		
		/**
		 * Représente un système Windows.
		 */
		
		WINDOWS,
		
		/**
		 * Représente un système Mac.
		 */
		
		MAC,
		
		/**
		 * Représente un système Linux.
		 */
		
		LINUX,
		
		/**
		 * Représente un système inconnu.
		 */
		
		UNKNOWN;
		
	}

}