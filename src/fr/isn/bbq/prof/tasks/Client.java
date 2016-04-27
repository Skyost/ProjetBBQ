package fr.isn.bbq.prof.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;

public class Client extends Thread {
	
	public static final short THUMBNAIL_SIZE = 100;
	
	private boolean running = false;
	
	private final ClientInterface parent;
	private final String request;
	private final Computer[] computers; // Les ordinateurs auxquels il faut envoyer un requête.
	
	public Client(final ClientInterface parent, final String request, final Computer... computers) {
		this.parent = parent;
		this.request = request;
		this.computers = computers;
	}
	
	@Override
	public final void run() {
		running = true;
		while(running) {
			final List<Computer> joinedComputers = new ArrayList<Computer>();
			for(final Computer computer : computers) {
				new Thread() {
					
					@Override
					public final void run() {
						try {
							parent.connection(computer, System.currentTimeMillis());
							//parent.onError(computer, null); // Test.
							final int port = 4444;
							System.out.println("Connexion à l'ordinateur " + computer.name + " (" + computer.ip + ") sur le port " + port + "...");
							final Socket client = new Socket(computer.ip, port);
							if(!running) {
								parent.onInterrupted(computer, System.currentTimeMillis());
							}
							System.out.println("Connexion réussie à " + client.getRemoteSocketAddress() + ".");
							final OutputStream outToServer = client.getOutputStream();
							final DataOutputStream out = new DataOutputStream(outToServer);
							out.writeUTF(request);
							if(!running) {
								parent.onInterrupted(computer, System.currentTimeMillis());
							}
							final InputStream inFromServer = client.getInputStream();
							final DataInputStream in = new DataInputStream(inFromServer);
							System.out.println("Réponse du server \"" + in.readUTF() + "\".");
							client.close();
							if(running) {
								/* 
								 * final String message = returned.split(" ");
								 * parent.onSuccess(computer, message[0], Long.valueOf(message[1]));
								 */
							}
							else {
								parent.onInterrupted(computer, System.currentTimeMillis());
							}
						}
						catch(final Exception ex) {
							parent.onError(computer, ex, System.currentTimeMillis());
						}
						joinedComputers.add(computer);
					}
					
				}.start();
			}
			try {
				while(computers.length != joinedComputers.size()) {
					Thread.sleep(1000);
				}
				parent.onWaiting();
				Thread.sleep(ProjetBBQProf.settings.refreshInterval * 1000); // Le client se connecte à chaque serveur toutes les 5 secondes.
			}
			catch(final InterruptedException ex) {}
		}
	}
	
	public final boolean isRunning() {
		return running;
	}
	
	public final void stopRequests() {
		running = false;
	}
	
	public interface ClientInterface {
		
		public void connection(final Computer computer, final long time);
		public void onSuccess(final Computer computer, final Object returned, final long responseTime);
		public void onError(final Computer computer, final Exception ex, final long responseTime);
		public void onInterrupted(final Computer computer, final long time);
		public void onWaiting();
		
	}

}