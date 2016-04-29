package fr.isn.bbq.eleve;

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

import fr.isn.bbq.eleve.utils.XMLSettings;

/**
 * Représente les paramètres de configuration de l'application.
 */

public class AppSettings implements XMLSettings {
	
	public String ip = "192.168.0.1"; // L'IP de cet ordinateur.
	public int port = 4444; // Le port de connexion au logiciel élève.
	public int timeOut = 10; // Le temps imparti pour que la socket se connecte.
	public List<String> uuids = new ArrayList<String>(Arrays.asList(
			"f03b4a82-1791-4b25-9e37-26e10d186c95"
	)); // Liste des UUIDs autorisés.
	
	@Override
	public final boolean load(final String content) {
		try {
			uuids.clear(); // On enlève tous les éléments qui sont déjà dans la liste des uuids.
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.parse(new InputSource(new StringReader(content))); // On parse le contenu XML.
			final Element root = document.getDocumentElement();
			ip = root.getElementsByTagName("ip").item(0).getFirstChild().getNodeValue(); // Le premier enfant du premier élément <ip>.
			port = Integer.valueOf(root.getElementsByTagName("port").item(0).getFirstChild().getNodeValue());
			timeOut = Integer.valueOf(root.getElementsByTagName("time-out").item(0).getFirstChild().getNodeValue());
			final NodeList uuids = ((Element)root.getElementsByTagName("uuids").item(0)).getElementsByTagName("uuid");
			for(int i = 0; i != uuids.getLength(); i++) { // On parse chaque élément du noeud <computers>.
				final Node child = uuids.item(i);
				if(child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				this.uuids.add(child.getTextContent());
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
			document.appendChild(root);
			final Node ip = document.createElement("ip");
			ip.appendChild(document.createTextNode(this.ip));
			final Node port = document.createElement("port");
			port.appendChild(document.createTextNode(String.valueOf(this.port)));
			final Node timeOut = document.createElement("time-out");
			timeOut.appendChild(document.createTextNode(String.valueOf(this.timeOut)));
			final Node uuids = document.createElement("uuids");
			for(final String uuid : this.uuids) { // On parse chaque UUID.
				final Node node = document.createElement("uuid"); // On créé l'élément <computer>.
				node.appendChild(document.createTextNode(uuid));
				uuids.appendChild(node);
			}
			root.appendChild(ip);
			root.appendChild(port);
			root.appendChild(timeOut);
			root.appendChild(uuids);
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