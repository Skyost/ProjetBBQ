package fr.isn.bbq.eleve.utils;

/**
 * Création de fichiers en XML.
 * <br/>Utilisé pour les salles, les paramètres de l'application, ...
 */

public interface XMLSettings {
	
	/**
	 * On charge les champs de la classe en fonction du paramètre "content" qui est parsé.
	 * 
	 * @param content Le contenu XML.
	 * 
	 * @return <b>true</b> Si le contenu est parsé avec succès.
	 * <br><b>false</b> Autrement.
	 */
	
	public boolean load(final String content);
	
	/**
	 * On transforme la classe en XML.
	 * 
	 * @return La classe transformée en XML.
	 */
	
	public String toXML();
	
}