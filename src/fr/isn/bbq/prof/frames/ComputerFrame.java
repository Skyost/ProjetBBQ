package fr.isn.bbq.prof.frames;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.dialogs.MessageDialog;
import fr.isn.bbq.prof.tasks.Client;
import fr.isn.bbq.prof.tasks.Client.ClientInterface;
import fr.isn.bbq.prof.utils.LanguageManager;
import fr.isn.bbq.prof.utils.Request;
import fr.isn.bbq.prof.utils.Utils;
import fr.isn.bbq.prof.utils.Request.RequestType;

import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
	
	private long refreshTime = -1l;
	
	/**
	 * Ci-dessous, les différents champs utilisés pour le redimensionnement :
	 */
	
	private final float defaultFontSize;
	private BufferedImage currentScreenshot;
	
	/**
	 * Construction de cet IHM.
	 * 
	 * @param computer L'ordinateur correspondant.
	 */
	
	public ComputerFrame(final Computer computer) {
		this.computer = computer;
		this.setTitle(buildTitle(null));
		this.setIconImages(ProjetBBQProf.icons);
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
		defaultFontSize = lblScreenshot.getFont().getSize2D();
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
		lblScreenshot.setText(LanguageManager.getString("computer.status.connection"));
	}

	@Override
	public final void onSuccess(final Computer computer, final Object... returned) {
		stopRefreshingScreenshot();
		final long responseTime = (long)returned[1];
		if(responseTime < refreshTime) {
			return;
		}
		if(!(returned[returned.length - 1] instanceof Image)) {
			onError(computer, new IllegalArgumentException("Pas d'image renvoyée.") ,responseTime);
			return;
		}
		refreshTime = responseTime;
		currentScreenshot = (BufferedImage)returned[returned.length - 1];
		lblScreenshot.setIcon(new ImageIcon(currentScreenshot));
		lblScreenshot.setText(null);
		this.setTitle(buildTitle(returned[0].toString()));
	}

	@Override
	public final void onError(final Computer computer, final Exception ex, final long responseTime) {
		ex.printStackTrace();
		if(responseTime < refreshTime) {
			return;
		}
		refreshTime = responseTime;
		lblScreenshot.setIcon(null);
		lblScreenshot.setText("<html>" + LanguageManager.getString("computer.status.error", ex.getMessage()) + "</html>");
		this.setTitle(buildTitle(null));
	}

	@Override
	public final void onInterrupted(final Computer computer, final long time) {
		stopRefreshingScreenshot();
		if(time < refreshTime) {
			return;
		}
		refreshTime = time;
		lblScreenshot.setIcon(null);
		lblScreenshot.setText(LanguageManager.getString("computer.status.interrupted"));
		this.setTitle(buildTitle(null));
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
		final StringBuilder builder = new StringBuilder();
		builder.append(computer.name + " (" + computer.ip + ":" + computer.port + ")");
		if(username != null) {
			builder.append(" → " + username);
		}
		if(refreshTime != -1l) {
			final Calendar current = Calendar.getInstance();
			current.setTimeInMillis(refreshTime);
			builder.append(" (" + Utils.addZeroIfMissing(current.get(Calendar.DAY_OF_MONTH)) + "/");
			builder.append(Utils.addZeroIfMissing(current.get(Calendar.MONTH) + 1) + "/"); 
			builder.append(Utils.addZeroIfMissing(current.get(Calendar.YEAR)) + " ");
			builder.append(Utils.addZeroIfMissing(current.get(Calendar.HOUR_OF_DAY)) + ":");
			builder.append(Utils.addZeroIfMissing(current.get(Calendar.MINUTE)) + ":");
			builder.append(Utils.addZeroIfMissing(current.get(Calendar.SECOND)) + ")");
		}
		return builder.toString();
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
		refresh.setToolTipText(LanguageManager.getString("common.pc.singular.refresh.screenshot"));
		/* Le bouton pour envoyer un message : */
		final JButton sendMessage = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_sendmessage.png")));
		sendMessage.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				final Object[] data = Utils.createMessageDialog(null);
				if((boolean)data[0]) {
					ComputerFrame.createClientDialog(new Request(RequestType.MESSAGE, data[1].toString(), data[2].toString()), ComputerFrame.this, computer);
				}
			}
			
		});
		sendMessage.setToolTipText(LanguageManager.getString("common.pc.singular.message"));
		/* Le bouton pour envoyer éteindre le PC : */
		final JButton shutdown = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_shutdown.png")));
		shutdown.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				createClientDialog(new Request(RequestType.SHUTDOWN));
			}
			
		});
		shutdown.setToolTipText(LanguageManager.getString("common.pc.singular.shutdown"));
		/* Le bouton pour redémarrer le PC : */
		final JButton restart = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_restart.png")));
		restart.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				createClientDialog(new Request(RequestType.RESTART));
			}
			
		});
		restart.setToolTipText(LanguageManager.getString("common.pc.singular.restart"));
		/* Le bouton pour déconnecter le PC : */
		final JButton logout = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_logout.png")));
		logout.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				createClientDialog(new Request(RequestType.LOGOUT));
			}
			
		});
		logout.setToolTipText(LanguageManager.getString("common.pc.singular.logout"));
		/* Le bouton pour verrouiller le PC : */
		final JButton lock = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_lock.png")));
		lock.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				createClientDialog(new Request(RequestType.LOCK));
			}
			
		});
		lock.setToolTipText(LanguageManager.getString("common.pc.singular.lock"));
		/* Le bouton pour déverrouiller le PC : */
		final JButton unlock = new JButton(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_unlock.png")));
		unlock.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				createClientDialog(new Request(RequestType.UNLOCK));
			}
			
		});
		unlock.setToolTipText(LanguageManager.getString("common.pc.singular.unlock"));
		/* Zoom sur la capture d'écran : */
		final int minimum = 0; // Le minimum est 0.
		final int maximum = 100; // Le maximum est 100.
		final int defaultValue = maximum / 2; // 50 par défaut (on affichera 50*2=100), ce qui nous autorise à avoir une image deux fois plus grandes.
		final int pas = 5; // De combien est augmenté ou diminué le zoom avec la molette.
		
		
		final JLabel zoomDisplayed = new JLabel();
		final JSlider zoom = new JSlider(minimum, maximum, defaultValue);
		zoom.addChangeListener(new ChangeListener() {

			@Override
			public final void stateChanged(final ChangeEvent event) {
				/* Le facteur de zoom. Par défaut, égal à 50/(100/2)=50/50=1. Le facteur sera donc toujours compris entre 0 et 2 (1 étant la valeur par défaut). */
				final float factor = (float)zoom.getValue() / (float)defaultValue;
				
				zoomDisplayed.setText(LanguageManager.getString("computer.zoom", zoom.getValue() * 2)); // On met à jour le texte en fonction du facteur de zoom.
				if(factor == 0f) { // On a pas envie de faire disparaitre complétement le texte ou l'image.
					return;
				}
				if(lblScreenshot.getIcon() == null) { // Pas de screen téléchargé, que du texte.
					lblScreenshot.setFont(lblScreenshot.getFont().deriveFont(defaultFontSize * factor)); // On applique une taille (par défaut) de 12f * facteur.
					return;
				} // Un screen et donc pas de texte :
				if(factor == 1f) { // Si on a un facteur de 1, pas besoin de redimensionner :
					lblScreenshot.setIcon(new ImageIcon(currentScreenshot));
					return;
				}
				final AffineTransform transform = new AffineTransform();
				transform.scale(factor, factor); // On transforme l'image avec notre facteur.
				lblScreenshot.setIcon(new ImageIcon(new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(currentScreenshot, null))); // Et on applique l'image.
			}
			
		});
		this.addMouseWheelListener(new MouseWheelListener() { // On ajoute un évènement global qui écoute la molette de la souris :

			@Override
			public final void mouseWheelMoved(final MouseWheelEvent event) {
				/* event.getWheelRotation() est supérieur à 0 si il y a une rotation vers le haut et inférieur à 0 si vers le bas. En fonction de ceci, on applique un pas positif ou négatif pour zoomer ou dézoomer. */
				zoom.setValue(zoom.getValue() + (event.getWheelRotation() < 0 ? pas : (-1) * pas));
			}
			
		});
		zoomDisplayed.setText(LanguageManager.getString("computer.zoom", zoom.getValue() * 2));
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
		toolbar.addSeparator();
		toolbar.add(zoom);
		toolbar.add(zoomDisplayed);
		return toolbar;
	}
	
	/**
	 * On créé le popup menu de la capture d'écran.
	 * 
	 * @return Le popup menu.
	 */
	
	public final JPopupMenu createScreenshotMenu() {
		final JPopupMenu popup = new JPopupMenu();
		final JMenuItem save = new JMenuItem(LanguageManager.getString("computer.save.popup"));
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
					final FileNameExtensionFilter filter = new FileNameExtensionFilter(LanguageManager.getString("computer.save.format", extension.toUpperCase()), extension);
					if(i == 0) { // On ajoute la première extension comme extension par défaut, les autres sont ajoutées après.
						chooser.setFileFilter(filter);
					}
					else {
						chooser.addChoosableFileFilter(filter);
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
						JOptionPane.showMessageDialog(ComputerFrame.this, LanguageManager.getString("computer.save.error"), LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
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
		final JList<String> log = computers.length > 1 ? new JList<String>() : null; // Utilisé pour afficher des informations (si plusieurs ordinateurs).
		final DefaultListModel<String> model = new DefaultListModel<String>() {
			
			private static final long serialVersionUID = 1L;
			
			/* On souhaite utiliser du HTML dans la liste : */

			@Override
			public final void addElement(final String element) {
				super.addElement("<html>" + element + "</html>");
			}
			
			@Override
			public final void setElementAt(final String element, final int index) {
				super.setElementAt("<html>" + element + "</html>", index);
			}
			
		};
		if(log != null) {
			log.setModel(model); // Swing utilise MVC.
			for(final Computer computer : computers) {
				model.addElement(LanguageManager.getString("common.pc.dialog.message.preparing", computer.name)); // Texte par défaut.
			}
		}
		
		final List<Computer> joinedComputers = new ArrayList<Computer>(); // La liste des ordinateurs joints.
		new Client(new ClientInterface() { // On créé le client.
			
			/** 
			 * Temps pendant lequel le dialogue est affiché.
			 */
			
			private static final int DIALOG_TIME = 5;
			
			/**
			 * Le dialogue d'attente (message différent si il y a plusieurs ordinateurs ou non).
			 */
			
			private final MessageDialog dialog = new MessageDialog(parent, LanguageManager.getString("common.pc.dialog.title"), log == null ? LanguageManager.getString("common.pc.dialog.message.preparing", computers[0].name) : LanguageManager.getString("common.pc.dialog.message.plural"), log);

			@Override
			public final void connection(final Computer computer, final long time) {
				if(log == null) { // Si il n'y a qu'un ordinateur :
					dialog.setMessage(LanguageManager.getString("common.pc.dialog.message.connection", computer.name));
				}
				else {
					model.setElementAt(LanguageManager.getString("common.pc.dialog.message.connection", computer.name), Arrays.asList(computers).indexOf(computer));
				}
				dialog.setVisible(true);
			}

			@Override
			public final void onSuccess(final Computer computer, final Object... returned) {
				if(log == null) { 
					dialog.setMessage(LanguageManager.getString("common.pc.dialog.message.success", computer.name));
				}
				else {
					model.setElementAt(LanguageManager.getString("common.pc.dialog.message.success", computer.name), Arrays.asList(computers).indexOf(computer));
					dialog.pack(); // On doit adapter la taille du dialogue à chaque fois qu'on change un élément de la liste.
				}
				joinedComputers.add(computer);
				if(computers.length == joinedComputers.size()) {
					closeDialog();
				}
			}

			@Override
			public final void onError(final Computer computer, final Exception ex, final long responseTime) {
				ex.printStackTrace();
				if(log == null) { 
					dialog.setMessage(LanguageManager.getString("common.pc.dialog.message.error", computer.name, ex.getMessage()));
				}
				else {
					model.setElementAt(LanguageManager.getString("common.pc.dialog.message.error", computer.name, ex.getMessage()), Arrays.asList(computers).indexOf(computer));
					dialog.pack();
				}
				joinedComputers.add(computer);
				if(computers.length == joinedComputers.size()) {
					closeDialog();
				}
			}

			@Override
			public final void onInterrupted(final Computer computer, final long time) {
				if(log == null) { 
					dialog.setMessage(LanguageManager.getString("common.pc.dialog.message.interrupted", computer.name));
				}
				else {
					model.setElementAt(LanguageManager.getString("common.pc.dialog.message.interrupted", computer.name), Arrays.asList(computers).indexOf(computer));
					dialog.pack();
				}
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
				dialog.setMessage((log == null ? dialog.getMessage().replace("<html>", "").replace("</html>", "") + "<br>" : "") + LanguageManager.getString("common.pc.dialog.footer", DIALOG_TIME));
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