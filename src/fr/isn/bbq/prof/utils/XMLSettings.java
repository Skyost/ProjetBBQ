package fr.isn.bbq.prof.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Cr�ation de param�tres en XML.
 * <br/>Utilis� pour les salles, les param�tres de l'application, ...
 */

public abstract class XMLSettings {
	
	private transient File file;
	
	/**
	 * Cr�ation d'une nouvelle instance de la classe.
	 * 
	 * @param file Le fichier � charger (ceci ne se fait pas automatiquement, il faut appeler la m�thode <b>load()</b>).
	 */
	
	public XMLSettings(final File file) {
		this.file = file;
	}
	
	/**
	 * Retourne le fichier qui est utilis� par l'instance.
	 * 
	 * @return Le fichier utilis� par l'instance.
	 */
	
	public final File getFile() {
		return file;
	}
	
	/**
	 * Modifie le fichier utilis� par l'instance.
	 * 
	 * @param file Le nouveau fichier � utiliser.
	 */
	
	public final void setFile(final File file) {
		this.file = file;
	}
	
	/**
	 * Chargement des param�tres du fichier. Si certains ne sont pas pr�sents, ils sont cr��s avec leur valeur par d�faut.
	 * <br/>Divers probl�mes (exceptions) peuvent se produire.
	 */
	
	public final void load() throws SAXException, IOException, ParserConfigurationException, IllegalArgumentException, IllegalAccessException, DOMException, TransformerFactoryConfigurationError, TransformerException {
		if(!file.exists()) {
			this.save();
			return;
		}
		final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		final Document document = builder.parse(file);
		final Element root = document.getDocumentElement();
		boolean needToSave = false;
		for(final Field field : this.getClass().getFields()) {
			final SerializationOptions options = this.getAnnotation(field);
			if(options == null) {
				continue;
			}
			final Class<?> type = field.getType();
			final Node value = root.getElementsByTagName(options.name()).item(0).getFirstChild();
			if(value == null) {
				needToSave = true;
				continue;
			}
			if(type.equals(boolean.class)) {
				field.set(this, Boolean.valueOf(value.getNodeValue()));
				continue;
			}
			if(type.equals(String.class)) {
				field.set(this, value.getNodeValue());
				continue;
			}
			if(type.equals(int.class)) {
				field.set(this, Integer.valueOf(value.getNodeValue()));
				continue;
			}
			if(type.equals(List.class)) {
				final List<String> list = new ArrayList<String>();
				final NodeList children = value.getChildNodes();
				for(int i = 0; i != children.getLength(); i++) {
					list.add(children.item(i).getNodeValue());
				}
				field.set(this, list);
				continue;
			}
		}
		if(needToSave) {
			save();
		}
	}
	
	/**
	 * Enregistrement des param�tres dans le fichier.
	 * <br/>Divers probl�mes (exceptions) peuvent se produire.
	 */
	
	public final void save() throws ParserConfigurationException, DOMException, IllegalArgumentException, IllegalAccessException, TransformerFactoryConfigurationError, TransformerException {
		final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		final Document document = builder.newDocument();
		final Element root = document.createElement("configuration");
		document.appendChild(root);
		for(final Field field : this.getClass().getFields()) {
			final SerializationOptions options = this.getAnnotation(field);
			if(options == null) {
				continue;
			}
			final Class<?> type = field.getType();
			if(type.equals(boolean.class) || type.equals(int.class) || type.equals(String.class)) {
				final Node node = document.createElement(options.name());
				node.appendChild(document.createTextNode(field.get(this).toString()));
				root.appendChild(node);
				continue;
			}
			if(type.equals(List.class)) {
				final Node node = document.createElement(options.name());
				for(final Object item : (List<?>)field.get(this)) {
					final Node element = document.createElement("value");
					element.appendChild(document.createTextNode(item.toString()));
					node.appendChild(element);
				}
				root.appendChild(node);
				continue;
			}
		}
		if(file.exists()) {
			file.delete();
		}
		final Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.displayName());
		final DOMSource source = new DOMSource(document);
		transformer.transform(source, new StreamResult(file));
	}
	
	/**
	 * On obtient l'annotation <b>SerializationOptions</b> dans le but de pouvoir extraire divers param�tres.
	 * 
	 * @param field Le champ concern�.
	 * 
	 * @return L'annotation <b>SerializationOptions</b>.
	 */
	
	private final SerializationOptions getAnnotation(final Field field) {
		if(Modifier.isTransient(field.getModifiers())) {
			return null;
		}
		final SerializationOptions options = field.getAnnotation(SerializationOptions.class);
		return options;
	}
	
	/**
	 * Permet de passer divers param�tres pour s�rialiser le champ.
	 */
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	protected @interface SerializationOptions {
		
		public String name();
		
	}
	
}