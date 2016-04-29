package fr.isn.bbq.eleve;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import fr.isn.bbq.eleve.tasks.HandleClient;
import fr.isn.bbq.eleve.utils.Utils;

public class ProjetBBQEleve {
	
	/*
	* Variables globales du projet.
	*/
	
	public static final String APP_NAME = "Projet BBQ";
	public static final String APP_VERSION = "0.1";
	
	public static AppSettings settings;
	
	/**
	* Première méthode exécutée par le programme. TODO: Commenter le code.
	* 
	* @param args Arguments à passer.
	*/
	
	public static final void main(final String[] args) {
		ServerSocket server = null;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			final File settings = new File(Utils.getParentFolder(), "settings.xml");
			ProjetBBQEleve.settings = new AppSettings();
			if(!settings.exists()) {
				Files.write(settings.toPath(), ProjetBBQEleve.settings.toXML().getBytes());
			}
			else {
				ProjetBBQEleve.settings.load(new String(Files.readAllBytes(settings.toPath())));
			}
			server = new ServerSocket(ProjetBBQEleve.settings.port, 50, InetAddress.getByName(ProjetBBQEleve.settings.ip));
			server.setSoTimeout(ProjetBBQEleve.settings.timeOut * 1000);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Impossible de démarrer " + APP_NAME + " en tâche de fond !", "Erreur !", JOptionPane.ERROR_MESSAGE);
		}
		if(server == null) {
			return;
		}
		while(true) {
			try {
				System.out.println("En attente de client sur " + server.getInetAddress().getHostAddress() + ":" + server.getLocalPort() + "...");
				final Socket client = server.accept();
				new HandleClient(client).start();
			}
			catch(final SocketTimeoutException timeOut) {
				continue;
			}
			catch(final Exception ex) {
				ex.printStackTrace();
				break;
			}
		}
		try {
			server.close();
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
}