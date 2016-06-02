package fr.isn.bbq.eleve;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

import fr.isn.bbq.eleve.utils.XMLSettings;

/**
 * Représente les paramètres de configuration de l'application.
 */

public class AppSettings extends XMLSettings {
	
	private static final String[] TAGS = new String[]{ // En cas de mise à jour de la configuration, il faut penser à ajouter la balise ici (en plus du reste) :
		"configuration",	// TAGS[0]
		"ip",				// TAGS[1]
		"backlog",			// TAGS[2]
		"port",				// TAGS[3]
		"time-out"	,		// TAGS[4]
		"show-tray-icon",	// TAGS[5]
		"uuids",			// TAGS[6]
		"uuid",				// TAGS[7]
		"image-type",		// TAGS[8]
		"thumbnail",		// TAGS[9]
		"height",			// TAGS[10]
		"width"				// TAGS[11]
	};
	
	public String ip = "192.168.0.1"; // L'IP de cet ordinateur.
	public int backlog = 50; // Le nombre de clients simultanés supportés.
	public int port = 4444; // Le port de connexion au logiciel élève.
	public int timeOut = 10; // Le temps imparti pour que la socket se connecte.
	public boolean showTrayIcon = true; // Permet d'afficher une icône dans la barre d'outils.
	public List<String> uuids = new ArrayList<String>(Arrays.asList(
		UUID.randomUUID().toString()
	)); // Liste des UUIDs autorisés.
	public String imageType = "JPG";
	public int thumbnailHeight = 100; // Hauteur de la miniature.
	public int thumbnailWidth = 100; // Largeur de la miniature.
	
	@Override
	public final XMLError load(final File file) {
		final XMLError result = new XMLError();
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.parse(new InputSource(new StringReader(new String(Files.readAllBytes(file.toPath()))))); // On parse le contenu XML.
			final Element root = document.getDocumentElement();
			
			final String ip = getObject(root, TAGS[1], String.class); // On récupère le paramètre.
			if(ip == null) {
				result.addInvalidParameters(TAGS[1]);
			}
			else {
				this.ip = ip;
			}
			
			final Integer backlog = getObject(root, TAGS[2], Integer.class);
			if(backlog == null) {
				result.addInvalidParameters(TAGS[2]);
			}
			else {
				this.backlog = backlog;
			}
			
			final Integer port = getObject(root, TAGS[3], Integer.class);
			if(port == null) {
				result.addInvalidParameters(TAGS[3]);
			}
			else {
				this.port = port;
			}
			
			final Integer timeOut = getObject(root, TAGS[4], Integer.class);
			if(timeOut == null) {
				result.addInvalidParameters(TAGS[4]);
			}
			else {
				this.timeOut = timeOut;
			}
			
			final Boolean showTrayIcon = getObject(root, TAGS[5], Boolean.class);
			if(showTrayIcon == null) {
				result.addInvalidParameters(TAGS[5]);
			}
			else {
				this.showTrayIcon = showTrayIcon;
			}
			
			if(elementContains(root, TAGS[6])) {
				uuids.clear(); // On enlève tous les éléments qui sont déjà dans la liste des uuids.
				final NodeList uuids = ((Element)root.getElementsByTagName(TAGS[6]).item(0)).getElementsByTagName(TAGS[7]);
				for(int i = 0; i != uuids.getLength(); i++) { // On parse chaque élément du noeud <uuids> qui est <uuid>.
					final Node child = uuids.item(i);
					if(child.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					this.uuids.add(child.getTextContent());
				}
			}
			else {
				result.addInvalidParameters(TAGS[6]);
			}
			
			final String imageType = getObject(root, TAGS[8], String.class);
			if(imageType == null) {
				result.addInvalidParameters(TAGS[8]);
			}
			else {
				this.imageType = imageType;
			}
			
			if(elementContains(root, TAGS[9])) {
				final Element thumbnail = (Element)root.getElementsByTagName(TAGS[9]).item(0);
				
				final Integer thumbnailHeight = getObject(thumbnail, TAGS[10], Integer.class);
				final Integer thumbnailWidth = getObject(thumbnail, TAGS[11], Integer.class);
				if(thumbnailHeight != null && thumbnailWidth != null) {
					this.thumbnailHeight = thumbnailHeight;
					this.thumbnailWidth = thumbnailWidth;
				}
				else {
					result.addInvalidParameters(TAGS[10], TAGS[11]);
				}
			}
			else {
				result.addInvalidParameters(TAGS[9]);
			}
			
			if(result.getInvalidParameters().length != 0) {
				super.write(file);
			}
			return result;
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	@Override
	public final String toXML() {
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.newDocument();
			final Element root = document.createElement(TAGS[0]); // On créé le noeud principal du fichier XML.
			document.appendChild(root);
			final Node ip = document.createElement(TAGS[1]);
			ip.appendChild(document.createTextNode(this.ip));
			final Node backlog = document.createElement(TAGS[2]);
			backlog.appendChild(document.createTextNode(String.valueOf(this.backlog)));
			final Node port = document.createElement(TAGS[3]);
			port.appendChild(document.createTextNode(String.valueOf(this.port)));
			final Node timeOut = document.createElement(TAGS[4]);
			timeOut.appendChild(document.createTextNode(String.valueOf(this.timeOut)));
			final Node showTrayIcon = document.createElement(TAGS[5]);
			showTrayIcon.appendChild(document.createTextNode(String.valueOf(this.showTrayIcon)));
			final Node uuids = document.createElement(TAGS[6]);
			for(final String uuid : this.uuids) { // On parse chaque UUID.
				final Node node = document.createElement(TAGS[7]); // On créé l'élément <uuid>.
				node.appendChild(document.createTextNode(uuid));
				uuids.appendChild(node);
			}
			final Node imageType = document.createElement(TAGS[8]);
			imageType.appendChild(document.createTextNode(String.valueOf(this.imageType)));
			final Node thumbnail = document.createElement(TAGS[9]);
			final Node thumbnailHeight = document.createElement(TAGS[10]);
			thumbnailHeight.appendChild(document.createTextNode(String.valueOf(this.thumbnailHeight)));
			thumbnail.appendChild(thumbnailHeight);
			final Node thumbnailWidth = document.createElement(TAGS[11]);
			thumbnailWidth.appendChild(document.createTextNode(String.valueOf(this.thumbnailWidth)));
			thumbnail.appendChild(thumbnailWidth);
			root.appendChild(ip);
			root.appendChild(backlog);
			root.appendChild(port);
			root.appendChild(timeOut);
			root.appendChild(showTrayIcon);
			root.appendChild(uuids);
			root.appendChild(imageType);
			root.appendChild(thumbnail);
			final Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // On indent le fichier XML (plus joli).
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); // Deux espaces par noeud.
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); // On souhaite du XML.
			transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.displayName()); // On souhaite de l'UTF-8.
			final StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
}