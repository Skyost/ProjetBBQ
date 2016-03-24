package fr.isn.bbq.eleve.tests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerTest {
	
	/**
	* Premi�re m�thode ex�cut�e par le programme.
	* 
	* @param args Arguments � passer.
	*/
	
	public static final void main(final String[] args) {
		new Thread() {
			
			@Override
			public final void run() {
				try {
					final ServerSocket serverSocket = new ServerSocket(4444, 50, InetAddress.getByName("192.168.0.138")); // Cr�ation d'un nouveau serveur sur le port 4444.
					serverSocket.setSoTimeout(10000);
					while(true) {
						try {
							System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
							Socket server = serverSocket.accept(); // Le client se connecte.
							System.out.println("Just connected to " + server.getRemoteSocketAddress());
							DataInputStream in = new DataInputStream(server.getInputStream());
							System.out.println(in.readUTF()); // Message du client.
							DataOutputStream out = new DataOutputStream(server.getOutputStream());
							out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!"); // Envoi d'un message au client.
							server.close();
						}
						catch(final SocketTimeoutException timedOut) {
							continue;
						}
						catch(final Exception ex) {
							ex.printStackTrace();
							break;
						}
					}
					serverSocket.close();
				}
				catch(final Exception ex) {
					ex.printStackTrace();
				}
			}
			
		}.start();
	}

}