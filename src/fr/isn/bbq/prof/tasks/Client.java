package fr.isn.bbq.prof.tasks;

import fr.isn.bbq.prof.Computer;

public class Client extends Thread {
	
	private final ClientInterface parent;
	private final Computer[] computers; // Les ordinateurs auxquels il faut envoyer un requête.
	
	public Client(final ClientInterface parent, final Computer... computers) {
		this.parent = parent;
		this.computers = computers;
	}
	
	@Override
	public final void run() {
		for(final Computer computer : computers) {
			try {
				parent.connection(computer);
			}
			catch(final Exception ex) {
				parent.onError(computer, ex);
			}
		}
	}
	
	public interface ClientInterface {
		
		public void connection(final Computer computer);
		public void onSuccess(final Computer computer, final Object returned);
		public void onError(final Computer computer, final Exception ex);
		
	}

}