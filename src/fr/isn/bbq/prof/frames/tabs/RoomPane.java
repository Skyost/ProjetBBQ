package fr.isn.bbq.prof.frames.tabs;

import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.tasks.Client;
import fr.isn.bbq.prof.tasks.Client.ClientInterface;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class RoomPane extends JPanel implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
	
	private final Client client;
	
	/**
	 * Première méthode exécutée par le panel.
	 */
	
	public RoomPane(final List<Computer> computers) {
		final GridLayout gridLayout = new GridLayout();
		final JScrollPane scrollPane = new JScrollPane(new JPanel(gridLayout));
		final GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
		);
		client = new Client(this, computers.toArray(new Computer[computers.size()]));
		this.setLayout(groupLayout);
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
	
}