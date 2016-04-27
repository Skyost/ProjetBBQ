package fr.isn.bbq.prof.frames.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.Room;
import fr.isn.bbq.prof.tasks.Client;
import fr.isn.bbq.prof.tasks.Client.ClientInterface;
import fr.isn.bbq.prof.tasks.ClientRequests;
import fr.isn.bbq.prof.tasks.ClientRequests.RequestType;
import fr.isn.bbq.prof.utils.StatusBar;
import fr.isn.bbq.prof.utils.WrapLayout;

public class RoomPane extends JPanel implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
	
	private Client client;
	private final String name;
	private final StatusBar bar;
	private final HashMap<Computer, ComputerThumbnail> thumbnails = new HashMap<Computer, ComputerThumbnail>();
	private ComputerThumbnail selected;
	
	/**
	 * Première méthode exécutée par le panel.
	 */
	
	public RoomPane(final Room room, final StatusBar bar) {
		name = room.name;
		this.bar = bar;
		for(final Computer computer : room.computers) {
			final ComputerThumbnail thumbnail = new ComputerThumbnail(computer);
			thumbnails.put(computer, thumbnail);
			this.add(thumbnail);
		}
		this.setLayout(new WrapLayout(WrapLayout.LEFT));
		startRequests();
	}
	
	public final void startRequests() {
		final Set<Computer> computers = thumbnails.keySet();
		client = new Client(this, ClientRequests.createRequest(RequestType.THUMBNAIL, ProjetBBQProf.settings.uuid), computers.toArray(new Computer[computers.size()]));
		client.start();
	}
	
	public final void stopRequests() {
		client.stopRequests();
		client = null;
	}

	@Override
	public final void connection(final Computer computer, final long time) {
		bar.setText("Connexion aux ordinateurs de la salle " + name + "...");
		thumbnails.get(computer).setThumbnail(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/loading.gif")), time);
	}

	@Override
	public final void onSuccess(final Computer computer, final Object returned, final long responseTime) {
		bar.setText("La miniature de l'ordinateur " + computer.name + " (" + computer.ip + ") a été récupérée avec succès.");
		// TODO : On met l'image envoyée sur la miniature
	}

	@Override
	public final void onError(final Computer computer, final Exception ex, final long responseTime) {
		bar.setText("L'ordinateur " + computer.name + "(" + computer.ip + ") n'a pas pu être joint.");
		ex.printStackTrace();
		onInterrupted(computer, responseTime);
	}
	
	@Override
	public final void onInterrupted(final Computer computer, final long time) {
		thumbnails.get(computer).setThumbnail(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/error.png")), time);
	}
	
	@Override
	public final void onWaiting() {
		bar.setText("Attente de " + ProjetBBQProf.settings.refreshInterval + " avant de rafraîchir les miniatures...");
	}
	
	public class ComputerThumbnail extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private final Computer computer;
		private final JLabel thumbnail = new JLabel();
		private long thumbnailTime;
		
		public ComputerThumbnail(final Computer computer) {
			this.computer = computer;
			thumbnail.setBackground(Color.WHITE);
			thumbnail.setText(computer.name);
			thumbnail.setOpaque(true);
			thumbnail.setHorizontalTextPosition(JLabel.CENTER);
			thumbnail.setVerticalTextPosition(JLabel.BOTTOM);
			thumbnail.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK), new EmptyBorder(10,10,10,10)));
			this.add(thumbnail, BorderLayout.CENTER);
			this.addMouseListener(new MouseListener() {

				@Override
				public final void mouseClicked(final MouseEvent event) {
					if(RoomPane.this.selected != null) {
						RoomPane.this.selected.unselect();
					}
					System.out.println(computer.name);
					if(event.getClickCount() == 2 && !event.isConsumed()) {
						System.out.println("Double clic !");
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

		public final Computer getComputer() {
			return computer;
		}
		
		public final void setThumbnail(final ImageIcon thumbnail, final long downloadedTime) {
			if(downloadedTime <= thumbnailTime) {
				return;
			}
			if(thumbnail.getIconHeight() > Client.THUMBNAIL_SIZE || thumbnail.getIconWidth() > Client.THUMBNAIL_SIZE) {
				thumbnail.getImage().getScaledInstance(Client.THUMBNAIL_SIZE, Client.THUMBNAIL_SIZE, Image.SCALE_DEFAULT);
			}
			this.thumbnailTime = downloadedTime;
			this.thumbnail.setIcon(thumbnail);
		}
		
		public final void select() {
			RoomPane.this.selected = this;
			this.setBackground(Color.BLUE);
		}
		
		public final void unselect() {
			RoomPane.this.selected = null;
			this.setBackground(UIManager.getColor("Panel.background"));
		}
		
	}

}