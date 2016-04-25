package fr.isn.bbq.prof.frames.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.tasks.Client;
import fr.isn.bbq.prof.tasks.Client.ClientInterface;
import fr.isn.bbq.prof.tasks.ClientRequests;
import fr.isn.bbq.prof.tasks.ClientRequests.RequestType;
import fr.isn.bbq.prof.utils.WrapLayout;

public class RoomPane extends JPanel implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
	
	private final Client client;
	private final HashMap<Computer, ComputerThumbnail> thumbnails = new HashMap<Computer, ComputerThumbnail>();
	private ComputerThumbnail selected;
	
	/**
	 * Première méthode exécutée par le panel.
	 */
	
	public RoomPane(final List<Computer> computers) {
		client = new Client(this, ClientRequests.createRequest(RequestType.THUMBNAIL, ProjetBBQProf.settings.uuid), computers.toArray(new Computer[computers.size()]));
		for(final Computer computer : computers) {
			final ComputerThumbnail thumbnail = new ComputerThumbnail(computer);
			thumbnails.put(computer, thumbnail);
			this.add(thumbnail);
		}
		this.setLayout(new WrapLayout(WrapLayout.LEFT));
		startRequests();
	}
	
	public final void startRequests() {
		client.start();
	}
	
	public final void stopRequests() {
		client.stopRequests();
	}

	@Override
	public final void connection(final Computer computer) {
		thumbnails.get(computer).setThumbnail(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/loading.gif")));
	}

	@Override
	public final void onSuccess(final Computer computer, final Object returned) {
		// TODO Auto-generated method stub
	}

	@Override
	public final void onError(final Computer computer, final Exception ex) {
		thumbnails.get(computer).setThumbnail(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/error.png")));
	}
	
	public class ComputerThumbnail extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private final Computer computer;
		private final JLabel thumbnail = new JLabel();
		
		public ComputerThumbnail(final Computer computer) {
			this.computer = computer;
			setThumbnail(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/loading.gif")));
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
		
		public final void setThumbnail(final ImageIcon thumbnail) {
			if(thumbnail.getIconHeight() > Client.THUMBNAIL_SIZE || thumbnail.getIconWidth() > Client.THUMBNAIL_SIZE) {
				thumbnail.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
			}
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