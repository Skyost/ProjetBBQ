package fr.isn.bbq.prof.frames.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.jtattoo.plaf.smart.SmartLookAndFeel;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.Room;
import fr.isn.bbq.prof.frames.ComputerFrame;
import fr.isn.bbq.prof.tasks.Client;
import fr.isn.bbq.prof.tasks.Client.ClientInterface;
import fr.isn.bbq.prof.utils.Request;
import fr.isn.bbq.prof.utils.StatusBar;
import fr.isn.bbq.prof.utils.Utils;
import fr.isn.bbq.prof.utils.WrapLayout;
import fr.isn.bbq.prof.utils.Request.RequestType;

/**
 * L'onglet d'une salle de classe.
 */

public class RoomPane extends JPanel implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Le client utilisé pour formuler des requêtes au postes élèves.
	 */
	
	private Client client;
	
	/**
	 * Le nom de cette salle de classe.
	 */
	
	private final String name;
	
	/**
	 * La barre de statut, utilisée pour afficher des informations.
	 */
	
	private final StatusBar bar;
	
	/**
	 * La liste des miniatures.
	 */
	
	private final HashMap<Computer, ComputerThumbnail> thumbnails = new HashMap<Computer, ComputerThumbnail>();
	
	/**
	 * La miniature séléctionnée.
	 */
	
	private ComputerThumbnail selected;
	
	/**
	 * Première méthode exécutée par le panel.
	 */
	
	public RoomPane(final Room room, final StatusBar bar) {
		name = room.name;
		this.bar = bar;
		for(final Computer computer : room.computers) { // Pour chaque ordinateur de la salle, on créé une miniature.
			final ComputerThumbnail thumbnail = new ComputerThumbnail(computer);
			thumbnails.put(computer, thumbnail);
			this.add(thumbnail);
		}
		this.setLayout(new WrapLayout(WrapLayout.LEFT));
		this.addMouseListener(new MouseListener() {
			
			@Override
			public final void mouseClicked(final MouseEvent event) {
				if(RoomPane.this.selected != null && (SwingUtilities.isLeftMouseButton(event) || SwingUtilities.isRightMouseButton(event))) {
					RoomPane.this.selected.unselect();
				}
			}

			@Override
			public final void mouseEntered(final MouseEvent event) {}

			@Override
			public final void mouseExited(final MouseEvent event) {}

			@Override
			public final void mousePressed(final MouseEvent event) {}

			@Override
			public final void mouseReleased(final MouseEvent event) {}
			
		});
		startRequests();
	}
	
	public final String getName() {
		return name;
	}
	
	/**
	 * Démarre les requêtes.
	 */
	
	public final void startRequests() {
		final Set<Computer> computers = thumbnails.keySet();
		client = new Client(this, new Request(RequestType.THUMBNAIL), computers.toArray(new Computer[computers.size()]));
		client.start();
	}
	
	/**
	 * Arrête les requêtes.
	 */
	
	public final void stopRequests() {
		client.stopRequests();
		client = null;
	}

	@Override
	public final void connection(final Computer computer, final long time) {
		bar.setText("Connexion aux ordinateurs de la salle " + name + "...");
		thumbnails.get(computer).setThumbnail(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/thumbnails/thumbnail_loading.gif")), time);
	}

	@Override
	public final void onSuccess(final Computer computer, final Object... returned) {
		if(!(returned[returned.length - 1] instanceof Image)) {
			return;
		}
		bar.setText("La miniature de l'ordinateur " + computer.name + " (" + computer.ip + ":" + computer.port + ") a été récupérée avec succès.");
		final ComputerThumbnail thumbnail = thumbnails.get(computer);
		thumbnail.setThumbnail(new ImageIcon((BufferedImage)returned[returned.length - 1]), (long)returned[1]);
		thumbnail.setTitle(computer.name + System.lineSeparator() + "(" + returned[0] + ")");
	}

	@Override
	public final void onError(final Computer computer, final Exception ex, final long responseTime) {
		bar.setText("L'ordinateur " + computer.name + "(" + computer.ip + ":" + computer.port + ") n'a pas pu être joint.");
		ex.printStackTrace();
		onInterrupted(computer, responseTime);
	}
	
	@Override
	public final void onInterrupted(final Computer computer, final long time) {
		final ComputerThumbnail thumbnail = thumbnails.get(computer);
		thumbnail.setThumbnail(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/thumbnails/thumbnail_error.png")), time);
		thumbnail.setTitle(computer.name);
	}
	
	@Override
	public final void onWaiting() {
		bar.setText("Attente de " + ProjetBBQProf.settings.refreshInterval + " secondes avant de rafraîchir les miniatures...");
	}
	
	/**
	 * La miniature d'un ordinateur.
	 */
	
	public class ComputerThumbnail extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private final Computer computer;
		private final JLabel thumbnail = new JLabel();
		private long thumbnailTime;
		
		public ComputerThumbnail(final Computer computer) {
			this.computer = computer;
			thumbnail.setBackground(Color.WHITE);
			thumbnail.setOpaque(true);
			thumbnail.setHorizontalTextPosition(JLabel.CENTER);
			thumbnail.setVerticalTextPosition(JLabel.BOTTOM);
			thumbnail.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK), new EmptyBorder(10, 10, 10, 10)));
			setTitle(computer.name);
			this.add(thumbnail, BorderLayout.CENTER);
			this.addMouseListener(new MouseListener() {

				@Override
				public final void mouseClicked(final MouseEvent event) {
					if(SwingUtilities.isRightMouseButton(event)) {
						createThumbnailPopupMenu().show(ComputerThumbnail.this, event.getX(), event.getY());
					}
					else if(SwingUtilities.isLeftMouseButton(event)) {
						if(event.getClickCount() == 2 && !event.isConsumed()) {
							new ComputerFrame(computer).setVisible(true); // Si il y a un double clic, on ouvre l'IHM de l'ordinateur correspondant.
						}
					}
					else {
						return;
					}
					if(RoomPane.this.selected != null) {
						RoomPane.this.selected.unselect();
					}
					ComputerThumbnail.this.select();
				}

				@Override
				public final void mouseEntered(final MouseEvent event) {}

				@Override
				public final void mouseExited(final MouseEvent event) {}

				@Override
				public final void mousePressed(final MouseEvent event) {}

				@Override
				public final void mouseReleased(final MouseEvent event) {}
				
			});
			for(final Component component : this.getComponents()) {
				component.addMouseListener(this.getMouseListeners()[0]);
			}
		}
		
		/**
		 * Permet d'obtenir l'ordinateur
		 * 
		 * @return L'ordinateur.
		 */

		public final Computer getComputer() {
			return computer;
		}
		
		/**
		 * Permet de changer la miniature.
		 * 
		 * @param thumbnail La miniature.
		 * @param downloadedTime La date en millisecondes.
		 */
		
		public final void setThumbnail(ImageIcon thumbnail, final long downloadedTime) {
			if(downloadedTime <= thumbnailTime) {
				return;
			}
			if(thumbnail.getIconHeight() != ProjetBBQProf.settings.thumbnailHeight || thumbnail.getIconWidth() != ProjetBBQProf.settings.thumbnailWidth) {
				thumbnail = new ImageIcon(thumbnail.getImage().getScaledInstance(ProjetBBQProf.settings.thumbnailWidth, ProjetBBQProf.settings.thumbnailHeight, Image.SCALE_SMOOTH));
			}
			this.thumbnailTime = downloadedTime;
			this.thumbnail.setIcon(thumbnail);
		}
		
		/**
		 * Changement du titre de la miniature.
		 * 
		 * @param title Le nouveau titre.
		 */
		
		public final void setTitle(final String title) {
			thumbnail.setText("<html><div style=text-align:\"center\";>" + title.replace(System.lineSeparator(), "<br>") + "</div></html>");
		}
		
		/**
		 * Permet de retourner la miniature actuelle.
		 * 
		 * @return La miniature actuelle.
		 */
		
		public final Icon getThumbnail() {
			return thumbnail.getIcon();
		}
		
		/**
		 * Sélectionne la miniature.
		 */
		
		public final void select() {
			RoomPane.this.selected = this;
			this.setBackground(SmartLookAndFeel.getWindowTitleBackground().darker());
		}
		
		/**
		 * Déselectionne la miniature.
		 */
		
		public final void unselect() {
			RoomPane.this.selected = null;
			this.setBackground(SmartLookAndFeel.getBackgroundColor()); // On remet la couleur par défaut.
		}
		
		private final JPopupMenu createThumbnailPopupMenu() {
			final JPopupMenu popup = new JPopupMenu();
			final JMenuItem sendMessage = new JMenuItem("Envoyer un message");
			sendMessage.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					final Object[] dialogData = Utils.createMessageDialog(null);
					if((boolean)dialogData[0]) {
						final Component[] components = (Component[])dialogData[1];
						ComputerFrame.createClientDialog(new Request(RequestType.MESSAGE, ((JTextField)components[0]).getText(), String.valueOf(((JSpinner)components[1]).getValue())), null, computer);
					}
				}
				
			});
			sendMessage.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_sendmessage.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			final JMenuItem shutdown = new JMenuItem("Éteindre le PC");
			shutdown.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					ComputerFrame.createClientDialog(new Request(RequestType.SHUTDOWN), null, computer);
				}
				
			});
			shutdown.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_shutdown.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			final JMenuItem restart = new JMenuItem("Redémarrer le PC");
			restart.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					ComputerFrame.createClientDialog(new Request(RequestType.RESTART), null, computer);
				}
				
			});
			restart.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_restart.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			final JMenuItem logout = new JMenuItem("Déconnecter le PC");
			logout.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					ComputerFrame.createClientDialog(new Request(RequestType.LOGOUT), null, computer);
				}
				
			});
			logout.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_logout.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			final JMenuItem lock = new JMenuItem("Verrouiller le PC");
			lock.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					ComputerFrame.createClientDialog(new Request(RequestType.LOCK), null, computer);
				}
				
			});
			lock.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_lock.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			final JMenuItem unlock = new JMenuItem("Déverrouiller le PC");
			unlock.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					ComputerFrame.createClientDialog(new Request(RequestType.UNLOCK), null, computer);
				}
				
			});
			unlock.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_unlock.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			popup.add(sendMessage);
			popup.addSeparator();
			popup.add(shutdown);
			popup.add(restart);
			popup.add(logout);
			popup.addSeparator();
			popup.add(lock);
			popup.add(unlock);
			return popup;
		}
		
	}

}