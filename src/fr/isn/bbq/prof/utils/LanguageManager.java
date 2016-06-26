package fr.isn.bbq.prof.utils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Properties;

import javax.swing.JOptionPane;

import fr.isn.bbq.prof.ProjetBBQProf;

/**
 * Permet de gérer les différentes languages gérées par le logiciel (tiré d'Algogo).
 */

public class LanguageManager {
	
	/**
	 * Si une clé n'est pas trouvée.
	 */
	
	public static final Map<String, String> DEFAULT_STRINGS = Collections.unmodifiableMap(new HashMap<String, String>() {

		private static final long serialVersionUID = 1L;
		
		{
			put("default-string", "?");
			put("error.title", "Erreur !");
			put("error.message", "Une erreur est survenue : \"%s\".");
		}
		
	});
	
	/**
	 * Le package contenant les fichiers de langue.
	 */
	
	public static final String PACKAGE = "/fr/isn/bbq/prof/res/lang/";
	
	/**
	 * Les langues disponibles.
	 */
	
	public static final HashMap<String, String> AVAILABLE_LANGUAGES = new HashMap<String, String>();
	
	private static final HashMap<String, String> strings = new HashMap<String, String>();
	static {
		try {
			AVAILABLE_LANGUAGES.put("fr", "Français"); // Une seule langue disponible : le français.
			if(AVAILABLE_LANGUAGES != null && AVAILABLE_LANGUAGES.size() > 0) {
				final Properties properties = new Properties();
				properties.load(new InputStreamReader(ProjetBBQProf.class.getResourceAsStream(PACKAGE + (AVAILABLE_LANGUAGES.get(ProjetBBQProf.settings.customLanguage) == null ? "fr" : ProjetBBQProf.settings.customLanguage) + ".lang"), StandardCharsets.UTF_8));
				for(final Entry<Object, Object> entry : properties.entrySet()) {
					strings.put(entry.getKey().toString(), entry.getValue().toString());
				}
			}
		}
		catch(final Exception ex) {
			JOptionPane.showMessageDialog(null, String.format(DEFAULT_STRINGS.get("error.message"), ex.getMessage()), DEFAULT_STRINGS.get("error.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Retourne la chaîne de caractères correspondant à la clé donnée.
	 * 
	 * @param key La clé.
	 * @param args Pour formatter la chaîne (avec <i>String.format(...)</i>).
	 * 
	 * @return La chaîne de caractère.
	 */
	
	public static final String getString(final String key, final Object... args) {
		String value = strings.get(key);
		if(value == null) {
			value = DEFAULT_STRINGS.get(key);
		}
		if(value != null) {
			return args == null ? value : String.format(value, args);
		}
		return DEFAULT_STRINGS.get("default-string");
	}
	
	/**
	 * Retourne le nom de la langue.
	 * 
	 * @return Le nom.
	 */
	
	public static final String getCurrentLanguageName() {
		return getString("language.name");
	}
	
	/**
	 * Retourne la version du fichier de langue.
	 * 
	 * @return La version.
	 */
	
	public static final int getCurrentLanguageVersion() {
		return Integer.parseInt(getString("language.version"));
	}
	
	/**
	 * Retourne une liste des langues disponibles.
	 * 
	 * @return Une map :
	 * <br><b>Clé :</b> Code la langue.
	 * <br><b>Valeur :</b> Nom de la langue.
	 */
	
	public static final Map<String, String> getAvailableLanguages() {
		return new HashMap<String, String>(AVAILABLE_LANGUAGES);
	}

}