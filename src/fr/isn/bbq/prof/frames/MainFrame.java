package fr.isn.bbq.prof.frames;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.Room;
import fr.isn.bbq.prof.dialogs.MessageDialog;
import fr.isn.bbq.prof.frames.tabs.RoomPane;
import fr.isn.bbq.prof.utils.LanguageManager;
import fr.isn.bbq.prof.utils.Request;
import fr.isn.bbq.prof.utils.StatusBar;
import fr.isn.bbq.prof.utils.Utils;
import fr.isn.bbq.prof.utils.Request.RequestType;
import fr.isn.bbq.prof.utils.XMLSettings.XMLError;

import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * IHM principal du logiciel prof.
 */

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Les différentes salles de classe.
	 */
	
	private final List<Room> rooms = new ArrayList<Room>();
	
	/**
	 * La barre de statut pour l'affichage de messages.
	 */
	
	private final StatusBar bar = new StatusBar();
	
	/**
	 * Le composant permettant d'afficher des onglets.
	 */
	
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	
	/**
	 * L'index actuel de l'onglet séléctionné. Actuellement -1 mais il est changé dès que les salles sont ajoutées.
	 */
	
	private int currentIndex = -1;
	
	/**
	 * Première méthode exécutée par la fenêtre.
	 */
	
	public MainFrame() {
		this.setTitle(buildTitle(null));
		this.setIconImages(ProjetBBQProf.icons);
		this.setSize(600, 400); // Par défaut, une taille de 600x400.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setJMenuBar(createMenuBar());
		tabbedPane.setBorder(BorderFactory.createEmptyBorder());
		tabbedPane.addChangeListener(new ChangeListener() {

			/**
			 * On écoute tous les changements d'onglet de la part de l'utilisateur.
			 */
			
			@Override
			public final void stateChanged(final ChangeEvent event) {
				if(currentIndex != -1) { // Si l'index est défini, on annule toutes les requêtes.
					((RoomPane)((JScrollPane)tabbedPane.getComponent(currentIndex)).getViewport().getView()).stopRequests();
				}
				currentIndex = tabbedPane.getSelectedIndex(); // Et on redéfini l'index au nouvel index.
				final RoomPane pane = (RoomPane)((JScrollPane)tabbedPane.getComponent(currentIndex)).getViewport().getView();
				pane.startRequests(); // On démarre les requêtes.
				MainFrame.this.setTitle(buildTitle(pane.getName())); // Puis on change le titre.
			}
			
		});
		final Container content = this.getContentPane();
		content.add(tabbedPane, BorderLayout.CENTER); // Les onglets au centre de l'IHM.
		content.add(bar, BorderLayout.SOUTH); // La barre en bas.
		new Thread() {
			
			@Override
			public final void run() {
				loadRooms(); // On charge les salles.
			}
			
		}.start();
	}
	
	/**
	 * Chargement des salles.
	 */
	
	private final void loadRooms() {
		final MessageDialog message = new MessageDialog(this, LanguageManager.getString("main.loadingrooms.title"), LanguageManager.getString("main.loadingrooms.message")); // Message d'attente.
		message.setVisible(true);
		rooms.clear(); // On enlève toutes les salles si il y en a.
		tabbedPane.removeAll(); // On enlève également tous les onglets affichés si il y en a.
		for(final File roomFile : ProjetBBQProf.getRoomDirectory().listFiles()) { // On regarde tous les fichiers contenus dans le dossier des salles de classe.
			try {
				if(!roomFile.getName().toLowerCase().endsWith(".xml")) { // On ne retient que les fichiers qui ont l'extension .xml.
					continue;
				}
				final Room room = new Room(); // On créé une salle de classe "blanche".
				final XMLError result = room.load(roomFile); // On tente de la charger.
				if(result.getInvalidParameters().length != 0) {
					throw new IllegalArgumentException("<html>" + LanguageManager.getString("main.loadingrooms.invalidroom", roomFile.getName(), Utils.join(", ", result.getInvalidParameters())) + "</html>"); // Si cela échoue, on déclenche une erreur.
				}
				rooms.add(room); // Et on ajoute la classe chargée dans la liste des salles de classe.
			}
			catch(final Exception ex) {
				message.dispose();
				ex.printStackTrace();
				JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			}
		}
		if(rooms.size() == 0) { // Si il n'y a pas de salles de classe chargées, on affiche un message d'erreur.
			message.dispose();
			JOptionPane.showMessageDialog(MainFrame.this, LanguageManager.getString("main.loadingrooms.error"), LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		for(final Room room : rooms) { // Pour chaque salle de classe, on créé l'onglet correspondant.
			final JScrollPane pane = new JScrollPane(new RoomPane(room, bar));
			pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			tabbedPane.add(room.name, pane);
		}
		tabbedPane.setSelectedIndex(0); // On va sur le premier onglet.
		message.dispose(); // On ferme le dialogue.
	}
	
	/**
	 * On créé le menu de cet IHM.
	 * 
	 * @return Le menu.
	 */
	
	public final JMenuBar createMenuBar() {
		final JMenuBar menu = new JMenuBar();
		/* Le menu rafraîchir : */
		final JMenuItem refresh = new JMenuItem(LanguageManager.getString("common.pc.plural.refresh"));
		refresh.addActionListener(new ActionListener() {
			
			/**
			 * On stop les requêtes et on les redémarre.
			 */

			@Override
			public final void actionPerformed(final ActionEvent event) {
				final RoomPane pane = (RoomPane)((JScrollPane)tabbedPane.getComponent(tabbedPane.getSelectedIndex())).getViewport().getView(); // On va rechercher la RoomPane actuelle.
				pane.stopRequests();
				pane.startRequests();
			}
			
		});
		refresh.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_refresh.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH))); // On doit redimensionner les icônes en 16x16.
		/* Le menu pour envoyer un message : */
		final JMenuItem sendMessage = new JMenuItem(LanguageManager.getString("common.pc.plural.message"));
		sendMessage.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				final Object[] data = Utils.createMessageDialog(null);
				if((boolean)data[0]) {
					ComputerFrame.createClientDialog(new Request(RequestType.MESSAGE, data[1].toString(), data[2].toString()), MainFrame.this, getComputers());
				}
			}
			
		});
		sendMessage.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_sendmessage.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		/* Le menu pour envoyer éteindre les PCs : */
		final JMenuItem shutdown = new JMenuItem(LanguageManager.getString("common.pc.plural.shutdown"));
		shutdown.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				ComputerFrame.createClientDialog(new Request(RequestType.SHUTDOWN), MainFrame.this, getComputers());
			}
			
		});
		shutdown.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_shutdown.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		/* Le menu pour redémarrer les PCs : */
		final JMenuItem restart = new JMenuItem(LanguageManager.getString("common.pc.plural.restart"));
		restart.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				ComputerFrame.createClientDialog(new Request(RequestType.RESTART), MainFrame.this, getComputers());
			}
			
		});
		restart.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_restart.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		/* Le menu pour déconnecter les PCs : */
		final JMenuItem logout = new JMenuItem(LanguageManager.getString("common.pc.plural.logout"));
		logout.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				ComputerFrame.createClientDialog(new Request(RequestType.LOGOUT), MainFrame.this, getComputers());
			}
			
		});
		logout.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_logout.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		/* Le menu pour verrouiller les PCs : */
		final JMenuItem lock = new JMenuItem(LanguageManager.getString("common.pc.plural.lock"));
		lock.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				ComputerFrame.createClientDialog(new Request(RequestType.LOCK), MainFrame.this, getComputers());
			}
			
		});
		lock.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_lock.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		/* Le menu pour déverrouiller les PCs : */
		final JMenuItem unlock = new JMenuItem(LanguageManager.getString("common.pc.plural.unlock"));
		unlock.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				ComputerFrame.createClientDialog(new Request(RequestType.UNLOCK), MainFrame.this, getComputers());
			}
			
		});
		unlock.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_unlock.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		/* Le menu pour ouvrir l'aide en ligne : */
		final JMenuItem onlineHelp = new JMenuItem(LanguageManager.getString("main.menu.help.onlinehelp"));
		onlineHelp.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				if(!Desktop.isDesktopSupported()) {
					return;
				}
				try {
					Desktop.getDesktop().browse(new URL("https://github.com/Skyost/ProjetBBQ/wiki").toURI());
				}
				catch(final Exception ex) {
					ex.printStackTrace();
				}
			}
			
		});
		onlineHelp.setIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_onlinehelp.png")));
		/* Le menu pour afficher la boîte À propos : */
		final JMenuItem about = new JMenuItem(LanguageManager.getString("main.menu.help.about"));
		about.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				new AboutFrame().setVisible(true); // Affiche la boîte À propos.
			}
			
		});
		about.setIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_about.png")));
		final JMenu edit = new JMenu(LanguageManager.getString("main.menu.edit"));
		edit.add(refresh);
		edit.add(sendMessage);
		edit.addSeparator();
		edit.add(shutdown);
		edit.add(restart);
		edit.add(logout);
		edit.addSeparator();
		edit.add(lock);
		edit.add(unlock);
		final JMenu help = new JMenu(LanguageManager.getString("main.menu.help"));
		help.add(onlineHelp);
		help.addSeparator();
		help.add(about);
		menu.add(edit);
		menu.add(help);
		return menu;
	}
	
	/**
	 * Permet d'obtenir tous les ordinateurs gérées par le logiciel, indépendemment de la salle.
	 * 
	 * @return Les ordinateurs gérées par le logiciel.
	 */
	
	private final Computer[] getComputers() {
		final List<Computer> computers = new ArrayList<Computer>();
		for(final Room room : rooms) {
			for(final Computer computer : room.computers) {
				computers.add(computer);
			}
		}
		return computers.toArray(new Computer[computers.size()]);
	}
	
	/**
	 * Permet de créer le titre en fonction de la salle actuelle.
	 * 
	 * @param roomName La salle actuelle.
	 * 
	 * @return Le titre.
	 */
	
	private final String buildTitle(final String roomName) {
		return ProjetBBQProf.APP_NAME + " v" + ProjetBBQProf.APP_VERSION + (roomName == null ? "" : " (" + roomName + ")");
	}
	
}