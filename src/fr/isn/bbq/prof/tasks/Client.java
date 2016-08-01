package fr.isn.bbq.prof.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import fr.isn.bbq.prof.utils.LanguageManager;
import fr.isn.bbq.prof.utils.Request;
import fr.isn.bbq.prof.utils.Utils;

/**
 * Le client utilisé pour formuler des requêtes aux logiciels élèves.
 */

public class Client extends Thread {
	
	/**
	 * La version du protocol utilisée pour communiquer avec le serveur.
	 */
	
	public static final short PROTOCOL_VERSION = 1;
	
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
							System.out.println(LanguageManager.getString("client.output.connection", computer.name, computer.ip, computer.port));
							final SSLSocket client = (SSLSocket)SSLSocketFactory.getDefault().createSocket(); // On créé le client.
							client.setEnabledCipherSuites(getEnabledCipherSuites(client)); // On ajoute les types d'encryptions supportés par le client (doivent être anonymes).
							client.connect(new InetSocketAddress(computer.ip, computer.port), ProjetBBQProf.settings.timeOut * 1000); // On se connecte au poste élève.
							if(!running) { // Si le client n'est plus en fonctionnement, on interrompt tout.
								parent.onInterrupted(computer, System.currentTimeMillis());
								client.close();
								return;
							}
							System.out.println(LanguageManager.getString("client.output.success", client.getRemoteSocketAddress()));
							System.out.println(LanguageManager.getString("client.output.request", request.toString()));
							final DataOutputStream output = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
							output.writeUTF(request.toString()); // On prépare l'envoi.
							output.flush(); // On envoie la requête.
							if(!running) { // Si le client n'est plus en fonctionnement, on interrompt tout.
								parent.onInterrupted(computer, System.currentTimeMillis());
								client.close();
								return;
							}
							final DataInputStream input = new DataInputStream(new BufferedInputStream(client.getInputStream()));
							final String response = input.readUTF(); // On récupère le contenu de la réponse.
							System.out.println(LanguageManager.getString("client.output.response", response)); // in.readUTF() permet d'obtenir la réponse du serveur.
							if(running) { // Si le client n'est plus en fonctionnement, on interrompt tout.
								final String[] parts = response.split(" "); // On sépare la réponse UTF à l'espace.
								if(Utils.isNumeric(parts[2])) { // Si le protocole est bien un nombre, on le vérifie.
									final int version = Integer.parseInt(parts[2]);
									if(PROTOCOL_VERSION < version) { // Si la version du protocole du prof est inférieure à celle de l'élève, on renvoie une erreur.
										client.close();
										throw new Exception(LanguageManager.getString("client.message.clienttooold"));
									}
									else if(PROTOCOL_VERSION > version) { // Et si la version du protocole du prof est supérieure à celle de l'élève, on en renvoie une autre.
										client.close();
										throw new Exception(LanguageManager.getString("client.message.servertooold"));
									}
								}
								else { // Si ce n'est pas un nombre, on montre une erreur.
									client.close();
									throw new Exception(LanguageManager.getString("client.message.invalidprotocol"));
								}
								String message = null;
								final String[] splittedMessage = Arrays.copyOfRange(parts, 4, parts.length);
								if(splittedMessage.length > 0) {
									message = Utils.join(" ", splittedMessage); // On recréé le message.
								}
								if(parts[0].equals("0")) { // Si la première partie est 0 (soit valide) alors, on renvoi un succès.
									switch(request.getType()) { // En fonction de ce que l'on a demandé on execute ou non une action.
									case THUMBNAIL:
									case FULL_SCREENSHOT:
										parent.onSuccess(computer, parts[1], Long.valueOf(parts[3]), message, ImageIO.read(input));
										break;
									default:
										parent.onSuccess(computer, parts[1], Long.valueOf(parts[3]), message);
										break;
									}
								}
								else { // Sinon c'est une erreur.
									client.close();
									parent.onError(computer, new Exception(message), Long.valueOf(parts[3]));
								}
							}
							else { // On interrompt le client.
								parent.onInterrupted(computer, System.currentTimeMillis());
							}
							System.out.println(LanguageManager.getString("client.output.closing"));
							client.close();
						}
						catch(final Exception ex) {
							if(!running) { // Si le client on ne renvoie rien.
								return;
							}
							parent.onError(computer, ex, System.currentTimeMillis());
						}
						joinedComputers.add(computer); // On ajoute l'ordinateur aux ordinateurs joints.
					}
					
				}.start();
			}
			try {
				if(oneRequest) { // Si il n'y a qu'une requête, on s'en va. Sinon on attends.
					System.out.println();
					return;
				}
				System.out.println(LanguageManager.getString("client.output.waiting.computers"));
				while(computers.length != joinedComputers.size()) { // Tant que tous les ordinateurs n'ont pas tous été joints.
					if(!running) {
						return;
					}
					Thread.sleep(1000);
				}
				System.out.println(LanguageManager.getString("client.output.waiting.interval", ProjetBBQProf.settings.refreshInterval));
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