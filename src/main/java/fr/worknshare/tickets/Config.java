package fr.worknshare.tickets;

import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Singleton class for config storage
 * @author Jérémy LAMBERT
 *
 */
public final class Config {

	private static Config instance;

	private Hashtable<String, String> values;

	private Config() {
		load();
	}

	private void load() {
		Logger.getGlobal().info("Loading config");

		values = new Hashtable<>();

		ClassLoader classLoader = TicketsApplication.class.getClassLoader();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xml = builder.parse(classLoader.getResourceAsStream("config.xml"));
			Element root = xml.getDocumentElement();

			Node node = root.getFirstChild();
			while(node != null) {
				values.put(node.getNodeName(), node.getTextContent());
				node = node.getNextSibling();
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't load config", e);
		}

		Logger.getGlobal().info("Loaded config.");
		Logger.getGlobal().info("Current environment: " + get("Environment"));
		Logger.getGlobal().info("Remote host: " + get("Host"));
	}

	/**
	 * Get a value from the config
	 * @param key
	 * @return the value associated with the given key, null if not found
	 */
	public String get(String key) {
		return values.get(key);
	}

	public static final Config getInstance() {
		if(instance == null) 
			instance = new Config();
		return instance;
	}
}
