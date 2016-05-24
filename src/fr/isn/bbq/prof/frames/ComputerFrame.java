package fr.isn.bbq.prof.frames;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.dialogs.MessageDialog;
import fr.isn.bbq.prof.tasks.Client;
import fr.isn.bbq.prof.tasks.Client.ClientInterface;
import fr.isn.bbq.prof.utils.Request;
import fr.isn.bbq.prof.utils.Utils;
import fr.isn.bbq.prof.utils.Request.RequestType;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * IHM d'un poste élève.
 */

public class ComputerFrame extends JFrame implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Le client utilisé pour demander des captures d'écran.
	 */
	
	private Client client;
	
	/**
	 * L'ordinateur affiché par cet IHM.
	 */
	
	private final Computer computer;
	
	/**
	 * Utilisé pour afficher la capture d'écran.
	 * <br>Moyen pour accélerer le défilement dans le panel : http://stackoverflow.com/questions/5583495/how-do-i-speed-up-the-scroll-speed-in-a-jscrollpane-when-using-the-mouse-wheel.
	 */
	
	private final JLabel lblScreenshot = new JLabel();
	
	/**
	 * La dernière fois que la capture d'écran a été rafraichie.
	 */
	
	private long refreshTime;
	
	/**
	 * Construction de cet IHM.
	 * 
	 * @param computer L'ordinateur correspondant.
	 */
	
	public ComputerFrame(final Computer computer) {
		this.computer = computer;
		this.setTitle(buildTitle(null));
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/app_icon.png")));
		this.setSize(600, 400); // Par défaut, une taille de 600x400.
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public final void windowClosing(final WindowEvent windowEvent) {
				stopRefreshingScreenshot(); // On annule la requête si l'IHM est fermé.
			}
			
		});
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		lblScreenshot.setFont(lblScreenshot.getFont().deriveFont(Font.ITALIC));
		lblScreenshot.setHorizontalAlignment(SwingConstants.CENTER);
		if(ImageIO.getWriterFormatNames().length > 0) { // Si il n'est pas possible d'enregistrer une image, on n'ajoute pas de menu popup.
			lblScreenshot.setComponentPopupMenu(createScreenshotMenu());
		}
		final Container container = this.getContentPane();
		container.add(createToolbar(), BorderLayout.NORTH);
		container.add(new JScrollPane(lblScreenshot), BorderLayout.CENTER);
		refreshScreenshot();
	}

	@Override
	public final void connection(final Computer computer, final long time) {
		if(time < refreshTime) {
			return;
		}
		lblScreenshot.setIcon(null);
		lblScreenshot.setText("Chargement de la capture d'écran...");
	}

	@Override
	public final void onSuccess(final Computer computer, final Object... returned) {
		stopRefreshingScreenshot();
		final long responseTime = (long)returned[1];
		if(responseTime < refreshTime) {
			return;
		}
		if(!(returned[returned.length - 1] instanceof Image)) {
			return;
		}
		lblScreenshot.setIcon(new ImageIcon((BufferedImage)returned[returned.length - 1]));
		lblScreenshot.setText(null);
		this.setTitle(buildTitle(returned[0].toString()));
	}

	@Override
	public final void onError(final Computer computer, final Exception ex, final long responseTime) {
		ex.printStackTrace();
		if(responseTime < refreshTime) {
			return;
		}
		lblScreenshot.setIcon(null);
		lblScreenshot.setText("<html>Impossible de charger la capture d'écran !<br/>Raison : " + ex.getMessage() + "</html>");
	}

	@Override
	public final void onInterrupted(final Computer computer, final long time) {
		stopRefreshingScreenshot();
		if(time < refreshTime) {
			return;
		}
		lblScreenshot.setIcon(null);
		lblScreenshot.setText("Chargement annulé.");
	}
	
	@Override
	public final void onWaiting() {} // Jamais appelé quand il n'y a qu'un ordinateur à joindre.
	
	/**
	 * Rafraîchit la capture d'écran.
	 */
	
	private final void refreshScreenshot() {
		if(client != null) {
			stopRefreshingScreenshot();
		}
		client = new Client(this, new Request(RequestType.FULL_SCREENSHOT), new Computer[]{computer}, true);
		client.start();
	}
	
	/**
	 * Arrête le rafraichissement de la capture d'écran.
	 */
	
	private final void stopRefreshingScreenshot() {
		if(client != null) {
			client.stopRequests();
			client = null;
		}
	}
	
	/**
	 * Permet de formatter le titre de l'IHM.
	 * 
	 * @param username Le nom d'utilisateur envoyé par l'ordinateur distant.
	 * 
	 * @return Le titre formatté.
	 */
	
	private final String buildTitle(final String username) {
		return computer.name + " (" + computer.ip + ":" + computer.port + ")" + (username == null ? "" : " → " + username);
	}
	
	/**
	 * On créé le toolbar de cet IHM.
	 * 
	 * @return Le toolbar.
	 */
	
	public final JToolBar createToolbar() {
		final JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false); // On ne veut pas qu'elle se détache de l'IHM.
		/* Le bouton rafraîchir : */
		final JButton refresh = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_refresh.png")));
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				stopRefreshingScreenshot();
				refreshScreenshot();
			}
			
		});
		refresh.setToolTipText("Rafraîchir la capture d'écran");
		/* Le bouton pour envoyer un message : */
		final JButton sendMessage = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_sendmessage.png")));
		sendMessage.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				final Object[] dialogData = Utils.createMessageDialog(ComputerFrame.this);
				if((boolean)dialogData[0]) { // Cf. Documentation de la fonction Utils.createMessageDialog(...).
					final Component[] components = (Component[])dialogData[1];
					createClientDialog(new Request(RequestType.MESSAGE, ((JTextField)components[0]).getText(), String.valueOf(((JSpinner)components[1]).getValue())), ComputerFrame.this, computer);
				}
			}
			
		});
		sendMessage.setToolTipText("Envoyer un message");
		/* Le bouton pour envoyer éteindre le PC : */
		final JButton shutdown = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_shutdown.png")));
		shutdown.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				createClientDialog(new Request(RequestType.SHUTDOWN));
			}
			
		});
		shutdown.setToolTipText("Éteindre le PC");
		/* Le bouton pour redémarrer le PC : */
		final JButton restart = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_restart.png")));
		restart.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				createClientDialog(new Request(RequestType.RESTART));
			}
			
		});
		restart.setToolTipText("Redémarrer le PC");
		/* Le bouton pour déconnecter le PC : */
		final JButton logout = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_logout.png")));
		logout.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				createClientDialog(new Request(RequestType.LOGOUT));
			}
			
		});
		logout.setToolTipText("Déconnecter le PC");
		/* Le bouton pour verrouiller le PC : */
		final JButton lock = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_lock.png")));
		lock.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				createClientDialog(new Request(RequestType.LOCK));
			}
			
		});
		lock.setToolTipText("Verrouiller le PC");
		/* Le bouton pour déverrouiller le PC : */
		final JButton unlock = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_unlock.png")));
		unlock.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				createClientDialog(new Request(RequestType.UNLOCK));
			}
			
		});
		unlock.setToolTipText("Déverrouiller le PC");
		/* Puis on ajoute les différents boutons : */
		toolbar.add(refresh);
		toolbar.add(sendMessage);
		toolbar.addSeparator();
		toolbar.add(shutdown);
		toolbar.add(restart);
		toolbar.add(logout);
		toolbar.addSeparator();
		toolbar.add(lock);
		toolbar.add(unlock);
		return toolbar;
	}
	
	/**
	 * On créé le popup menu de la capture d'écran.
	 * 
	 * @return Le popup menu.
	 */
	
	public final JPopupMenu createScreenshotMenu() {
		final JPopupMenu popup = new JPopupMenu();
		final JMenuItem save = new JMenuItem("Enregistrer la capture d'écran...");
		save.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				final String[] availableWriters = ImageIO.getWriterFormatNames(); // On liste les différentes extensions disponibles.
				final List<String> addedWriters = new ArrayList<String>();
				final JFileChooser chooser = new JFileChooser();
				for(int i = 0; i != availableWriters.length; i++) {
					final String extension = availableWriters[i].toLowerCase();
					if(addedWriters.contains(extension)) { // Si l'extension est déjà ajoutée, on ne l'ajoute pas à nouveau.
						continue;
					}
					if(i == 0) { // On ajoute la première extension comme extension par défaut, les autres sont ajoutées après.
						chooser.setFileFilter(new FileNameExtensionFilter("Image " + extension.toUpperCase(), extension));
					}
					else {
						chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image " + extension.toUpperCase(), extension));
					}
					addedWriters.add(extension); // On ajoute l'extension.
				}
				chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter()); // On n'accepte pas tous les fichiers.
				chooser.setMultiSelectionEnabled(false); // Une seule selection.
				if(chooser.showSaveDialog(ComputerFrame.this) == JFileChooser.APPROVE_OPTION) {
					try {
						final String extension = ((FileNameExtensionFilter)chooser.getFileFilter()).getExtensions()[0]; // Récupération, de l'extension.
						File output = chooser.getSelectedFile();
						if(!output.getName().endsWith("." + extension)) { // Si l'extension n'est pas ajoutée à la fin du fichier, on l'ajoute.
							output = new File(output.getPath() + "." + extension);
						}
						/* Transformation de l'Icon du JLabel en BufferedImage : */
						final Icon screenshot = lblScreenshot.getIcon();
						final BufferedImage image = new BufferedImage(screenshot.getIconWidth(), screenshot.getIconHeight(), BufferedImage.TYPE_INT_RGB);
						final Graphics graphics = image.createGraphics();
						screenshot.paintIcon(null, graphics, 0, 0);
						graphics.dispose();
						ImageIO.write(image, extension, output); // Puis on enregistre le tout.
					}
					catch(final Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(ComputerFrame.this, "Impossible d'enregistrer la capture d'écran.", "Erreur !", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
		});
		save.setIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_save.png")));
		popup.add(save);
		return popup;
	}
	
	/**
	 * Permet de créer le dialogue d'attente et d'envoyer une requête.
	 * 
	 * @param request La requête.
	 */
	
	private final void createClientDialog(final Request request) {
		createClientDialog(request, ComputerFrame.this, computer);
	}
	
	/**
	 * Permet de créer le dialogue d'attente et d'envoyer une requête.
	 * 
	 * @param request La requête.
	 * @param parent L'IHM parent.
	 * @param computers Les ordinateurs qui doivent recevoir cette requête.
	 */
	
	public static final void createClientDialog(final Request request, final JFrame parent, final Computer... computers) {
		final List<Computer> joinedComputers = new ArrayList<Computer>(); // La liste des ordinateurs joints.
		new Client(new ClientInterface() { // On créé le client.
			
			/** 
			 * Temps pendant lequel le dialogue est affiché.
			 */
			
			private static final int DIALOG_TIME = 5;
			
			/**
			 * Le message d'attente du dialogue.
			 */
			
			private static final String CLOSING_MESSAGE = "Ce message se fermera dans " + DIALOG_TIME + " seconde(s)...";
			
			/**
			 * Le dialogue d'attente.
			 */
			
			private final MessageDialog dialog = new MessageDialog(parent, "Envoi de la requête", "");

			@Override
			public final void connection(final Computer computer, final long time) {
				dialog.setMessage("<span style=\"font-weight: bold;\">[" + computer.name + "]</span><span> Connexion à l'ordinateur...</span>");
				dialog.setVisible(true);
			}

			@Override
			public final void onSuccess(final Computer computer, final Object... returned) {
				dialog.setMessage("<span style=\"font-weight: bold;\">[" + computer.name + "]</span><span> Action effectuée !</span>");
				joinedComputers.add(computer);
				if(computers.length == joinedComputers.size()) {
					closeDialog();
				}
			}

			@Override
			public final void onError(final Computer computer, final Exception ex, final long responseTime) {
				ex.printStackTrace();
				dialog.setMessage("<span style=\"font-weight: bold;\">[" + computer.name + "]</span><span> Erreur : " + ex.getMessage() + "</span>");
				joinedComputers.add(computer);
				if(computers.length == joinedComputers.size()) {
					closeDialog();
				}
			}

			@Override
			public final void onInterrupted(final Computer computer, final long time) {
				dialog.setMessage("<span style=\"font-weight: bold;\">[" + computer.name + "]</span><span> Envoi interrompu.</span>");
				joinedComputers.add(computer);
				if(computers.length == joinedComputers.size()) {
					closeDialog();
				}
			}

			@Override
			public final void onWaiting() {}
			
			/**
			 * Ferme la boîte de dialogue en attendant.
			 */
			
			private final void closeDialog() {
				dialog.setMessage(dialog.getMessage().replace("<html>", "").replace("</html>", "") + "<br>" + CLOSING_MESSAGE);
				new Timer().schedule(new TimerTask() {
					
					@Override
					public final void run() {
						dialog.dispose();
					}
						
				}, DIALOG_TIME * 1000);
			}
			
		}, request, computers, true).start();
	}

}