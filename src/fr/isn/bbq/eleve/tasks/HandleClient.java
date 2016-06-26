package fr.isn.bbq.eleve.tasks;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;

import javax.imageio.ImageIO;

import fr.isn.bbq.eleve.ProjetBBQEleve;
import fr.isn.bbq.eleve.frames.LockFrame;
import fr.isn.bbq.eleve.frames.MessageFrame;
import fr.isn.bbq.eleve.utils.LanguageManager;
import fr.isn.bbq.eleve.utils.OS;
import fr.isn.bbq.eleve.utils.ServerUtils;
import fr.isn.bbq.eleve.utils.Utils;
import fr.isn.bbq.eleve.utils.ServerUtils.RequestType;

/**
 * Permet de servir un client.
 */

public class HandleClient extends Thread {
	
	/**
	 * La version du protocol utilisée pour communiquer avec le client.
	 */
	
	public static final short PROTOCOL_VERSION = 1;
	
	/**
	 * Le client.
	 */
	
	private final Socket client;
	
	/**
	 * L'écran de verrouillage.
	 */
	
	private static LockFrame lockFrame = null;
	
	/**
	 * Création d'un thread permettant de servir le client (il faut encore le démarrer avec <b>start()</b>).
	 * 
	 * @param client Le client.
	 */
	
	public HandleClient(final Socket client) {
		this.client = client;
	}
	
	@Override
	public final void run() {
		try {
			System.out.println(LanguageManager.getString("server.debug.connected", client.getRemoteSocketAddress()));
			final DataInputStream input = new DataInputStream(new BufferedInputStream(client.getInputStream()));
			final String message = input.readUTF(); // On récupère le contenu de la requête.
			System.out.println(LanguageManager.getString("server.debug.received", message));
			final DataOutputStream output = new DataOutputStream(new BufferedOutputStream(client.getOutputStream())); // Pour renvoyer des messages au client.
			final String[] parts = message.split(" "); // On sépare la requête à l'espace.
			if(parts.length < 2) { // Il faut qu'il y ai au moins deux arguments (l'index et l'UUID).
				ServerUtils.sendMessage(client, ServerUtils.createResponse(false, LanguageManager.getString("server.response.invalid.message")), output);
				return;
			}
			if(Utils.isNumeric(parts[2])) { // Si le protocole est bien un nombre, on le vérifie.
				final int version = Integer.parseInt(parts[2]);
				if(PROTOCOL_VERSION < version) { // Si la version du protocole de l'élève est inférieure à celle du prof, on renvoie une erreur.
					ServerUtils.sendMessage(client, ServerUtils.createResponse(false, LanguageManager.getString("server.response.invalid.servertooold")), output);
					return;
				}
				else if(PROTOCOL_VERSION > version) { // Et si la version du protocole de l'élève est supérieure à celle du prof, on en renvoie une autre.
					ServerUtils.sendMessage(client, ServerUtils.createResponse(false, LanguageManager.getString("server.response.invalid.clienttooold")), output);
					return;
				}
			}
			else { // Sinon on renvoie une erreur.
				ServerUtils.sendMessage(client, ServerUtils.createResponse(false, LanguageManager.getString("server.response.invalid.protocol")), output);
				return;
			}
			RequestType type = null;
			if(!Utils.isNumeric(parts[0]) || (type = RequestType.getFromIndex(Integer.valueOf(parts[0]))) == null) { // Si l'index n'est pas numérique ou est invalide, on renvoie une erreur.
				ServerUtils.sendMessage(client, ServerUtils.createResponse(false, LanguageManager.getString("server.response.invalid.index")), output);
				return;
			}
			if(!ProjetBBQEleve.settings.uuids.contains(parts[1])) { // Si l'UUID n'est pas dans la liste, on renvoie une erreur.
				ServerUtils.sendMessage(client, ServerUtils.createResponse(false, LanguageManager.getString("server.response.unauthorized")), output);
				return;
			}
			switch(type) {
			case THUMBNAIL:
				/* Méthode permettant de redimensionner une image. */
				final BufferedImage resized = new BufferedImage(ProjetBBQEleve.settings.thumbnailWidth, ProjetBBQEleve.settings.thumbnailHeight, BufferedImage.TYPE_INT_RGB);
				final Graphics graphics = resized.createGraphics();
				graphics.drawImage(screenshot(), 0, 0, ProjetBBQEleve.settings.thumbnailWidth, ProjetBBQEleve.settings.thumbnailHeight, null);
				graphics.dispose();
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false); // On envoie d'abord la réponse "true" sans fermer l'OutputStream.
				ImageIO.write(resized, ProjetBBQEleve.settings.imageType, output); // Puis on envoie l'image redimensionnée.
				break;
			case FULL_SCREENSHOT:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false); // Idem ici.
				ImageIO.write(screenshot(), ProjetBBQEleve.settings.imageType, output);
				break;
			case MESSAGE:
				if(Utils.isNumeric(parts[parts.length - 1])) { // Si c'est un chiffre, on renvoie un accusé et on affiche le message.
					ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false); // Accusé de réception.
					new MessageFrame(Utils.join(" ", Arrays.copyOfRange(parts, 3, parts.length - 1)), Integer.valueOf(parts[parts.length - 1])).setVisible(true);
				}
				else { // Si ce n'est pas un chiffre, on renvoie une erreur.
					ServerUtils.sendMessage(client, ServerUtils.createResponse(false, LanguageManager.getString("server.response.invalid.length")), output, false);
				}
				break;
			case LOCK:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false); // Accusé de reception.
				if(lockFrame == null) { // Si l'écran n'est pas déjà bloqué, on le bloque.
					lockFrame = new LockFrame(screenshot()); // cf. documentation sur le constructeur de LockFrame.
					lockFrame.setVisible(true);
				}
				break;
			case UNLOCK:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false); // Accusé de reception.
				if(lockFrame != null) { // Si l'écran est bloqué, on le débloque.
					lockFrame.unlockAndClose();
					lockFrame = null;
				}
				break;
			case SHUTDOWN:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false); // Accusé de reception.
				OS.shutdown(); // On eteint l'ordinateur.
				break;
			case RESTART:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false); // Accusé de reception.
				OS.restart(); // On redémarre l'ordinateur.
				break;
			case LOGOUT:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false); // Accusé de reception.
				OS.logout(); // On déconnecte l'élève.
				break;
			default:
				break;
			}
			System.out.println(LanguageManager.getString("server.debug.closing"));
			client.close(); // On ferme la connexion avec le client.
			System.out.println();
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Prise d'une capture d'écran.
	 * 
	 * @return La capture d'écran.
	 * 
	 * @throws HeadlessException Si une exception se produit.
	 * @throws AWTException Si une exception se produit.
	 */
	
	private static final BufferedImage screenshot() throws HeadlessException, AWTException {
		return new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}

}