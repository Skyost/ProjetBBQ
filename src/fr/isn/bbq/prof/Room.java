package fr.isn.bbq.prof;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

public class Room extends XMLSettings {
	
	private static final String[] TAGS = new String[]{ // En cas de mise à jour de la configuration, il faut penser à ajouter la balise ici (en plus du reste) :
		"configuration",	// TAGS[0]
		"name",				// TAGS[1]
		"computers",		// TAGS[2]
		"computer",			// TAGS[3]
		"name",				// TAGS[4]
		"ip",				// TAGS[5]
		"port",				// TAGS[5]
	};
	
	public String name; // Le nom de la salle.
	public final List<Computer> computers = new ArrayList<Computer>(); // Les ordinateurs qui composent la salle.
	
	@Override
	public final XMLError load(final File file) {
		final XMLError result = new XMLError();
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.parse(new InputSource(new StringReader(new String(Files.readAllBytes(file.toPath()))))); // On parse le contenu XML.
			final Element root = document.getDocumentElement();
			
			final String name = getObject(root, TAGS[1], String.class); // On récupère le paramètre.
			if(name == null) {
				result.addInvalidParameters(TAGS[1]);
			}
			else {
				this.name = name;
			}
			
			if(elementContains(root, TAGS[2])) {
				computers.clear(); // On enlève tous les éléments qui sont déjà dans la liste des ordinateurs.
				final NodeList computers = root.getElementsByTagName(TAGS[2]).item(0).getChildNodes();
				for(int i = 0; i != computers.getLength(); i++) { // On parse chaque élément du noeud <computers>.
					final Node child = computers.item(i);
					if(child.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					final Computer computer = new Computer(); // On créé une instance d'ordinateur "blanc".
					final Element element = (Element)child;
					
					computer.name = getObject(element, TAGS[4], String.class); // On y ajoute les différents paramètres parsés.
					if(computer.name == null) {
						result.addInvalidParameters("Ordinateur(" + i + ") : " + TAGS[4]);
					}
					
					computer.ip = getObject(element, TAGS[5], String.class);
					if(computer.ip == null) {
						result.addInvalidParameters("Ordinateur(" + i + ") : " + TAGS[5]);
					}
					
					computer.port = getObject(element, TAGS[6], Integer.class);
					if(computer.port == null) {
						result.addInvalidParameters("Ordinateur(" + i + ") : " + TAGS[6]);
					}
					
					this.computers.add(computer); // Et on ajoute l'ordinateur à la liste de la salle.
				}
			}
			else {
				result.addInvalidParameters(TAGS[2]);
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
			document.appendChild(root);
			final Node name = document.createElement(TAGS[1]);
			name.appendChild(document.createTextNode(this.name));
			final Node computers = document.createElement(TAGS[2]);
			for(final Computer computer : this.computers) { // On parse chaque ordinateur.
				final Node node = document.createElement(TAGS[3]); // On créé l'élément <computer>.
				final Node computerName = document.createElement(TAGS[4]); // On y ajoute les différents paramètres.
				computerName.appendChild(document.createTextNode(computer.name));
				final Node computerIp = document.createElement(TAGS[5]);
				computerIp.appendChild(document.createTextNode(computer.ip));
				final Node computerPort = document.createElement(TAGS[6]);
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