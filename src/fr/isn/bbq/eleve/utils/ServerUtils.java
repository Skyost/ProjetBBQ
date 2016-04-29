package fr.isn.bbq.eleve.utils;

public class ServerUtils {
	
	public static final String createResponse(final boolean success) {
		return createResponse(success, null);
	}
	
	public static final String createResponse(final boolean success, final String message) {
		return (success ? "0 " : "1 ") + (message == null ? "" : message + " ") + System.currentTimeMillis();
	}
	
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
		
		MESSAGE(2);
		
		private final int index;
		
		private RequestType(final int index) {
			this.index = index;
		}
		
		public static final RequestType getFromIndex(final int index) {
			for(final RequestType request : RequestType.values()) {
				if(index != request.index) {
					continue;
				}
				return request;
			}
			return null;
		}
		
		public final int getIndex() {
			return index;
		}
		
	}

}
