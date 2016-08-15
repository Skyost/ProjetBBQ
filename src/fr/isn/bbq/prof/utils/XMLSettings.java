package fr.isn.bbq.prof.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

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
	 * @return L'erreur rencontrée pendant le chargement.
	 */
	
	public abstract XMLError load(final File file);
	
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
		Files.write(file.toPath(), toXML().getBytes(StandardCharsets.UTF_8));
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
	
	/**
	 * Permet de retourner un objet contenu dans un élément.
	 * 
	 * @param element L'élément.
	 * @param tag Le tag de l'objet.
	 * @param type Le type de cet object.
	 * 
	 * @return Retourne l'object contenu dans l'élément.
	 */
	
	@SuppressWarnings("unchecked")
	protected final <T> T getObject(final Element element, final String tag, final Class<? extends T> type) {
		try {
			if(!elementContains(element, tag)) {
				return null;
			}
			final String value = element.getElementsByTagName(tag).item(0).getFirstChild().getNodeValue();
			if(type == String.class) {
				return (T)value;
			}
			if(type == Integer.class) {
				return (T)Integer.valueOf(value);
			}
			if(type == Boolean.class) {
				return (T)Boolean.valueOf(value);
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Class permettant de renvoyer des erreurs pendant le chargement du XML.
	 */
	
	public static class XMLError {
		
		private final List<String> invalidParameters = new ArrayList<String>();
		
		/**
		 * Permet de retourner les paramètres dits invalides.
		 * 
		 * @return Les paramètres invalides.
		 */
		
		public final String[] getInvalidParameters() {
			return invalidParameters.toArray(new String[invalidParameters.size()]);
		}
		
		/**
		 * Permet d'ajouter des paramètres dits invalides.
		 * 
		 * @param invalidParameters Les paramètres invalides.
		 */
		
		public final void addInvalidParameters(final String... invalidParameters) {
			this.invalidParameters.addAll(Arrays.asList(invalidParameters));
		}
		
		/**
		 * Permet de supprimer tous les paramètres invalides.
		 */
		
		public final void clearInvalidParameters() {
			this.invalidParameters.clear();
		}
		
	}
	
}