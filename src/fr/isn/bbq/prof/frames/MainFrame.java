package fr.isn.bbq.prof.frames;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.Room;
import fr.isn.bbq.prof.dialogs.MessageDialog;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final List<Room> rooms = new ArrayList<Room>();

	/**
	 * Première méthode exécutée par la fenêtre.
	 */
	
	public MainFrame() {
		this.setTitle(ProjetBBQProf.APP_NAME + " v" + ProjetBBQProf.APP_VERSION);
		this.setSize(100, 100);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		loadRooms();
	}
	
	/**
	 * Chargement des salles.
	 */
	
	private final void loadRooms() {
		try {
			rooms.clear();
			final MessageDialog message = new MessageDialog(this, "Chargement des salles, veuillez patienter...");
			message.setVisible(true);
			for(final File roomFile : ProjetBBQProf.getRoomDirectory().listFiles()) {
				final Room room = new Room(roomFile);
				room.load();
				rooms.add(room);
			}
			message.dispose();
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}

}