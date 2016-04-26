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
							parent.connection(computer);
							//parent.onError(computer, null); // Test
							final int port = 4444;
							System.out.println("Connecting to " + computer.ip + " on port " + port);
							Socket client = new Socket(computer.ip, port);
							System.out.println("Just connected to " + client.getRemoteSocketAddress());
							OutputStream outToServer = client.getOutputStream();
							DataOutputStream out = new DataOutputStream(outToServer);
							out.writeUTF(request); //TODO : traiter chaque requête dans un Thread séparé
							InputStream inFromServer = client.getInputStream();
							DataInputStream in = new DataInputStream(inFromServer);
							System.out.println("Server says " + in.readUTF());
							client.close();
						}
						catch(final Exception ex) {
							parent.onError(computer, ex);
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
		
		public void connection(final Computer computer);
		public void onSuccess(final Computer computer, final Object returned);
		public void onError(final Computer computer, final Exception ex);
		public void onWaiting();
		
	}

}