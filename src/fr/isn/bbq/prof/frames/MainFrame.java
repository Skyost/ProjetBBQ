package fr.isn.bbq.prof.frames;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.Room;
import fr.isn.bbq.prof.dialogs.MessageDialog;
import fr.isn.bbq.prof.frames.tabs.RoomPane;
import fr.isn.bbq.prof.utils.Request;
import fr.isn.bbq.prof.utils.StatusBar;
import fr.isn.bbq.prof.utils.Request.RequestType;

import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Toolkit;
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
		this.setTitle(ProjetBBQProf.APP_NAME + " v" + ProjetBBQProf.APP_VERSION);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/app_icon.png")));
		this.setSize(600, 400); // Par défaut, une taille de 600x400.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setJMenuBar(createMenuBar());
		tabbedPane.setBorder(BorderFactory.createEmptyBorder());
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
		final MessageDialog message = new MessageDialog(this, "Patientez...", "Chargement des salles, veuillez patienter..."); // Message d'attente.
		message.setVisible(true);
		rooms.clear(); // On enlève toutes les salles si il y en a.
		tabbedPane.removeAll(); // On enlève également tous les onglets affichés si il y en a.
		for(final File roomFile : ProjetBBQProf.getRoomDirectory().listFiles()) { // On regarde tous les fichiers contenus dans le dossier des salles de classe.
			try {
				if(!roomFile.getName().toLowerCase().endsWith(".xml")) { // On ne retient que les fichiers qui ont l'extension .xml.
					continue;
				}
				final Room room = new Room(); // On créé une salle de classe "blanche".
				if(!room.load(new String(Files.readAllBytes(roomFile.toPath())))) { // On tente de la charger.
					throw new IllegalArgumentException("Le fichier \"" + roomFile.getName() + "\" est invalide !"); // Si cela échoue, on déclenche une erreur.
				}
				rooms.add(room); // Et on ajoute la classe chargée dans la liste des salles de classe.
			}
			catch(final Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(MainFrame.this, "Le fichier \"" + roomFile.getName() + "\" n'est pas un fichier XML valide. Veuillez consulter l'aide en ligne.", "Erreur !", JOptionPane.ERROR_MESSAGE);
			}
		}
		message.dispose(); // On ferme le dialogue.
		if(rooms.size() == 0) { // Si il n'y a pas de salles de classe chargées, on affiche un message d'erreur.
			JOptionPane.showMessageDialog(MainFrame.this, "Pas de salle valide ajout�e. Veuillez consulter l'aide en ligne.", "Erreur !", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		for(final Room room : rooms) { // Pour chaque salle de classe, on créé l'onglet correspondant.
			final JScrollPane pane = new JScrollPane(new RoomPane(room, bar));
			pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			tabbedPane.add(room.name, pane);
		}
		tabbedPane.setSelectedIndex(0); // On va sur le premier onglet.
		currentIndex = tabbedPane.getSelectedIndex();
		tabbedPane.addChangeListener(new ChangeListener() {

			/**
			 * On écoute tous les changements d'onglet de la part de l'utilisateur.
			 */
			
			@Override
			public final void stateChanged(final ChangeEvent event) {
				if(currentIndex != -1) { // Si l'index est défini, on annule toutes les requêtes.
					((RoomPane)((JScrollPane)tabbedPane.getComponent(tabbedPane.getSelectedIndex())).getViewport().getView()).stopRequests();
				}
				currentIndex = tabbedPane.getSelectedIndex(); // Et on redéfini l'index au nouvel index.
			}
			
		});
	}
	
	/**
	 * On créé le menu de cet IHM.
	 * 
	 * @return Le menu.
	 */
	
	public final JMenuBar createMenuBar() {
		final List<Computer> computers = new ArrayList<Computer>();
		for(final Room room : rooms) {
			for(final Computer computer : room.computers) {
				computers.add(computer);
			}
		}
		final JMenuBar menu = new JMenuBar();
		final JMenuItem refresh = new JMenuItem("Rafraîchir les miniatures");
		refresh.addActionListener(new ActionListener() {
			
			/**
			 * On stop les requêtes et on les redémarre.
			 */

			@Override
			public final void actionPerformed(final ActionEvent event) {
				final RoomPane pane = (RoomPane)((JScrollPane)tabbedPane.getComponent(tabbedPane.getSelectedIndex())).getViewport().getView();
				pane.stopRequests();
				pane.startRequests();
			}
			
		});
		refresh.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_refresh.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		final JMenuItem sendMessage = new JMenuItem("Envoyer un message");
		sendMessage.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				final JTextField textField = new JTextField();
				final JSpinner spinner = new JSpinner();
				final List<Component> components = new ArrayList<Component>(); // Composants de la boîte de dialogue.
				components.add(new JLabel("Message :"));
				components.add(textField);
				components.add(new JLabel("Durée d'affichage (en secondes) :"));
				components.add(spinner);
				spinner.setModel(new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1));
				final JFormattedTextField field = ((NumberEditor)spinner.getEditor()).getTextField();
				((NumberFormatter)field.getFormatter()).setAllowsInvalid(false);
				if(JOptionPane.showConfirmDialog(MainFrame.this, components.toArray(new Object[components.size()]), "Envoyer un message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
					ComputerFrame.createClientDialog(new Request(RequestType.MESSAGE, ProjetBBQProf.settings.uuid, textField.getText(), String.valueOf(spinner.getValue())), MainFrame.this, computers);
				}
			}
			
		});
		sendMessage.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_sendmessage.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		final JMenuItem shutdown = new JMenuItem("Éteindre les PCs");
		shutdown.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				ComputerFrame.createClientDialog(new Request(RequestType.SHUTDOWN, ProjetBBQProf.settings.uuid), MainFrame.this, computers);
			}
			
		});
		shutdown.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_shutdown.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		final JMenuItem restart = new JMenuItem("Redémarrer les PCs");
		restart.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				ComputerFrame.createClientDialog(new Request(RequestType.RESTART, ProjetBBQProf.settings.uuid), MainFrame.this, computers);
			}
			
		});
		restart.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_restart.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		final JMenuItem logout = new JMenuItem("Déconnecter les PCs");
		logout.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				ComputerFrame.createClientDialog(new Request(RequestType.LOGOUT, ProjetBBQProf.settings.uuid), MainFrame.this, computers);
			}
			
		});
		logout.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_logout.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		final JMenuItem lock = new JMenuItem("Verrouiller les PCs");
		lock.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				ComputerFrame.createClientDialog(new Request(RequestType.LOCK, ProjetBBQProf.settings.uuid), MainFrame.this, computers);
			}
			
		});
		lock.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_lock.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		final JMenuItem unlock = new JMenuItem("Déverrouiller les PCs");
		unlock.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				ComputerFrame.createClientDialog(new Request(RequestType.UNLOCK, ProjetBBQProf.settings.uuid), MainFrame.this, computers);
			}
			
		});
		unlock.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_unlock.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		final JMenuItem onlineHelp = new JMenuItem("Aide en ligne...");
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
		final JMenuItem about = new JMenuItem("À propos...");
		about.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				// TODO: IHM "À propos"
			}
			
		});
		about.setIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_about.png")));
		final JMenu edit = new JMenu("Édition");
		edit.add(refresh);
		edit.add(sendMessage);
		edit.addSeparator();
		edit.add(shutdown);
		edit.add(restart);
		edit.add(logout);
		edit.addSeparator();
		edit.add(lock);
		edit.add(unlock);
		final JMenu help = new JMenu("Aide");
		help.add(onlineHelp);
		help.addSeparator();
		help.add(about);
		menu.add(edit);
		menu.add(help);
		return menu;
	}
	
}