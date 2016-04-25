package fr.isn.bbq.prof.frames;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.Room;
import fr.isn.bbq.prof.dialogs.MessageDialog;
import fr.isn.bbq.prof.frames.tabs.RoomPane;

import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;
import java.awt.Toolkit;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final List<Room> rooms = new ArrayList<Room>();
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private int currentIndex = -1;

	/**
	 * Première méthode exécutée par la fenêtre.
	 */
	
	public MainFrame() {
		this.setTitle(ProjetBBQProf.APP_NAME + " v" + ProjetBBQProf.APP_VERSION);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/app_icon.png")));
		this.setSize(600, 400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		loadRooms();
	}
	
	/**
	 * Chargement des salles.
	 */
	
	private final void loadRooms() {
		final MessageDialog message = new MessageDialog(this, "Chargement des salles, veuillez patienter...");
		message.setVisible(true);
		try {
			rooms.clear();
			tabbedPane.removeAll();
			for(final File roomFile : ProjetBBQProf.getRoomDirectory().listFiles()) {
				if(!roomFile.getName().toLowerCase().endsWith(".xml")) {
					continue;
				}
				final Room room = new Room();
				room.load(new String(Files.readAllBytes(roomFile.toPath())));
				rooms.add(room); // On prends le nom du fichier sans l'extension.
			}
			message.dispose();
			if(rooms.size() == 0) {
				JOptionPane.showMessageDialog(MainFrame.this, "Pas de salle valide ajoutée. Veuillez consulter l'aide en ligne.", "Erreur !", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			for(final Room room : rooms) {
				final JScrollPane pane = new JScrollPane(new RoomPane(room.computers));
				pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				tabbedPane.add(room.name, pane);
			}
			tabbedPane.addChangeListener(new ChangeListener() {

				@Override
				public final void stateChanged(final ChangeEvent event) {
					if(currentIndex != -1) {
						((RoomPane)tabbedPane.getComponentAt(currentIndex)).stopRequests();
					}
					currentIndex = tabbedPane.getSelectedIndex();
				}
				
			});
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			message.setMessage(ex.getClass().getName()); // TODO: Meilleure gestion de l'erreur.
		}
	}

}