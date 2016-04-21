package fr.isn.bbq.prof.tests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientTest {
	
	/**
	 * Première méthode exécutée par le programme.
	 * 
	 * @param args Arguments à passer.
	 */
	
	public static final void main(final String[] args) {
		try {
			final String server = args[0];
			final int port = 5900;
			System.out.println("Connecting to " + server + " on port " + port);
			Socket client = new Socket(server, port);
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
			ex.printStackTrace();
		}
	}

}