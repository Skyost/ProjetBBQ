package fr.isn.bbq.prof.frames;

import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.tasks.Client;
import fr.isn.bbq.prof.tasks.Client.ClientInterface;
import fr.isn.bbq.prof.utils.ClientRequests;
import fr.isn.bbq.prof.utils.ClientRequests.RequestType;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

/**
 * IHM d'un poste élève.
 */

public class ComputerFrame extends JFrame implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
	
	private Client client;
	private final Computer computer;
	private final JLabel lblScreenshot = new JLabel();
	private long refreshTime;
	
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
				stopRefreshingScreenshot();
			}
		});
		this.setJMenuBar(createMenuBar());
		
		lblScreenshot.setFont(lblScreenshot.getFont().deriveFont(Font.ITALIC));
		lblScreenshot.setHorizontalAlignment(SwingConstants.CENTER);
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
	}

	@Override
	public final void onError(final Computer computer, final Exception ex, final long responseTime) {
		ex.printStackTrace();
		onInterrupted(computer, responseTime);
	}

	@Override
	public final void onInterrupted(final Computer computer, final long time) {
		stopRefreshingScreenshot();
		if(time < refreshTime) {
			return;
		}
		lblScreenshot.setIcon(null);
		lblScreenshot.setText("Impossible de charger la capture d'écran !");
	}

	@Override
	public final void onWaiting() {} // Jamais appelé quand il n'y a qu'un ordinateur à joindre.
	
	private final void refreshScreenshot() {
		client = new Client(this, ClientRequests.createRequest(RequestType.FULL_SCREENSHOT, ProjetBBQProf.settings.uuid), new Computer[]{computer}, true);
		client.start();
	}
	
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
		final JMenuItem sendMessage = new JMenuItem("Envoyer un message...");
		sendMessage.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				final JSpinner spinner = new JSpinner();
				final List<Component> components = new ArrayList<Component>();
				components.add(new JLabel("Message :"));
				components.add(new JTextField());
				components.add(new JLabel("Durée d'affichage (en secondes) :"));
				components.add(spinner);
				spinner.setModel(new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1));
				final JFormattedTextField field = ((NumberEditor)spinner.getEditor()).getTextField();
				((NumberFormatter)field.getFormatter()).setAllowsInvalid(false);
				if(JOptionPane.showConfirmDialog(ComputerFrame.this, components.toArray(new Object[components.size()]), "Envoyer un message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
					// TODO: On envoie le message
				}
			}
			
		});
		final JMenu edit = new JMenu("Édition");
		edit.add(refresh);
		final JMenu computer = new JMenu(this.computer.name);
		computer.add(sendMessage);
		menu.add(edit);
		menu.add(computer);
		return menu;
	}

}