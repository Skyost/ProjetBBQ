package fr.isn.bbq.prof;

import java.io.File;

import fr.isn.bbq.prof.utils.XMLSettings;

/**
 * Représente une salle de classe.
 */

public class Room extends XMLSettings {
	
	@SerializationOptions(name = "name")
	public String name;
	@SerializationOptions(name = "ip")
	public String ip;
	
	public Room(final File file) {
		super(file);
	}
	
}