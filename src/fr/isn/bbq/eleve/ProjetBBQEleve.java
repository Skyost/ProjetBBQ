package fr.isn.bbq.eleve;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import fr.isn.bbq.eleve.tasks.HandleClient;
import fr.isn.bbq.eleve.utils.Utils;

public class ProjetBBQEleve {
	
	/*
	* Constantes globales du projet :
	*/
	
	public static final String APP_NAME = "Projet BBQ";
	public static final String APP_VERSION = "0.1.2";
	
	/*
	 * Certaines variables comme les paramètres, la liste d'icônes (16, 32, 64, 128, 256, 512) :
	 */
	
	public static AppSettings settings;
	public static final List<Image> icons = new ArrayList<Image>();
	
	/**
	* Première méthode exécutée par le programme.
	* 
	* @param args Arguments à passer.
	*/
	
	public static final void main(final String[] args) {
		SSLServerSocket server = null;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Le style par défaut de l'application.
			final File settings = new File(Utils.getParentFolder(), "settings.xml"); // Le fichier de paramètres XML.
			ProjetBBQEleve.settings = new AppSettings();
			if(settings.exists()) { // Si les paramètres existent, on les charge.
				ProjetBBQEleve.settings.load(settings);
			}
			else { // Sinon on le créé et on applique des paramètres par défaut puis on quitte le programme.
				ProjetBBQEleve.settings.write(settings);
				JOptionPane.showMessageDialog(null, "<html>Les paramètres XML ont été enregistré ici :<br>" + settings.getPath() + "<br>Veuillez les modifier avant de démarrer l'application.</html>");
				System.exit(0);
			}
			loadIcons();
			if(ProjetBBQEleve.settings.showTrayIcon) {
				createTrayIcon();
			}
			server = (SSLServerSocket)SSLServerSocketFactory.getDefault().createServerSocket(ProjetBBQEleve.settings.port, ProjetBBQEleve.settings.backlog, InetAddress.getByName(ProjetBBQEleve.settings.ip)); // On créé le serveur en fonction des paramètres XML.
			server.setSoTimeout(ProjetBBQEleve.settings.timeOut * 1000); // Et on change le timeout.
			server.setEnabledCipherSuites(getEnabledCipherSuites(server)); // On ajoute les types d'encryptions supportés par le serveur (doivent être anonymes).
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "<html>Impossible de démarrer " + APP_NAME + " en tâche de fond !<br/>Raison : " + ex.getMessage() + "</html>", "Erreur !", JOptionPane.ERROR_MESSAGE);
		}
		if(server == null) { // Si il y a une erreur durant l'initialisation de l'application, on s'en va.
			return;
		}
		boolean waitingMessageDisplayed = false; // On n'affiche le message qu'une fois.
		while(true) { // Le serveur boucle infiniment.
			try {
				if(!waitingMessageDisplayed) {
					System.out.println("En attente de client sur " + server.getInetAddress().getHostAddress() + ":" + server.getLocalPort() + "...");
					waitingMessageDisplayed = true;
				}
				final Socket client = server.accept(); // On attend pendant la durée de timeout si un client se connecte.
				new HandleClient(client).start(); // Un thread par client.
			}
			catch(final SocketTimeoutException timeOut) {
				continue; // Si il n'y a personne : nouveau tour.
			}
			catch(final Exception ex) {
				ex.printStackTrace();
				break;
			}
		}
		try {
			server.close(); // On ferme le serveur de sockets.
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Permet d'obtenir les cipher suites disponibles.
	 * 
	 * @param server Le serveur.
	 * 
	 * @return Les cipher suites disponibles.
	 */
	
	private static final String[] getEnabledCipherSuites(final SSLServerSocket server) {
		final List<String> suites = new ArrayList<String>();
		for(final String suite : server.getSupportedCipherSuites()) {
			if(!suite.toLowerCase().contains("_anon_")) {
				continue;
			}
			suites.add(suite);
		}
		return suites.toArray(new String[suites.size()]);
	}
	
	/**
	 * Permet de créer une icône dans la barre d'outils.
	 * 
	 * @throws AWTException Si une exception se produit.
	 */
	
	private static final void createTrayIcon() throws AWTException {
		if(!SystemTray.isSupported()) {
			return;
		}
		final TrayIcon icon = new TrayIcon(icons.get(0), APP_NAME + " v" + APP_VERSION + " - Activé");
		icon.setImageAutoSize(true);
		SystemTray.getSystemTray().add(icon);
	}
	
	/**
	 * Chargement des icônes.
	 */
	
	private static final void loadIcons() {
		final Image icon = Toolkit.getDefaultToolkit().getImage(ProjetBBQEleve.class.getResource("/fr/isn/bbq/eleve/res/app_icon.png"));
		icons.addAll(Arrays.asList(
			icon.getScaledInstance(16, 16, Image.SCALE_SMOOTH),
			icon.getScaledInstance(32, 32, Image.SCALE_SMOOTH),
			icon.getScaledInstance(64, 64, Image.SCALE_SMOOTH),
			icon.getScaledInstance(128, 128, Image.SCALE_SMOOTH),
			icon.getScaledInstance(256, 256, Image.SCALE_SMOOTH),
			icon/*.getScaledInstance(512, 512, Image.SCALE_SMOOTH) // L'icône est déjà en 512x512 */
		));
	}
	
}