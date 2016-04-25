package fr.isn.bbq.prof.frames;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.Room;
import fr.isn.bbq.prof.dialogs.MessageDialog;
import fr.isn.bbq.prof.frames.tabs.RoomPane;

import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final List<Room> rooms = new ArrayList<Room>();

	/**
	 * Première méthode exécutée par la fenêtre.
	 */
	
	public MainFrame() {
		this.setTitle(ProjetBBQProf.APP_NAME + " v" + ProjetBBQProf.APP_VERSION);
		this.setSize(600, 400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		loadRooms();
		for(final Room room : rooms) {
			tabbedPane.add(room.name, new JScrollPane(new RoomPane(room.computers)));
		}
	}
	
	/**
	 * Chargement des salles.
	 */
	
	private final void loadRooms() {
		final MessageDialog message = new MessageDialog(this, "Chargement des salles, veuillez patienter...");
		message.setVisible(true);
		try {
			rooms.clear();
			for(final File roomFile : ProjetBBQProf.getRoomDirectory().listFiles()) {
				if(!roomFile.getName().endsWith(".xml")) {
					continue;
				}
				final Room room = new Room();
				room.load(new String(Files.readAllBytes(roomFile.toPath())));
				rooms.add(room); // On prends le nom du fichier sans l'extension.
			}
			message.dispose();
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			message.setMessage(ex.getClass().getName()); // TODO: Meilleure gestion de l'erreur.
		}
	}

}