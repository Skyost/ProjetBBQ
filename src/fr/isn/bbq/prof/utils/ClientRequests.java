package fr.isn.bbq.prof.utils;

/**
 * Utilisé pour formuler des requêtes aux logiciels élèves.
 */

public class ClientRequests {
	
	/**
	 * Créé une requête.
	 * 
	 * @param type Le type de cette requête.
	 * @param args Les arguments (voir l'aide en ligne).
	 * 
	 * @return La requête formattée.
	 */
	
	public static final String createRequest(final RequestType type, final String... args) {
		final String request;
		int index = 0;
		switch(type) {
		case THUMBNAIL:
			request = index + " " + args[0];
			break;
		case FULL_SCREENSHOT:
			request = ++index + " " + args[0];
			break;
		default:
			throw new IllegalArgumentException("Type inconnu.");
		}
		return request;
	}
	
	/**
	 * Types de requête.
	 */
	
	public enum RequestType {
		
		/**
		 * On souhaite une miniature.
		 */
		
		THUMBNAIL,
		
		/**
		 * On souhaite une capture d'écran.
		 */
		
		FULL_SCREENSHOT;
		
	}

}