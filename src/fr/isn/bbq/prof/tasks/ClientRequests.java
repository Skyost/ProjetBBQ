package fr.isn.bbq.prof.tasks;

public class ClientRequests {
	
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
	
	public enum RequestType {
		
		THUMBNAIL,
		FULL_SCREENSHOT;
		
	}

}