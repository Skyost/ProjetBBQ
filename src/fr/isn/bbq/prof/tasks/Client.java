package fr.isn.bbq.prof.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.utils.Request;
import fr.isn.bbq.prof.utils.Utils;

/**
 * Le client utilisé pour formuler des requêtes aux logiciels élèves.
 */

public class Client extends Thread {
	
	/**
	 * La taille d'une miniature d'un ordinateur (carrée, en pixels).
	 */
	
	public static final short THUMBNAIL_SIZE = 100;
	
	/**
	 * Si le serveur est en fonctionnement.
	 */
	
	private boolean running = false;
	
	/**
	 * Le parent auquel on revoie des données.
	 */
	
	private final ClientInterface parent;
	
	/**
	 * La requête formulée aux logiciels élèves.
	 */
	
	private final Request request;
	
	/**
	 * Les logiciels élèves représentés par des objets "Computer".
	 */
	
	private final Computer[] computers;
	
	/**
	 * Si on ne souhaite pas répéter la requête.
	 */
	
	private final boolean oneRequest;
	
	/**
	 * Création d'un nouveau client.
	 * 
	 * @param parent Le parent (pour renvoyer des réponses).
	 * @param request La requête.
	 * @param computers Les ordinateurs auxquels il faut envoyer une requête.
	 */
	
	public Client(final ClientInterface parent, final Request request, final Computer... computers) {
		this(parent, request, computers, false);
	}
	
	/**
	 * Création d'un nouveau client.
	 * 
	 * @param parent Le parent (pour renvoyer des réponses).
	 * @param request La requête.
	 * @param computers Les ordinateurs auxquels il faut envoyer une requête.
	 * @param oneRequest Si il ne faut pas répéter la requête.
	 */
	
	public Client(final ClientInterface parent, final Request request, final Computer[] computers, final boolean oneRequest) {
		this.parent = parent;
		this.request = request;
		this.computers = computers;
		this.oneRequest = oneRequest;
	}
	
	@Override
	public final void run() {
		running = true;
		while(running) { // Tant que le serveur est démarré, on effectue les opérations suivantes.
			final List<Computer> joinedComputers = new ArrayList<Computer>();
			for(final Computer computer : computers) { // Pour chaque ordinateur on créé un nouveau Thread pour envoyer la requête.
				new Thread() {
					
					@Override
					public final void run() {
						try {
							parent.connection(computer, System.currentTimeMillis()); // On notifie le parent de la connexion.
							System.out.println("Connexion à l'ordinateur " + computer.name + " (" + computer.ip + ") sur le port " + computer.port + "...");
							final SSLSocket client = (SSLSocket)SSLSocketFactory.getDefault().createSocket();
							client.setEnabledCipherSuites(getEnabledCipherSuites(client));
							client.connect(new InetSocketAddress(computer.ip, computer.port), ProjetBBQProf.settings.timeOut * 1000); // On se connecte au poste élève.
							if(!running) { // Si le client n'est plus en fonctionnement, on interrompt tout.
								parent.onInterrupted(computer, System.currentTimeMillis());
								client.close();
								return;
							}
							System.out.println("Connexion réussie à " + client.getRemoteSocketAddress() + ".");
							System.out.println("Envoi de la requête \"" + request + "\"...");
							final DataOutputStream output = new DataOutputStream(client.getOutputStream());
							output.writeUTF(request.toString()); // On prépare l'envoi.
							output.flush(); // On envoie la requête.
							if(!running) { // Si le client n'est plus en fonctionnement, on interrompt tout.
								parent.onInterrupted(computer, System.currentTimeMillis());
								client.close();
								return;
							}
							final DataInputStream input = new DataInputStream(client.getInputStream());
							final String response = input.readUTF(); // On récupère le contenu de la réponse.
							System.out.println("R�ponse du server \"" + response + "\"."); // in.readUTF() permet d'obtenir la réponse du serveur.
							if(running) { // Si le client n'est plus en fonctionnement, on interrompt tout.
								final String[] parts = response.split(" "); // On sépare la réponse UTF à l'espace.
								if(parts[0].equals("0")) { // Si la première partie est 0 (soit valide) alors, on renvoi un succès.
									switch(request.getType()) { // En fonction de ce que l'on a demandé on execute ou non une action.
									case THUMBNAIL:
									case FULL_SCREENSHOT:
										parent.onSuccess(computer, ImageIO.read(client.getInputStream()), parts[1], Long.valueOf(parts[parts.length - 1]));
										break;
									default:
										parent.onSuccess(computer, true, parts[1], Long.valueOf(parts[parts.length - 1]));
										break;
									}
								}
								else {
									parent.onError(computer, new Exception(Utils.join(" ", Arrays.copyOfRange(parts, 1, parts.length - 1))), Long.valueOf(parts[parts.length - 1]));
								}
							}
							else {
								parent.onInterrupted(computer, System.currentTimeMillis());
							}
							System.out.println("Fermeture du client...");
							client.close();
						}
						catch(final Exception ex) {
							parent.onError(computer, ex, System.currentTimeMillis());
						}
						joinedComputers.add(computer); // On ajoute l'ordinateur aux ordinateurs joints.
					}
					
				}.start();
			}
			try {
				if(oneRequest) {
					System.out.println();
					return;
				}
				System.out.println("Attente des ordinateurs...");
				while(computers.length != joinedComputers.size()) { // Tant que tous les ordinateurs n'ont pas tous été joints.
					if(!running) {
						return;
					}
					Thread.sleep(1000);
				}
				System.out.println("Attente de " + ProjetBBQProf.settings.refreshInterval + " sec...");
				parent.onWaiting();
				Thread.sleep(ProjetBBQProf.settings.refreshInterval * 1000); // Le client se connecte à chaque serveur toutes les 5 secondes.
				System.out.println();
			}
			catch(final InterruptedException ex) {}
		}
	}
	
	/**
	 * Permet de vérifier si le client est toujours en train de traiter des données ou non.
	 * 
	 * @return <b>true</b> Oui.
	 * <br><b>false</b> Non.
	 */
	
	public final boolean isRunning() {
		return running;
	}
	
	/**
	 * Permet d'interrompre l'envoi de requêtes et des divers traitements.
	 */
	
	public final void stopRequests() {
		running = false;
	}
	
	/**
	 * Permet d'obtenir les cipher suites disponibles.
	 * 
	 * @param server Le serveur.
	 * 
	 * @return Les cipher suites disponibles.
	 */
	
	private static final String[] getEnabledCipherSuites(final SSLSocket server) {
		final List<String> suites = new ArrayList<String>();
		for(final String suite : server.getSupportedCipherSuites()) {
			if(!suite.toLowerCase().contains("_anon_")) {
				continue;
			}
			suites.add(suite);
		}
		return suites.toArray(new String[suites.size()]);
	}
	
	public interface ClientInterface {
		
		/**
		 * Appelé lorsque l'on tente de joindre un ordinateur.
		 * 
		 * @param computer L'ordinateur.
		 * @param time La date en millisecondes à laquelle cette action est exécutée.
		 */
		
		public void connection(final Computer computer, final long time);
		
		/**
		 * Appelé lorsque le logiciel élève renvoie une réponse.
		 * 
		 * @param computer L'ordinateur.
		 * @param returned La réponse (peut être parsée par le client) avec l'objet, le nom d'utilisateur et le temps de réponse en millisecondes.
		 */
		
		public void onSuccess(final Computer computer, final Object... returned);
		
		/**
		 * Appelé lorsqu'une erreur intervient lorsque l'on tente de joindre un ordinateur.
		 * 
		 * @param computer L'ordinateur.
		 * @param ex L'erreur.
		 * @param responseTime La date en millisecondes à laquelle on a tenté de joindre l'ordinateur.
		 */
		
		public void onError(final Computer computer, final Exception ex, final long responseTime);
		
		/**
		 * Appelé lorsque les requêtes ont été interrompues pour un ordinateur.
		 * 
		 * @param computer L'ordinateur.
		 * @param time La date en millisecondes à laquelle les requêtes ont été interrompues.
		 */
		
		public void onInterrupted(final Computer computer, final long time);
		
		/**
		 * Appelé lorsque l'on attend la durée définie dans le fichier de configuration XML.
		 */
		
		public void onWaiting();
		
	}

}