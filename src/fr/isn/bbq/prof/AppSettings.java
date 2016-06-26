package fr.isn.bbq.prof;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import fr.isn.bbq.prof.utils.XMLSettings;

/**
 * Représente les paramètres de configuration de l'application.
 */

public class AppSettings extends XMLSettings {
	
	private static final String[] TAGS = new String[]{ // En cas de mise à jour de la configuration, il faut penser à ajouter la balise ici (en plus du reste) :
		"configuration",	// TAGS[0]
		"room-directory",	// TAGS[1]
		"uuid",				// TAGS[2]
		"add-sample",		// TAGS[3]
		"refresh-interval",	// TAGS[4]
		"time-out",			// TAGS[5]
		"thumbnail",		// TAGS[6]
		"height",			// TAGS[7]
		"width",			// TAGS[8]
		"default-messages",	// TAGS[9]
		"message",			// TAGS[10]
		"custom-language"	// TAGS[11]
	};
	
	public String roomDir = "Salles"; // Le répertoire des salles.
	public String uuid; // L'UUID dont le logiciel a besoin pour se connecter aux postes.
	public boolean addSample = true; // Si on doit ajouter un fichier d'exemple ou non.
	public int refreshInterval = 3; // Le temps de rafraîchissement (en sec).
	public int timeOut = 10; // Le temps imparti pour que la socket se connecte.
	public int thumbnailHeight = 100; // Hauteur de la miniature.
	public int thumbnailWidth = 100; // Largeur de la miniature.
	public List<String> defaultMessages = new ArrayList<String>(Arrays.asList(
		"Votre PC va s'éteindre dans quelques instants.<br>Veuillez enregistrer votre activité dès à présent.",
		"Veuillez stopper votre activité immédiatement."
	)); // Liste des messages par défaut dans la boîte de dialogue "Envoyer un message".
	public String customLanguage = Locale.getDefault().getLanguage().toLowerCase(); // Permet de définir une autre langue.
	
	@Override
	public final XMLError load(final File file) {
		final XMLError result = new XMLError();
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.parse(new InputSource(new StringReader(new String(Files.readAllBytes(file.toPath()))))); // On parse le contenu XML.
			final Element root = document.getDocumentElement();
			
			final String roomDir = getObject(root, TAGS[1], String.class); // On récupère le paramètre.
			if(roomDir == null) {
				result.addInvalidParameters(TAGS[1]);
			}
			else {
				this.roomDir = roomDir;
			}
			
			final String uuid = getObject(root, TAGS[2], String.class);
			if(uuid == null) {
				result.addInvalidParameters(TAGS[2]);
			}
			else {
				this.uuid = uuid;
			}
			
			final Boolean addSample = getObject(root, TAGS[3], Boolean.class);
			if(addSample == null) {
				result.addInvalidParameters(TAGS[3]);
			}
			else {
				this.addSample = addSample;
			}
			
			final Integer refreshInterval = getObject(root, TAGS[4], Integer.class);
			if(refreshInterval == null) {
				result.addInvalidParameters(TAGS[4]);
			}
			else {
				this.refreshInterval = refreshInterval;
			}
			
			final Integer timeOut = getObject(root, TAGS[5], Integer.class);
			if(timeOut == null) {
				result.addInvalidParameters(TAGS[5]);
			}
			else {
				this.timeOut = timeOut;
			}
			
			if(elementContains(root, TAGS[6])) {
				final Element thumbnail = (Element)root.getElementsByTagName(TAGS[6]).item(0);
				
				final Integer thumbnailHeight = getObject(thumbnail, TAGS[7], Integer.class);
				final Integer thumbnailWidth = getObject(thumbnail, TAGS[8], Integer.class);
				if(thumbnailHeight != null && thumbnailWidth != null) {
					this.thumbnailHeight = thumbnailHeight;
					this.thumbnailWidth = thumbnailWidth;
				}
				else {
					result.addInvalidParameters(TAGS[7], TAGS[8]);
				}
			}
			else {
				result.addInvalidParameters(TAGS[6]);
			}
			
			if(elementContains(root, TAGS[9])) {
				defaultMessages.clear(); // On enlève tous les éléments qui sont déjà dans la liste des messages par défaut.
				final NodeList defaultMessages = ((Element)root.getElementsByTagName(TAGS[9]).item(0)).getElementsByTagName(TAGS[10]);
				for(int i = 0; i != defaultMessages.getLength(); i++) { // On parse chaque élément du noeud <default-messages> qui est <message>.
					final Node child = defaultMessages.item(i);
					if(child.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					this.defaultMessages.add(child.getTextContent());
				}
			}
			else {
				result.addInvalidParameters(TAGS[9]);
			}
			
			final String customLanguage = getObject(root, TAGS[11], String.class);
			if(customLanguage == null) {
				result.addInvalidParameters(TAGS[11]);
			}
			else {
				this.customLanguage = customLanguage;
			}
			
			if(result.getInvalidParameters().length != 0) {
				super.write(file);
			}
			return result;
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			result.addInvalidParameters(ex.getClass().getName());
		}
		return result;
	}
	
	@Override
	public final String toXML() {
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.newDocument();
			final Element root = document.createElement(TAGS[0]); // On créé le noeud principal du fichier XML.
			document.appendChild(root); // On l'ajoute.
			final Node roomDir = document.createElement(TAGS[1]);
			roomDir.appendChild(document.createTextNode(this.roomDir));
			final Node uuid = document.createElement(TAGS[2]);
			uuid.appendChild(document.createTextNode(this.uuid));
			final Node addSample = document.createElement(TAGS[3]);
			addSample.appendChild(document.createTextNode(String.valueOf(this.addSample)));
			final Node refreshInterval = document.createElement(TAGS[4]);
			refreshInterval.appendChild(document.createTextNode(String.valueOf(this.refreshInterval)));
			final Node timeOut = document.createElement(TAGS[5]);
			timeOut.appendChild(document.createTextNode(String.valueOf(this.timeOut)));
			final Node thumbnail = document.createElement(TAGS[6]);
			final Node thumbnailHeight = document.createElement(TAGS[7]);
			thumbnailHeight.appendChild(document.createTextNode(String.valueOf(this.thumbnailHeight)));
			thumbnail.appendChild(thumbnailHeight);
			final Node thumbnailWidth = document.createElement(TAGS[8]);
			thumbnailWidth.appendChild(document.createTextNode(String.valueOf(this.thumbnailWidth)));
			thumbnail.appendChild(thumbnailWidth);
			final Node defaultMessages = document.createElement(TAGS[9]);
			for(final String message : this.defaultMessages) { // On parse chaque message.
				final Node node = document.createElement(TAGS[10]); // On créé l'élément <message>.
				node.appendChild(document.createTextNode(message));
				defaultMessages.appendChild(node);
			}
			final Node customLanguage = document.createElement(TAGS[11]);
			customLanguage.appendChild(document.createTextNode(this.customLanguage));
			root.appendChild(roomDir); // Et on ajoute les différents noeuds au noeud principal.
			root.appendChild(uuid);
			root.appendChild(addSample);
			root.appendChild(refreshInterval);
			root.appendChild(timeOut);
			root.appendChild(thumbnail);
			root.appendChild(defaultMessages);
			root.appendChild(customLanguage);
			final Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // On indent le fichier XML (plus joli).
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); // Deux espaces par noeud.
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); // On souhaite du XML.
			transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.displayName()); // On souhaite de l'UTF-8.
			final StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString(); // Et on retourne le XML.
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
}