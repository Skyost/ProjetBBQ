package fr.isn.bbq.eleve;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
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

public class AppSettings implements XMLSettings {
	
	public String ip = "192.168.0.1"; // L'IP de cet ordinateur.
	public int backlog = 50; // Le nombre de clients simultanés supportés.
	public int port = 4444; // Le port de connexion au logiciel élève.
	public int timeOut = 10; // Le temps imparti pour que la socket se connecte.
	public List<String> uuids = new ArrayList<String>(Arrays.asList(
		UUID.randomUUID().toString()
	)); // Liste des UUIDs autorisés.
	public String imageType = "JPG";
	public int thumbnailHeight = 100; // Hauteur de la miniature.
	public int thumbnailWidth = 100; // Largeur de la miniature.
	
	@Override
	public final boolean load(final String content) {
		try {
			uuids.clear(); // On enlève tous les éléments qui sont déjà dans la liste des uuids.
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.parse(new InputSource(new StringReader(content))); // On parse le contenu XML.
			final Element root = document.getDocumentElement();
			ip = root.getElementsByTagName("ip").item(0).getFirstChild().getNodeValue(); // Le premier enfant du premier élément <ip>.
			backlog = Integer.valueOf(root.getElementsByTagName("backlog").item(0).getFirstChild().getNodeValue());
			port = Integer.valueOf(root.getElementsByTagName("port").item(0).getFirstChild().getNodeValue());
			timeOut = Integer.valueOf(root.getElementsByTagName("time-out").item(0).getFirstChild().getNodeValue());
			final NodeList uuids = ((Element)root.getElementsByTagName("uuids").item(0)).getElementsByTagName("uuid");
			for(int i = 0; i != uuids.getLength(); i++) { // On parse chaque élément du noeud <uuids> qui est <uuid>.
				final Node child = uuids.item(i);
				if(child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				this.uuids.add(child.getTextContent());
			}
			imageType = root.getElementsByTagName("image-type").item(0).getFirstChild().getNodeValue();
			final Element thumbnail = (Element)root.getElementsByTagName("thumbnail").item(0);
			thumbnailHeight = Integer.valueOf(thumbnail.getElementsByTagName("height").item(0).getFirstChild().getNodeValue());
			thumbnailWidth = Integer.valueOf(thumbnail.getElementsByTagName("width").item(0).getFirstChild().getNodeValue());
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
			document.appendChild(root);
			final Node ip = document.createElement("ip");
			ip.appendChild(document.createTextNode(this.ip));
			final Node backlog = document.createElement("backlog");
			backlog.appendChild(document.createTextNode(String.valueOf(this.backlog)));
			final Node port = document.createElement("port");
			port.appendChild(document.createTextNode(String.valueOf(this.port)));
			final Node timeOut = document.createElement("time-out");
			timeOut.appendChild(document.createTextNode(String.valueOf(this.timeOut)));
			final Node uuids = document.createElement("uuids");
			for(final String uuid : this.uuids) { // On parse chaque UUID.
				final Node node = document.createElement("uuid"); // On créé l'élément <uuid>.
				node.appendChild(document.createTextNode(uuid));
				uuids.appendChild(node);
			}
			final Node imageType = document.createElement("image-type");
			imageType.appendChild(document.createTextNode(String.valueOf(this.imageType)));
			final Node thumbnail = document.createElement("thumbnail");
			final Node thumbnailHeight = document.createElement("height");
			thumbnailHeight.appendChild(document.createTextNode(String.valueOf(this.thumbnailHeight)));
			thumbnail.appendChild(thumbnailHeight);
			final Node thumbnailWidth = document.createElement("width");
			thumbnailWidth.appendChild(document.createTextNode(String.valueOf(this.thumbnailWidth)));
			thumbnail.appendChild(thumbnailWidth);
			root.appendChild(ip);
			root.appendChild(backlog);
			root.appendChild(port);
			root.appendChild(timeOut);
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