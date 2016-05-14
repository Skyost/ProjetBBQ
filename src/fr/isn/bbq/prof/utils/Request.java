package fr.isn.bbq.prof.utils;

import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.tasks.Client;

/**
 * Utilisé pour formuler des requêtes aux logiciels élèves.
 */

public class Request {
	
	private RequestType type;
	private String[] args;
	
	/**
	 * Créé une nouvelle requête.
	 * 
	 * @param type Le type de cette requête.
	 */
	
	public Request(final RequestType type) {
		this(type, new String[]{});
	}
	
	/**
	 * Créé une nouvelle requête.
	 * 
	 * @param type Le type de cette requête.
	 * @param args Les arguments à envoyer.
	 */
	
	public Request(final RequestType type, final String... args) {
		this.type = type;
		this.args = args;
	}
	
	/**
	 * Permet d'obtenir le type de cette requête.
	 * 
	 * @return Le type de cette requête.
	 */
	
	public final RequestType getType() {
		return type;
	}
	
	/**
	 * Permet de changer le type de cette requête.
	 */
	
	public final void setType(final RequestType type) {
		this.type = type;
	}
	
	/**
	 * Permet d'obtenir les arguments cette requête (UUID et version du protocol exclu donc).
	 * 
	 * @return Les arguments de cette requête.
	 */
	
	public final String[] getArguments() {
		return args;
	}
	
	/**
	 * Permet de changer les arguments cette requête (UUID et version du protocol exclu).
	 */
	
	public final void setArguments(final String... args) {
		this.args = args;
	}
	
	/**
	 * Transforme la requête en chaîne de caractère. Prête à être envoyée.
	 * 
	 * @param type Le type de cette requête.
	 * @param args Les arguments (voir l'aide en ligne).
	 * 
	 * @return La requête formattée.
	 */
	
	@Override
	public final String toString() {
		switch(type) {
		case MESSAGE:
			if(args.length < 2) {
				throw new IllegalArgumentException("Not enough arguments.");
			}
			if(!Utils.isNumeric(args[1])) {
				throw new IllegalArgumentException("\"" + args[1] + "\" is not a number.");
			}
			break;
		default:
			break;
		}
		return type.getIndex() + " " + ProjetBBQProf.settings.uuid + " " + Client.PROTOCOL_VERSION + (args != null && args.length > 0 ? " " + Utils.join(" ", args) : "");
	}
	
	/**
	 * Types de requête.
	 */
	
	public enum RequestType {
		
		/**
		 * On souhaite une miniature.
		 */
		
		THUMBNAIL(0),
		
		/**
		 * On souhaite une capture d'écran.
		 */
		
		FULL_SCREENSHOT(1),
		
		/**
		 * On souhaite envoyer un message.
		 */
		
		MESSAGE(2),
		
		/**
		 * On souhaite geler l'écran.
		 */
		
		LOCK(3),
		
		/**
		 * On souhaite dégeler l'écran.
		 */
		
		UNLOCK(4),
		
		/**
		 * On souhaite éteindre l'ordinateur.
		 */
		
		SHUTDOWN(5),
		
		/**
		 * On souhaite redémarrer l'ordinateur.
		 */
		
		RESTART(6),
		
		/**
		 * On souhaite déconnecter l'ordinateur.
		 */
		
		LOGOUT(7);
		
		private final int index;
		
		private RequestType(final int index) {
			this.index = index;
		}
		
		/**
		 * Obtient le type correspondant à l'index.
		 */
		
		public static final RequestType getFromIndex(final int index) {
			for(final RequestType request : RequestType.values()) {
				if(index != request.index) {
					continue;
				}
				return request;
			}
			return null;
		}
		
		/**
		 * Obtient l'index de ce type.
		 * 
		 * @return L'index.
		 */
		
		public final int getIndex() {
			return index;
		}
		
	}

}