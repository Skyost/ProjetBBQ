package fr.isn.bbq.prof;

import java.io.File;
import fr.isn.bbq.prof.utils.XMLSettings;

/**
 * Représente les paramètres de configuration de l'application.
 */

public class AppSettings extends XMLSettings {
	
	@SerializationOptions(name = "rooms-directory")
	public String roomDir = "Salles";
	
	public AppSettings(final File file) {
		super(file);
	}
	
}