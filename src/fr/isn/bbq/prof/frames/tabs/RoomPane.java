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
import fr.isn.bbq.prof.utils.LanguageManager;
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
		this.bar = bar; // On ajoute une référence vers la barre de statuts pour la mettre à jour.
		for(final Computer computer : room.computers) { // Pour chaque ordinateur de la salle, on créé une miniature.
			final ComputerThumbnail thumbnail = new ComputerThumbnail(computer);
			thumbnails.put(computer, thumbnail); // On ajoute les miniatures en fonction des ordinateurs.
			this.add(thumbnail);
		}
		this.setLayout(new WrapLayout(WrapLayout.LEFT)); // Les images doivent être disposées en partant de la gauche.
		this.addMouseListener(new MouseListener() {
			
			@Override
			public final void mouseClicked(final MouseEvent event) {
				if(RoomPane.this.selected != null && (SwingUtilities.isLeftMouseButton(event) || SwingUtilities.isRightMouseButton(event))) { // Si il y a un clic gauche ou un droit, on déselectionne la miniature.
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
	}
	
	/**
	 * Permet d'obtenir le nom de la salle gérée par ce panel.
	 */
	
	public final String getName() {
		return name;
	}
	
	/**
	 * Démarre les requêtes (si il y en a déjà, elles sont annulées).
	 */
	
	public final void startRequests() {
		if(client != null) {
			stopRequests();
		}
		final Set<Computer> computers = thumbnails.keySet();
		client = new Client(this, new Request(RequestType.THUMBNAIL), computers.toArray(new Computer[computers.size()]));
		client.start();
	}
	
	/**
	 * Arrête les requêtes.
	 */
	
	public final void stopRequests() {
		if(client == null) {
			return;
		}
		client.stopRequests();
		client = null;
	}

	@Override
	public final void connection(final Computer computer, final long time) {
		bar.setText(LanguageManager.getString("room.statusbar.connection", name)); // On change la barre de status et on met à jour l'image.
		thumbnails.get(computer).setThumbnail(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/thumbnails/thumbnail_loading.gif")), time);
	}

	@Override
	public final void onSuccess(final Computer computer, final Object... returned) {
		if(!(returned[returned.length - 1] instanceof Image)) { // Si on a pas d'image, on s'en va.
			return;
		}
		/* Sinon on met à jour la barre de status, le titre et l'image : */
		bar.setText(LanguageManager.getString("room.statusbar.success", computer.name, computer.ip, computer.port));
		final ComputerThumbnail thumbnail = thumbnails.get(computer);
		thumbnail.setThumbnail(new ImageIcon((BufferedImage)returned[returned.length - 1]), (long)returned[1]);
		thumbnail.setTitle(computer.name + System.lineSeparator() + "(" + returned[0] + ")");
	}

	@Override
	public final void onError(final Computer computer, final Exception ex, final long responseTime) {
		bar.setText(LanguageManager.getString("room.statusbar.error", computer.name, computer.ip, computer.port, ex.getMessage()));
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
		bar.setText(LanguageManager.getString("room.statusbar.waiting", ProjetBBQProf.settings.refreshInterval));
	}
	
	/**
	 * La miniature d'un ordinateur.
	 */
	
	public class ComputerThumbnail extends JPanel {

		private static final long serialVersionUID = 1L;
		
		/**
		 * L'ordinateur géré par cette miniature.
		 */
		
		private final Computer computer;
		
		/**
		 * Permet d'afficher la miniature.
		 */
		
		private final JLabel thumbnail = new JLabel();
		
		/**
		 * La dernière fois que cette miniature a été rafraichie.
		 */
		
		private long thumbnailTime;
		
		public ComputerThumbnail(final Computer computer) {
			this.computer = computer; // On pointe la référence vers l'ordinateur.
			thumbnail.setBackground(Color.WHITE); // Fond blanc.
			thumbnail.setOpaque(true); // Opaque.
			thumbnail.setHorizontalTextPosition(JLabel.CENTER); // Texte centrée horizonatalement.
			thumbnail.setVerticalTextPosition(JLabel.BOTTOM); // Et en dessous de l'image.
			thumbnail.setBorder(new CompoundBorder(BorderFactory.createLineBorder(SmartLookAndFeel.getWindowBorderColor()), new EmptyBorder(10, 10, 10, 10))); // Avec une bordure noire de 10px.
			setTitle(computer.name); // On affecte un titre par défaut.
			this.add(thumbnail, BorderLayout.CENTER); // La miniature est ajoutée au centre du panel.
			this.addMouseListener(new MouseListener() {

				@Override
				public final void mouseClicked(final MouseEvent event) {
					if(SwingUtilities.isRightMouseButton(event)) { // Si c'est un clic droit, on créé un popup menu.
						createThumbnailPopupMenu().show(ComputerThumbnail.this, event.getX(), event.getY());
					}
					else if(SwingUtilities.isLeftMouseButton(event)) { // Si c'est un clic gauche et un double clic, on ouvre le menu de la capture d'écran.
						if(event.getClickCount() == 2 && !event.isConsumed()) {
							new ComputerFrame(computer).setVisible(true);
						}
					}
					else { // Sinon on s'en va.
						return;
					}
					if(RoomPane.this.selected != null) { // Si il y a déjà une miniature de séléctionnée, on la déselectionne.
						RoomPane.this.selected.unselect();
					}
					ComputerThumbnail.this.select(); // Et on séléctionne celle-ci.
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
		
		/**
		 * Permet de créer le popup menu (lors du clic droit sur cette miniature).
		 * 
		 * @return Le popup menu.
		 */
		
		private final JPopupMenu createThumbnailPopupMenu() {
			final JPopupMenu popup = new JPopupMenu();
			/* Le menu pour envoyer un message : */
			final JMenuItem sendMessage = new JMenuItem(LanguageManager.getString("common.pc.singular.message"));
			sendMessage.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					final Object[] data = Utils.createMessageDialog(null);
					if((boolean)data[0]) {
						ComputerFrame.createClientDialog(new Request(RequestType.MESSAGE, data[1].toString(), data[2].toString()), null, computer);
					}
				}
				
			});
			sendMessage.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_sendmessage.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			/* Le menu pour envoyer éteindre le PC : */
			final JMenuItem shutdown = new JMenuItem(LanguageManager.getString("common.pc.singular.shutdown"));
			shutdown.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					ComputerFrame.createClientDialog(new Request(RequestType.SHUTDOWN), null, computer);
				}
				
			});
			shutdown.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_shutdown.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			/* Le menu pour redémarrer le PC : */
			final JMenuItem restart = new JMenuItem(LanguageManager.getString("common.pc.singular.restart"));
			restart.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					ComputerFrame.createClientDialog(new Request(RequestType.RESTART), null, computer);
				}
				
			});
			restart.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_restart.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			/* Le menu pour déconnecter le PC : */
			final JMenuItem logout = new JMenuItem(LanguageManager.getString("common.pc.singular.logout"));
			logout.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					ComputerFrame.createClientDialog(new Request(RequestType.LOGOUT), null, computer);
				}
				
			});
			logout.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_logout.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			/* Le menu pour verrouiller le PC : */
			final JMenuItem lock = new JMenuItem(LanguageManager.getString("common.pc.singular.lock"));
			lock.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					ComputerFrame.createClientDialog(new Request(RequestType.LOCK), null, computer);
				}
				
			});
			lock.setIcon(new ImageIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/menu/menu_lock.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			/* Le menu pour déverrouiller le PC : */
			final JMenuItem unlock = new JMenuItem(LanguageManager.getString("common.pc.singular.unlock"));
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