package fr.isn.bbq.prof.utils;

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
	 * Permet d'obtenir les arguments cette requête.
	 * 
	 * @return Les arguments de cette requête.
	 */
	
	public final String[] getArguments() {
		return args;
	}
	
	/**
	 * Permet de changer les arguments cette requête.
	 */
	
	public final void setArguments(final String... args) {
		this.args = args;
	}
	
	/**
	 * Transforme la requête en chaîne de caractère.
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
			if(!Utils.isNumeric(args[2])) {
				throw new IllegalArgumentException("\"" + args[2] + "\" is not a number.");
			}
			break;
		default:
			if(args.length < 1) {
				throw new IllegalArgumentException("Not enough arguments.");
			}
			break;
		}
		return type.getIndex() + " " + Utils.join(" ", args);
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
		
		FREEZE(3),
		
		/**
		 * On souhaite éteindre l'ordinateur.
		 */
		
		SHUTDOWN(4),
		
		/**
		 * On souhaite redémarrer l'ordinateur.
		 */
		
		RESTART(5),
		
		/**
		 * On souhaite déconnecter l'ordinateur.
		 */
		
		LOGOUT(6);
		
		/**
		 * Index a envoyer dans la requête.
		 */
		
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