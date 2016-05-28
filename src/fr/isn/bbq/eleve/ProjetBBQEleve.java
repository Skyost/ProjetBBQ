package fr.isn.bbq.eleve;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import fr.isn.bbq.eleve.tasks.HandleClient;
import fr.isn.bbq.eleve.utils.Utils;

public class ProjetBBQEleve {
	
	/*
	* Variables globales du projet.
	*/
	
	public static final String APP_NAME = "Projet BBQ";
	public static final String APP_VERSION = "0.1.1";
	
	public static AppSettings settings;
	
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
			if(!settings.exists()) { // Si il n'existe pas, on le créé et on applique des paramètres par défaut puis on quitte le programme.
				Files.write(settings.toPath(), ProjetBBQEleve.settings.toXML().getBytes());
				JOptionPane.showMessageDialog(null, "<html>Les paramètres XML ont été enregistré ici :<br>" + settings.getPath() + "<br>Veuillez les modifier avant de démarrer l'application.</html>");
				System.exit(0);
			}
			else { // Sinon on le charge.
				ProjetBBQEleve.settings.load(new String(Files.readAllBytes(settings.toPath())));
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
	
}