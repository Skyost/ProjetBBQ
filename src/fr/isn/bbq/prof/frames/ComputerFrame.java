package fr.isn.bbq.prof.frames;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.dialogs.MessageDialog;
import fr.isn.bbq.prof.tasks.Client;
import fr.isn.bbq.prof.tasks.Client.ClientInterface;
import fr.isn.bbq.prof.utils.Request;
import fr.isn.bbq.prof.utils.Request.RequestType;

import java.awt.Component;
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
import javax.swing.text.NumberFormatter;

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
		this.setTitle(computer.name + " (" + computer.ip + ":" + computer.port + ")");
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
		this.setJMenuBar(createMenuBar());
		lblScreenshot.setFont(lblScreenshot.getFont().deriveFont(Font.ITALIC));
		lblScreenshot.setHorizontalAlignment(SwingConstants.CENTER);
		if(ImageIO.getWriterFormatNames().length > 0) { // Si il n'est pas possible d'enregistrer une image, on n'ajoute pas de menu popup.
			lblScreenshot.setComponentPopupMenu(createScreenshotMenu());
		}
		getContentPane().add(new JScrollPane(lblScreenshot), BorderLayout.CENTER);
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
	public final void onSuccess(final Computer computer, final Object returned, final long responseTime) {
		stopRefreshingScreenshot();
		if(responseTime < refreshTime) {
			return;
		}
		if(!(returned instanceof Image)) {
			return;
		}
		lblScreenshot.setIcon(new ImageIcon((BufferedImage)returned));
		lblScreenshot.setText(null);
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
		client = new Client(this, new Request(RequestType.FULL_SCREENSHOT, ProjetBBQProf.settings.uuid), new Computer[]{computer}, true);
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
	 * On créé le menu de cet IHM.
	 * 
	 * @return Le menu.
	 */
	
	public final JMenuBar createMenuBar() {
		final JMenuBar menu = new JMenuBar();
		final JMenuItem refresh = new JMenuItem("Rafraîchir la capture d'écran");
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				stopRefreshingScreenshot();
				refreshScreenshot();
			}
			
		});
		refresh.setIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_refresh.png")));
		final JMenuItem sendMessage = new JMenuItem("Envoyer un message...");
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
				if(JOptionPane.showConfirmDialog(ComputerFrame.this, components.toArray(new Object[components.size()]), "Envoyer un message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
					new Client(new ClientInterface() {
						
						public static final int DIALOG_TIME = 5;
						
						private final MessageDialog dialog = new MessageDialog(ComputerFrame.this, "Envoi du message", "Connexion à l'ordinateur...");

						@Override
						public final void connection(final Computer computer, final long time) {
							dialog.setVisible(true);
						}

						@Override
						public final void onSuccess(final Computer computer, final Object returned, final long responseTime) {
							dialog.setMessage("<html>Envoi effectué !<br/>Ce message se fermera dans " + DIALOG_TIME + " secondes.</html>");
							closeDialog();
						}

						@Override
						public final void onError(final Computer computer, final Exception ex, final long responseTime) {
							dialog.setMessage("<html>" + ex.getMessage() + "<br/>Ce message se fermera dans " + DIALOG_TIME + " secondes.</html>");
							closeDialog();
						}

						@Override
						public final void onInterrupted(final Computer computer, final long time) {
							dialog.setMessage("<html>Envoi interrompu.<br/>Ce message se fermera dans " + DIALOG_TIME + " secondes.</html>");
							closeDialog();
						}

						@Override
						public final void onWaiting() {}
						
						/**
						 * Ferme la boîte de dialogue en attendant.
						 */
						
						private final void closeDialog() {
							new Timer().schedule(new TimerTask() {
								
								@Override
								public final void run() {
									dialog.dispose();
								}
									
							}, DIALOG_TIME * 1000);
						}
						
					}, new Request(RequestType.MESSAGE, textField.getText(), String.valueOf(spinner.getValue())), new Computer[]{computer}, true);
				}
			}
			
		});
		sendMessage.setIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_sendmessage.png")));
		final JMenu computer = new JMenu(this.computer.name);
		computer.add(refresh);
		computer.add(sendMessage);
		menu.add(computer);
		return menu;
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
				final String[] availableWriters = ImageIO.getWriterFormatNames();
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

}