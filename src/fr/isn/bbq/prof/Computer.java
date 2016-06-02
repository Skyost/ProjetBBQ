package fr.isn.bbq.prof;

/**
 * Représente un ordinateur.
 */

public class Computer {
	
	/**
	 * Le nom de l'ordinateur.
	 */
	
	public String name;
	
	/**
	 * L'IP de cet ordinateur.
	 */
	
	public String ip;
	
	/**
	 * Le port de cet ordinateur (pour s'y connecter).
	 */
	
	public Integer port;
	
	public Computer() {
		this(null, null, null);
	}
	
	public Computer(final String name, final String ip, final Integer port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
	}
	
}