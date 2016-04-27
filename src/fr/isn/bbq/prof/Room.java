package fr.isn.bbq.prof;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
 * Représente une salle de classe.
 */

public class Room implements XMLSettings {
	
	public String name;
	public final List<Computer> computers = new ArrayList<Computer>();
	
	@Override
	public final boolean load(final String content) {
		try {
			computers.clear(); // On enlève tous les éléments qui sont déjà dans la liste des ordinateurs.
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.parse(new InputSource(new StringReader(content))); // On parse le contenu XML.
			final Element root = document.getDocumentElement();
			name = root.getElementsByTagName("name").item(0).getFirstChild().getNodeValue(); // Le premier enfant du premier élément <name>.
			final NodeList computers = root.getElementsByTagName("computers").item(0).getChildNodes();
			for(int i = 0; i != computers.getLength(); i++) { // On parse chaque élément du noeud <computers>.
				final Node child = computers.item(i);
				if(child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				final Computer computer = new Computer(); // On créé une instance d'ordinateur "blanc".
				final Element element = (Element)child;
				computer.name = element.getElementsByTagName("name").item(0).getFirstChild().getNodeValue(); // On y ajoute les différents paramètres parsés.
				computer.ip = element.getElementsByTagName("ip").item(0).getFirstChild().getNodeValue();
				computer.port = Integer.valueOf(element.getElementsByTagName("port").item(0).getFirstChild().getNodeValue());
				this.computers.add(computer); // Et on ajoute l'ordinateur à la liste de la salle.
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
			final Node name = document.createElement("name");
			name.appendChild(document.createTextNode(this.name));
			final Node computers = document.createElement("computers");
			for(final Computer computer : this.computers) { // On parse chaque ordinateur.
				final Node node = document.createElement("computer"); // On créé l'élément <computer>.
				final Node computerName = document.createElement("name"); // On y ajoute les différents paramètres.
				computerName.appendChild(document.createTextNode(computer.name));
				final Node computerIp = document.createElement("ip");
				computerIp.appendChild(document.createTextNode(computer.ip));
				final Node computerPort = document.createElement("port");
				computerPort.appendChild(document.createTextNode(String.valueOf(computer.port)));
				node.appendChild(computerName); // On ajoute les différents paramètres à l'élément <computer>.
				node.appendChild(computerIp);
				node.appendChild(computerPort);
				computers.appendChild(node); // Et on ajoute cet élément au noeud <computers>.
			}
			root.appendChild(name);
			root.appendChild(computers);
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