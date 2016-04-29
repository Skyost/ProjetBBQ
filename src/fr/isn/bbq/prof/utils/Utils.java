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
	
	/**
	 * Joindre un tableau de chaînes de caractère.
	 * 
	 * @param joiner Le séparateur.
	 * @param strings Le tableau.
	 * 
	 * @return Le tableau de chaînes de caractère joint.
	 */

	public static final String join(final String joiner, final String... strings) {
		final StringBuilder builder = new StringBuilder();
		for(final String string : strings) {
			builder.append(string + joiner);
		}
		builder.setLength(builder.length() - joiner.length());
		return builder.toString();
	}

}