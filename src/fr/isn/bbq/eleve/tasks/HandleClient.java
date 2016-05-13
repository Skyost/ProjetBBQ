package fr.isn.bbq.eleve.tasks;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import javax.imageio.ImageIO;

import fr.isn.bbq.eleve.ProjetBBQEleve;
import fr.isn.bbq.eleve.frames.LockFrame;
import fr.isn.bbq.eleve.frames.MessageFrame;
import fr.isn.bbq.eleve.utils.ServerUtils;
import fr.isn.bbq.eleve.utils.Utils;
import fr.isn.bbq.eleve.utils.ServerUtils.RequestType;

/**
 * Permet de servir un client.
 */

public class HandleClient extends Thread {
	
	/**
	 * La taille d'une miniature d'un ordinateur (carrée, en pixels).
	 */
	
	public static final short THUMBNAIL_SIZE = 100;
	
	/**
	 * Le client.
	 */
	
	private final Socket client;
	
	private LockFrame lockFrame;
	
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
			System.out.println("Connecté à " + client.getRemoteSocketAddress() + ".");
			final DataInputStream input = new DataInputStream(client.getInputStream());
			final String message = input.readUTF(); // On récupère le contenu de la requête.
			System.out.println("Message reçu : \"" + message + "\".");
			final String[] parts = message.split(" "); // On sépare la requête à l'espace.
			if(parts.length < 2) { // Il faut qu'il y ai au moins deux arguments (l'index et l'UUID).
				ServerUtils.sendMessage(client, ServerUtils.createResponse(false, "Message invalide (doit être de type \"<index> <uuid> <arguments>\"."));
				return;
			}
			RequestType type = null;
			if(!Utils.isNumeric(parts[0]) || (type = RequestType.getFromIndex(Integer.valueOf(parts[0]))) == null) { // Si l'index n'est pas numérique ou est invalide, on renvoie une erreur.
				ServerUtils.sendMessage(client, ServerUtils.createResponse(false, "L'index est invalide."));
				return;
			}
			if(!ProjetBBQEleve.settings.uuids.contains(parts[1])) { // Si l'UUID n'est pas dans la liste, on renvoie une erreur.
				ServerUtils.sendMessage(client, ServerUtils.createResponse(false, "Non autorisé."));
				return;
			}
			final OutputStream output = client.getOutputStream(); // Pour renvoyer des messages au client.
			switch(type) {
			case THUMBNAIL:
				/* Méthode permettant de redimensionner une image. */
				final BufferedImage resized = new BufferedImage(THUMBNAIL_SIZE, THUMBNAIL_SIZE, BufferedImage.TYPE_INT_RGB);
				final Graphics graphics = resized.createGraphics();
				graphics.drawImage(screenshot(), 0, 0, THUMBNAIL_SIZE, THUMBNAIL_SIZE, null);
				graphics.dispose();
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false); // On envoie d'abord la réponse "true" sans fermer l'OutputStream.
				ImageIO.write(resized, ProjetBBQEleve.settings.imageType, output); // Puis on envoie l'image redimensionnée.
				break;
			case FULL_SCREENSHOT:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false); // Idem ici.
				ImageIO.write(screenshot(), ProjetBBQEleve.settings.imageType, output);
				break;
			case MESSAGE:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true));
				if(!Utils.isNumeric(parts[parts.length - 1])) {
					ServerUtils.sendMessage(client, ServerUtils.createResponse(false, "Pas de dure valide entrée."));
					return;
				}
				new MessageFrame(Utils.join(" ", Arrays.copyOfRange(parts, 2, parts.length - 1)), Integer.valueOf(parts[parts.length - 1])).setVisible(true);
				break;
			case LOCK:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false);
				if(lockFrame == null) {
					lockFrame = new LockFrame(screenshot());
					lockFrame.setVisible(true);
				}
				break;
			case UNLOCK:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false);
				if(lockFrame != null) {
					lockFrame.unlockAndClose();
					lockFrame = null;
				}
				break;
			case SHUTDOWN:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false);
				// -s pour l'arret; -f pour le forcer; -t pour le temps.
				Runtime.getRuntime().exec("shutdown.exe -s -f -t 0");
				break;
			case RESTART:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false);
				// -s pour l'arret; -f pour le forcer; -t pour le temps.
				 Runtime.getRuntime().exec("shutdown.exe -r -f -t  0");
				break;
			case LOGOUT:
				ServerUtils.sendMessage(client, ServerUtils.createResponse(true), output, false);
				// -s pour l'arret; -f pour le forcer; -t pour le temps.
				 Runtime.getRuntime().exec("shutdown.exe -l");
				break;
			default:
				break;
			}
			System.out.println("Fermeture de la connexion avec le client...");
			client.close(); // On ferme la connexion au client.
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