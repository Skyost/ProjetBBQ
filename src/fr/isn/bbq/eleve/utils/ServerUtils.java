package fr.isn.bbq.eleve.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import fr.isn.bbq.eleve.tasks.HandleClient;

public class ServerUtils {
	
	/**
	 * Envoi d'un message à un client.
	 * 
	 * @param client Le client.
	 * @param message Le message.
	 * @param output L'OutputStream utilisé pour envoyer le message.
	 * 
	 * @throws IOException Si un exception se produit.
	 */
	
	public static final void sendMessage(final Socket client, final String message, final DataOutputStream output) throws IOException {
		sendMessage(client, message, output, true);
	}
	
	/**
	 * Envoi d'un message à un client.
	 * 
	 * @param client Le client.
	 * @param message Le message.
	 * @param output L'OutputStream utilisé pour envoyer le message.
	 * @param close Si on doit fermer la connexion après avoir envoyé le message.
	 * 
	 * @throws IOException Si un exception se produit.
	 */
	
	public static final void sendMessage(final Socket client, final String message, final DataOutputStream output, final boolean close) throws IOException {
		System.out.println(LanguageManager.getString("server.debug.response"));
		System.out.println(message);
		output.writeUTF(message); // On prépare l'envoi.
		output.flush(); // On envoie le message.
		if(close) {
			client.close();
		}
	}
	
	/**
	 * Création de la réponse.
	 * 
	 * @param success Si le traitement est un succès ou non.
	 * 
	 * @return La réponse formattée.
	 */
	
	public static final String createResponse(final boolean success) {
		return createResponse(success, null);
	}
	
	/**
	 * Création de la réponse.
	 * 
	 * @param success Si le traitement est un succès ou non.
	 * @param message Le message à envoyer.
	 * 
	 * @return La réponse formattée.
	 */
	
	public static final String createResponse(final boolean success, final String message) {
		return (success ? "0 " : "1 ") + OS.getUserName().replace(" ", "-") + " " + HandleClient.PROTOCOL_VERSION + " " + System.currentTimeMillis() + (message == null ? "" : " " + message);
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