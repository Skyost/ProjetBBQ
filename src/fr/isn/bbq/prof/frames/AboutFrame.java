package fr.isn.bbq.prof.frames;

import javax.swing.JFrame;
import fr.isn.bbq.prof.ProjetBBQProf;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ImageIcon;

/**
 * IHM d'un poste élève.
 */

public class AboutFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Construction de cet IHM.
	 */
	
	public AboutFrame() {
		this.setTitle("À propos...");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/app_icon.png")));
		this.setSize(600, 450); // Par défaut, une taille de 600x200.
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		final JLabel lblAppName = new JLabel(ProjetBBQProf.APP_NAME);
		lblAppName.setHorizontalAlignment(SwingConstants.CENTER);
		lblAppName.setFont(lblAppName.getFont().deriveFont(32f).deriveFont(Font.BOLD));
		
		final JLabel lblVersion = new JLabel("v" + ProjetBBQProf.APP_VERSION);
		lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
		
		final JLabel lblIcon = new JLabel(new ImageIcon(new ImageIcon(AboutFrame.class.getResource("/fr/isn/bbq/prof/res/app_icon.png")).getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH)));
		
		final StringBuilder builder = new StringBuilder("<html>");
		builder.append("<b>Liste des contributeurs :</b> ");
		builder.append("Thibault Dolley, ");
		builder.append("Bastien Lesachey, ");
		builder.append("Hugo Delaunay et ");
		builder.append("Hugo Meslin.</html>");
		
		final JLabel lblContributors = new JLabel(builder.toString());
		builder.setLength(0);
		
		builder.append("<html><a href=\"https://github.com/Skyost/ProjetBBQ/wiki\">Voir licence, aide et crédits ici.</a></html> ");
		
		final JLabel lblLicenceCredits = new JLabel(builder.toString());
		lblLicenceCredits.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblLicenceCredits.addMouseListener(new MouseListener() {

			@Override
			public final void mouseClicked(final MouseEvent event) {
				if(Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URL("https://github.com/Skyost/ProjetBBQ/wiki").toURI());
					}
					catch(final Exception ex) {
						ex.printStackTrace();
					}
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
		
		final Container content = this.getContentPane();
		final GroupLayout groupLayout = new GroupLayout(content);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblVersion, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
								.addComponent(lblAppName, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
								.addComponent(lblIcon, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE))
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblLicenceCredits, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(lblContributors, GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
							.addGap(20))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblAppName)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblVersion)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblIcon)
					.addGap(18)
					.addComponent(lblContributors)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblLicenceCredits)
					.addContainerGap(96, Short.MAX_VALUE))
		);
		content.setLayout(groupLayout);
	}
}