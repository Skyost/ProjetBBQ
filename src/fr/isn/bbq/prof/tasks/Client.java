package fr.isn.bbq.prof.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;

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
	
	private final String request;
	
	/**
	 * Les logiciels élèves représentés par des objets "Computer".
	 */
	
	private final Computer[] computers;
	
	private final boolean oneRequest;
	
	public Client(final ClientInterface parent, final String request, final Computer... computers) {
		this(parent, request, computers, false);
	}
	
	public Client(final ClientInterface parent, final String request, final Computer[] computers, final boolean oneRequest) {
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
							//parent.onError(computer, null); // Test.
							System.out.println("Connexion à l'ordinateur " + computer.name + " (" + computer.ip + ") sur le port " + computer.port + "...");
							final Socket client = new Socket();
							client.connect(new InetSocketAddress(computer.ip, computer.port), ProjetBBQProf.settings.timeOut * 1000); // On se connecte au poste élève.
							if(!running) { // Si le client n'est plus en fonctionnement, on interrompt tout.
								parent.onInterrupted(computer, System.currentTimeMillis());
								client.close();
								return;
							}
							System.out.println("Connexion réussie à " + client.getRemoteSocketAddress() + ".");
							System.out.println("Envoi de la requête \"" + request + "\"...");
							final OutputStream outToServer = client.getOutputStream();
							final DataOutputStream out = new DataOutputStream(outToServer);
							out.writeUTF(request); // On envoie la requête.
							if(!running) { // Si le client n'est plus en fonctionnement, on interrompt tout.
								parent.onInterrupted(computer, System.currentTimeMillis());
								client.close();
								return;
							}
							final InputStream inFromServer = client.getInputStream();
							final DataInputStream in = new DataInputStream(inFromServer);
							final String response = in.readUTF();
							System.out.println("Réponse du server \"" + response + "\"."); // in.readUTF() permet d'obtenir la réponse du serveur.
							if(running) { // Si le client n'est plus en fonctionnement, on interrompt tout.
								final String[] parts = response.split(" ");
								if(parts[0].equals("0")) {
									parent.onSuccess(computer, ImageIO.read(client.getInputStream()), Long.valueOf(parts[parts.length - 1]));
								}
								else {
									parent.onError(computer, new Exception("Bad response : \"" + response + "\""), Long.valueOf(parts[parts.length - 1]));
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
					return;
				}
				while(computers.length != joinedComputers.size()) { // Tant que tous les ordinateurs n'ont pas tous été joints.
					if(!running) {
						return;
					}
					System.out.println("Attente des ordinateurs...");
					Thread.sleep(1000);
				}
				System.out.println("Attente de " + ProjetBBQProf.settings.refreshInterval + " sec...");
				parent.onWaiting();
				Thread.sleep(ProjetBBQProf.settings.refreshInterval * 1000); // Le client se connecte à chaque serveur toutes les 5 secondes.
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
		 * @param returned La réponse (peut être parsée par le client).
		 * @param responseTime La date à laquelle la réponse à été envoyée, en millisecondes et définie par le client.
		 */
		
		public void onSuccess(final Computer computer, final Object returned, final long responseTime);
		
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