package fr.isn.bbq.prof.frames.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.tasks.Client;
import fr.isn.bbq.prof.tasks.Client.ClientInterface;
import fr.isn.bbq.prof.tasks.ClientRequests;
import fr.isn.bbq.prof.tasks.ClientRequests.RequestType;

public class RoomPane extends JPanel implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
	private static final short COLUMNS = 3;
	
	private final Client client;
	private final HashMap<Computer, ComputerThumbnail> thumbnails = new HashMap<Computer, ComputerThumbnail>();
	
	/**
	 * Première méthode exécutée par le panel.
	 */
	
	public RoomPane(final List<Computer> computers) {
		client = new Client(this, ClientRequests.createRequest(RequestType.THUMBNAIL, ProjetBBQProf.settings.uuid), computers.toArray(new Computer[computers.size()]));
		for(int i = 0, index = 0; i != COLUMNS; i++) {
			final JPanel line = new JPanel();
			for(int j = 0; j != computers.size() / COLUMNS; j++) {
				final Computer computer = computers.get(index++);
				final ComputerThumbnail thumbnail = new ComputerThumbnail(computer);
				thumbnails.put(computer, thumbnail);
				line.add(thumbnail);
			}
			this.add(line);
		}
		this.addComponentListener(new ComponentAdapter() {
			
			@Override
			public final void componentShown(final ComponentEvent event) {
				client.start();
			}
			
			@Override
			public final void componentHidden(final ComponentEvent event) {
				client.stopRequests();
			}
			
		});
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	@Override
	public final void connection(final Computer computer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public final void onSuccess(final Computer computer, final Object returned) {
		// TODO Auto-generated method stub
	}

	@Override
	public final void onError(final Computer computer, final Exception ex) {
		// TODO Auto-generated method stub
	}
	
	public class ComputerThumbnail extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private final Computer computer;
		private final JLabel thumbnail = new JLabel();
		
		public ComputerThumbnail(final Computer computer) {
			this.computer = computer;
			thumbnail.setBackground(Color.WHITE);
			setThumbnail(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/loading.gif")));
			thumbnail.setText(computer.name);
			thumbnail.setHorizontalTextPosition(JLabel.CENTER);
			thumbnail.setVerticalTextPosition(JLabel.BOTTOM);
			thumbnail.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK), new EmptyBorder(10,10,10,10)));
			this.add(thumbnail, BorderLayout.CENTER);
		}

		public final Computer getComputer() {
			return computer;
		}
		
		public final void setThumbnail(final ImageIcon thumbnail) {
			if(thumbnail.getIconHeight() > Client.THUMBNAIL_SIZE || thumbnail.getIconWidth() > Client.THUMBNAIL_SIZE) {
				return;
			}
			this.thumbnail.setIcon(thumbnail);
		}
		
	}
	
}