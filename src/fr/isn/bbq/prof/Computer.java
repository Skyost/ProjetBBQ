package fr.isn.bbq.prof;

/**
 * Représente une salle de classe.
 */

public class Computer {
	
	public String name;
	public String ip;
	public int port;
	
	public Computer() {
		this(null, null, -1);
	}
	
	public Computer(final String name, final String ip, final int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
	}
	
}