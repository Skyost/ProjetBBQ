package fr.isn.bbq.prof;


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

/*
 * TODO: Commenter cette classe.
 */

public class AppSettings {
	
	@SerializationOptions(name = "test")
	public String test = "Bonjour";
	
	private transient File file;
	public static final transient short RECENTS_LIMIT = 10;
	
	public AppSettings(final File file) {
		this.file = file;
	}
	
	public final File getFile() {
		return file;
	}
	
	public final void setFile(final File file) {
		this.file = file;
	}
	
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
	
	private final SerializationOptions getAnnotation(final Field field) {
		if(Modifier.isTransient(field.getModifiers())) {
			return null;
		}
		final SerializationOptions options = field.getAnnotation(SerializationOptions.class);
		return options;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface SerializationOptions {
		
		public String name();
		
	}
	
}