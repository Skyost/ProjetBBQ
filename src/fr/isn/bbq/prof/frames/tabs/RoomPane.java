package fr.isn.bbq.prof.frames.tabs;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.isn.bbq.prof.Computer;
import fr.isn.bbq.prof.tasks.Client.ClientInterface;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class RoomPane extends JPanel implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
	
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
		this.setLayout(groupLayout);
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