package fr.isn.bbq.eleve.tasks;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

import fr.isn.bbq.eleve.ProjetBBQEleve;
import fr.isn.bbq.eleve.utils.ServerUtils;
import fr.isn.bbq.eleve.utils.Utils;
import fr.isn.bbq.eleve.utils.ServerUtils.RequestType;

public class HandleClient extends Thread {
	
	/**
	 * La taille d'une miniature d'un ordinateur (carrée, en pixels).
	 */
	
	public static final short THUMBNAIL_SIZE = 100;
	
	private final Socket client;
	
	public HandleClient(final Socket client) {
		this.client = client;
	}
	
	@Override
	public final void run() {
		try {
			System.out.println("Connecté à " + client.getRemoteSocketAddress() + ".");
			final DataInputStream input = new DataInputStream(client.getInputStream());
			final String message = input.readUTF();
			System.out.println("Message reçu : \"" + message + "\".");
			final String[] parts = message.split(" ");
			if(parts.length < 2) {
				sendMessage(client, ServerUtils.createResponse(false, "Message invalide (doit être de type \"<index> <arguments>\"."));
				return;
			}
			RequestType type = null;
			if(!Utils.isNumeric(parts[0]) || (type = RequestType.getFromIndex(Integer.valueOf(parts[0]))) == null) {
				sendMessage(client, ServerUtils.createResponse(false, "L'index est invalide."));
				return;
			}
			if(!ProjetBBQEleve.settings.uuids.contains(parts[1])) {
				sendMessage(client, ServerUtils.createResponse(false, "Non autorisé."));
				return;
			}
			final OutputStream output = client.getOutputStream();
			switch(type) {
			case THUMBNAIL:
				final BufferedImage resized = new BufferedImage(THUMBNAIL_SIZE, THUMBNAIL_SIZE, BufferedImage.TYPE_INT_RGB);
				final Graphics graphics = resized.createGraphics();
				graphics.drawImage(screenshot(), 0, 0, THUMBNAIL_SIZE, THUMBNAIL_SIZE, null);
				graphics.dispose();
				sendMessage(client, ServerUtils.createResponse(true), output, false);
				ImageIO.write(resized, "JPG", output);
				break;
			case FULL_SCREENSHOT:
				sendMessage(client, ServerUtils.createResponse(true), output, false);
				ImageIO.write(screenshot(), "JPG", output);
				break;
			case MESSAGE:
				// TODO: Message avec IHM, temps d'affichage, ...
				break;
			default:
				break;
			}
			client.close();
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static final void sendMessage(final Socket client, final String message) throws IOException {
		sendMessage(client, message, client.getOutputStream());
	}
	
	private static final void sendMessage(final Socket client, final String message, final OutputStream output) throws IOException {
		sendMessage(client, message, output, true);
	}
	
	private static final void sendMessage(final Socket client, final String message, final OutputStream output, final boolean close) throws IOException {
		System.out.println("Envoi de la réponse...");
		System.out.println(message);
		final DataOutputStream dataOutput = new DataOutputStream(output);
		dataOutput.writeUTF(message);
		if(close) {
			client.close();
		}
		else {
			dataOutput.flush();
		}
	}
	
	private static final BufferedImage screenshot() throws HeadlessException, AWTException {
		return new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}

}