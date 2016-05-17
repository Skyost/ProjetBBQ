package fr.isn.bbq.prof.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;

/**
 * Un vérificateur de mise à jour via Github.
 * 
 * @author Skyost (cf. Algogo).
 */

public class GithubUpdater extends Thread {
	
	public static final String UPDATER_NAME = "GithubUpdater";
	public static final String UPDATER_VERSION = "0.1";
	
	public static final String UPDATER_GITHUB_USERNAME = "Skyost";
	public static final String UPDATER_GITHUB_REPO = "ProjetBBQ";
		
	private final String localVersion;
	private final GithubUpdaterResultListener caller;
	
	/**
	 * Création d'une instance de <b>GithubUpdater</b>.
	 * 
	 * @param localVersion La version locale.
	 * @param caller Le parent.
	 */
	
	public GithubUpdater(final String localVersion, final GithubUpdaterResultListener caller) {
		this.localVersion = localVersion;
		this.caller = caller;
	}
	
	@Override
	public final void run() {
		caller.updaterStarted();
		try {
			final HttpURLConnection connection = (HttpURLConnection)new URL("https://api.github.com/repos/" + UPDATER_GITHUB_USERNAME + "/" + UPDATER_GITHUB_REPO + "/releases").openConnection();
			connection.addRequestProperty("User-Agent", UPDATER_NAME + " by " + UPDATER_GITHUB_USERNAME + " v" + UPDATER_VERSION);
			final String response = connection.getResponseCode() + " " + connection.getResponseMessage();
			caller.updaterResponse(response);
			if(!response.startsWith("2")) {
				throw new Exception("Invalid response : " + response);
			}
			final InputStream input = connection.getInputStream();
			final InputStreamReader inputStreamReader = new InputStreamReader(input, StandardCharsets.UTF_8);
			final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			final JsonArray releases = Json.parse(bufferedReader.readLine()).asArray();
			if(releases.size() < 1) {
				caller.updaterNoUpdate(localVersion, localVersion);
				return;
			}
			input.close();
			inputStreamReader.close();
			bufferedReader.close();
			final String remoteVersion = releases.get(0).asObject().get("tag_name").asString().substring(1);
			if(compareVersions(remoteVersion, localVersion)) {
				caller.updaterUpdateAvailable(localVersion, remoteVersion);
			}
			else {
				caller.updaterNoUpdate(localVersion, remoteVersion);
			}
		}
		catch(final Exception ex) {
			caller.updaterException(ex);
		}
	}
	
	/**
	 * Compare de version.
	 * 
	 * @param version1 La version une.
	 * @param version2 La version deux.
	 * 
	 * @return <b>true</b> Si <b>versionTo</b> est inférieure à <b>versionWith</b>.
	 * <br><b>false</b> Si <b>versionTo</b> est supérieure ou égale à <b>versionWith</b>.
	 */
	
	private static final boolean compareVersions(final String versionTo, final String versionWith) {
		return normalisedVersion(versionTo, ".", 4).compareTo(normalisedVersion(versionWith, ".", 4)) > 0;
	}
	
	/**
	 * Retourne la chaîne formattée d'une version.
	 * <br>Utilisé pour la méthode <b>compareVersions(...)</b> de cette classe.
	 * 
	 * @param version La version que vous souhaitez formatter.
	 * @param separator Le séparateur entre les révisions de la version (souvent un point).
	 * @param maxWidth La taille maximale de la version formattée.
	 * 
	 * @return Une chaîne de caractère représentant une version formattée.
	 * 
	 * @author Peter Lawrey.
	 */

	private static final String normalisedVersion(final String version, final String separator, final int maxWidth) {
		final StringBuilder stringBuilder = new StringBuilder();
		for(final String normalised : Pattern.compile(separator, Pattern.LITERAL).split(version)) {
			stringBuilder.append(String.format("%" + maxWidth + 's', normalised));
		}
		return stringBuilder.toString();
	}
	
	public interface GithubUpdaterResultListener {
		
		/**
		 * Quand l'updater démarre.
		 */
		
		public void updaterStarted();
		
		/**
		 * Quand une erreur se produit.
		 * 
		 * @param ex L'erreur.
		 */
		
		public void updaterException(final Exception ex);
		
		/**
		 * La réponse de cette requête.
		 * 
		 * @param response La réponse.
		 */
		
		public void updaterResponse(final String response);
		
		/**
		 * Si une mise à jour est disponible.
		 * 
		 * @param localVersion La version locale (donnée pour construire l'updater).
		 * @param remoteVersion La version distante.
		 */
		
		public void updaterUpdateAvailable(final String localVersion, final String remoteVersion);
		
		/**
		 * Si il n'y a pas de mise à jour.
		 * 
		 * @param localVersion La version locale (donnée pour construire l'updater).
		 * @param remoteVersion La version distante.
		 */
		
		public void updaterNoUpdate(final String localVersion, final String remoteVersion);
		
	}

}