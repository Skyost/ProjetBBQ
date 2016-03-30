package fr.isn.bbq.prof.utils;

/**
 * Création de paramètres en XML (par reflection).
 * <br/>Utilisé pour les salles, les paramètres de l'application, ...
 */

public interface XMLSettings {
	
	public boolean load(final String content);
	public String toXML();
	
}