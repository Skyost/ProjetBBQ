package fr.isn.bbq.prof;

/**
 * Représente une salle de classe.
 */

public class Computer {
	
	public String name;
	public String ip;
	
	public Computer() {
		this(null, null);
	}
	
	public Computer(final String name, final String ip) {
		this.name = name;
		this.ip = ip;
	}
	
}