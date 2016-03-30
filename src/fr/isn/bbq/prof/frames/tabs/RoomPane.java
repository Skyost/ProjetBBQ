package fr.isn.bbq.prof.frames.tabs;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.isn.bbq.prof.Computer;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class RoomPane extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Première méthode exécutée par le panel.
	 */
	
	public RoomPane(final List<Computer> computers) {
		for(final Computer computer : computers) {
			System.out.println(computer.name);
		}
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
	
}