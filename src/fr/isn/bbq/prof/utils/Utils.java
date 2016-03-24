package fr.isn.bbq.prof.utils;

import java.io.File;
import java.net.URISyntaxException;

import fr.isn.bbq.prof.ProjetBBQProf;

public class Utils {
	
	/**
	 * Permet d'obtenir le chemin du fichier JAR.
	 * 
	 * @return Le chemin du fichier JAR.
	 * 
	 * @throws URISyntaxException Si la destination n'est pas valide.
	 */

	public static final File getParentFolder() throws URISyntaxException {
		return new File(ProjetBBQProf.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
	}

}