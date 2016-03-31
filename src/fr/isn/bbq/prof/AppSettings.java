package fr.isn.bbq.prof;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

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
import org.xml.sax.InputSource;

import fr.isn.bbq.prof.utils.XMLSettings;

/**
 * Représente les paramètres de configuration de l'application.
 */

public class AppSettings implements XMLSettings {
	
	public String roomDir = "Salles"; // Le répertoire des salles.
	public String uuid; // L'UUID dont le logiciel a besoin pour se connecter aux postes.
	public boolean addSample = true; // Si on doit ajouter un fichier d'exemple ou non.
	public int refreshInterval = 5; // Le temps de rafraîchissement (en sec).
	
	@Override
	public final boolean load(final String content) {
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document document = builder.parse(new InputSource(new StringReader(content)));
			final Element root = document.getDocumentElement();
			roomDir = root.getElementsByTagName("room-directory").item(0).getFirstChild().getNodeValue();
			uuid = root.getElementsByTagName("uuid").item(0).getFirstChild().getNodeValue();
			addSample = Boolean.valueOf(root.getElementsByTagName("add-sample").item(0).getFirstChild().getNodeValue());
			refreshInterval = Integer.valueOf(root.getElementsByTagName("refresh-interval").item(0).getFirstChild().getNodeValue());
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
			final Node roomDir = document.createElement("room-directory");
			roomDir.appendChild(document.createTextNode(this.roomDir));
			final Node uuid = document.createElement("uuid");
			uuid.appendChild(document.createTextNode(this.uuid));
			final Node addSample = document.createElement("add-sample");
			addSample.appendChild(document.createTextNode(String.valueOf(this.addSample)));
			final Node refreshInterval = document.createElement("refresh-interval");
			refreshInterval.appendChild(document.createTextNode(String.valueOf(this.refreshInterval)));
			root.appendChild(roomDir);
			root.appendChild(uuid);
			root.appendChild(addSample);
			root.appendChild(refreshInterval);
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