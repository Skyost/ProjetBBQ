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
	public List<Computer> computers;
	
	@Override
	public final boolean load(final String content) {
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.parse(new InputSource(new StringReader(content)));
			final Element root = document.getDocumentElement();
			name = root.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
			this.computers = new ArrayList<Computer>();
			final NodeList computers = root.getElementsByTagName("computers").item(0).getFirstChild().getChildNodes();
			for(int i = 0; i != computers.getLength(); i++) {
				final Computer computer = new Computer();
				final Element element = (Element)computers.item(i);
				computer.name = element.getElementsByTagName("name").item(0).getNodeValue();
				computer.ip = element.getElementsByTagName("ip").item(0).getNodeValue();
				this.computers.add(computer);
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
			final Element root = document.createElement("configuration");
			document.appendChild(root);
			final Node name = document.createElement("name");
			name.appendChild(document.createTextNode(this.name));
			final Node computers = document.createElement("computers");
			for(final Computer computer : this.computers) {
				final Node node = document.createElement("computer");
				final Node computerName = document.createElement("name");
				computerName.appendChild(document.createTextNode(computer.name));
				final Node computerIp = document.createElement("ip");
				computerIp.appendChild(document.createTextNode(computer.ip));
				node.appendChild(computerName);
				node.appendChild(computerIp);
				computers.appendChild(node);
			}
			root.appendChild(name);
			root.appendChild(computers);
			final Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.displayName());
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