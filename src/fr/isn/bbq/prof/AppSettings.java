package fr.isn.bbq.prof;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class AppSettings implements XMLSettings {
	
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
	
	@Override
	public final boolean load(final String content) {
		try {
			defaultMessages.clear(); // On enlève tous les éléments qui sont déjà dans la liste des messages par défaut.
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.parse(new InputSource(new StringReader(content))); // On parse le contenu XML.
			final Element root = document.getDocumentElement();
			roomDir = root.getElementsByTagName("room-directory").item(0).getFirstChild().getNodeValue(); // Le premier enfant du premier élément <room-directory>.
			uuid = root.getElementsByTagName("uuid").item(0).getFirstChild().getNodeValue();
			addSample = Boolean.valueOf(root.getElementsByTagName("add-sample").item(0).getFirstChild().getNodeValue());
			refreshInterval = Integer.valueOf(root.getElementsByTagName("refresh-interval").item(0).getFirstChild().getNodeValue());
			timeOut = Integer.valueOf(root.getElementsByTagName("time-out").item(0).getFirstChild().getNodeValue());
			final Element thumbnail = (Element)root.getElementsByTagName("thumbnail").item(0);
			thumbnailHeight = Integer.valueOf(thumbnail.getElementsByTagName("height").item(0).getFirstChild().getNodeValue());
			thumbnailWidth = Integer.valueOf(thumbnail.getElementsByTagName("width").item(0).getFirstChild().getNodeValue());
			final NodeList defaultMessages = ((Element)root.getElementsByTagName("default-messages").item(0)).getElementsByTagName("message");
			for(int i = 0; i != defaultMessages.getLength(); i++) { // On parse chaque élément du noeud <default-messages> qui est <message>.
				final Node child = defaultMessages.item(i);
				if(child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				this.defaultMessages.add(child.getTextContent());
			}
			return true;
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	@Override
	public final String toXML() {
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.newDocument();
			final Element root = document.createElement("configuration"); // On créé le noeud principal du fichier XML.
			document.appendChild(root); // On l'ajoute.
			final Node roomDir = document.createElement("room-directory");
			roomDir.appendChild(document.createTextNode(this.roomDir));
			final Node uuid = document.createElement("uuid");
			uuid.appendChild(document.createTextNode(this.uuid));
			final Node addSample = document.createElement("add-sample");
			addSample.appendChild(document.createTextNode(String.valueOf(this.addSample)));
			final Node refreshInterval = document.createElement("refresh-interval");
			refreshInterval.appendChild(document.createTextNode(String.valueOf(this.refreshInterval)));
			final Node timeOut = document.createElement("time-out");
			timeOut.appendChild(document.createTextNode(String.valueOf(this.timeOut)));
			final Node thumbnail = document.createElement("thumbnail");
			final Node thumbnailHeight = document.createElement("height");
			thumbnailHeight.appendChild(document.createTextNode(String.valueOf(this.thumbnailHeight)));
			thumbnail.appendChild(thumbnailHeight);
			final Node thumbnailWidth = document.createElement("width");
			thumbnailWidth.appendChild(document.createTextNode(String.valueOf(this.thumbnailWidth)));
			thumbnail.appendChild(thumbnailWidth);
			final Node defaultMessages = document.createElement("default-messages");
			for(final String message : this.defaultMessages) { // On parse chaque message.
				final Node node = document.createElement("message"); // On créé l'élément <message>.
				node.appendChild(document.createTextNode(message));
				defaultMessages.appendChild(node);
			}
			root.appendChild(roomDir); // Et on ajoute les différents noeuds au noeud principal.
			root.appendChild(uuid);
			root.appendChild(addSample);
			root.appendChild(refreshInterval);
			root.appendChild(timeOut);
			root.appendChild(thumbnail);
			root.appendChild(defaultMessages);
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