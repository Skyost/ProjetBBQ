package fr.isn.bbq.prof.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import fr.isn.bbq.prof.Computer;

public class Client extends Thread {
	
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
			for(final Computer computer : computers) {
				try {
					parent.connection(computer);
					final int port = 4444;
					System.out.println("Connecting to " + computer.ip + " on port " + port);
					Socket client = new Socket(computer.ip, port);
					System.out.println("Just connected to " + client.getRemoteSocketAddress());
					OutputStream outToServer = client.getOutputStream();
					DataOutputStream out = new DataOutputStream(outToServer);
					out.writeUTF("Hello from " + client.getLocalSocketAddress());
					InputStream inFromServer = client.getInputStream();
					DataInputStream in = new DataInputStream(inFromServer);
					System.out.println("Server says " + in.readUTF());
					client.close();
				}
				catch(final Exception ex) {
					parent.onError(computer, ex);
				}
			}
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
		
	}

}