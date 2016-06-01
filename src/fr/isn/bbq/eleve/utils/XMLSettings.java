package fr.isn.bbq.eleve.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.w3c.dom.Element;

import fr.isn.bbq.eleve.ProjetBBQEleve;

/**
 * Création de fichiers en XML.
 * <br/>Utilisé pour les salles, les paramètres de l'application, ...
 */

public abstract class XMLSettings {
	
	/**
	 * On charge les champs de la classe en fonction du paramètre "content" qui est parsé.
	 * 
	 * @param content Le contenu XML.
	 * 
	 * @return <b>true</b> Si le contenu est parsé avec succès.
	 * <br><b>false</b> Autrement.
	 */
	
	public abstract boolean load(final File file);
	
	/**
	 * On transforme la classe en XML.
	 * 
	 * @return La classe transformée en XML.
	 */
	
	public abstract String toXML();
	
	/**
	 * Permet d'enregistrer la configuration.
	 * 
	 * @param file Le fichier.
	 * 
	 * @throws IOException Si une erreur se produit.
	 */
	
	public final void write(final File file) throws IOException {
		Files.write(file.toPath(), ProjetBBQEleve.settings.toXML().getBytes());
	}
	
	/**
	 * Vérifie si un élément contient une balise spécifique.
	 * 
	 * @param element L'élément.
	 * @param tag La balise spécifique.
	 * 
	 * @return <b>true</b> Si la balise est contenue dans l'élément.
	 * <br><b>false</b> Autrement.
	 */
	
	protected final boolean elementContains(final Element element, final String tag) {
		return element.getElementsByTagName(tag).getLength() > 0;
	}
	
}