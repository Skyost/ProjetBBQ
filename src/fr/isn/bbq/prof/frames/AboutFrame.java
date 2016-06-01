package fr.isn.bbq.prof.frames;

import javax.swing.JFrame;
import fr.isn.bbq.prof.ProjetBBQProf;
import fr.isn.bbq.prof.tasks.Client;
import fr.isn.bbq.prof.utils.GithubUpdater;
import fr.isn.bbq.prof.utils.GithubUpdater.GithubUpdaterResultListener;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
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
 * IHM de la fenêtre "À propos".
 */

public class AboutFrame extends JFrame implements GithubUpdaterResultListener {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Permet d'afficher le statut de l'updater (une mise à jour est disponible, aucune mise à jour, ...).
	 */
	
	private final JLabel lblUpdaterStatus = new JLabel();
	
	/**
	 * Construction de cet IHM.
	 */
	
	public AboutFrame() {
		this.setTitle("À propos...");
		this.setIconImages(ProjetBBQProf.icons);
		this.setSize(600, 500); // Par défaut, une taille de 600x500.
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		new GithubUpdater(ProjetBBQProf.APP_VERSION, this).start(); // On démarre l'updater.
		
		final JLabel lblAppName = new JLabel(ProjetBBQProf.APP_NAME); // Permet d'afficher le nom de l'application.
		lblAppName.setHorizontalAlignment(SwingConstants.CENTER);
		lblAppName.setFont(lblAppName.getFont().deriveFont(32f).deriveFont(Font.BOLD));
		
		final StringBuilder builder = new StringBuilder("<html>");
		builder.append("<b>" + ProjetBBQProf.APP_NAME + "</b> ");
		builder.append("v" + ProjetBBQProf.APP_VERSION + " ");
		builder.append("<b>Protocole :</b> ");
		builder.append("v" + Client.PROTOCOL_VERSION + "</html>");
		
		final JLabel lblVersion = new JLabel(builder.toString()); // Permet d'afficher les différentes versions.
		lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
		
		final JLabel lblIcon = new JLabel(new ImageIcon(new ImageIcon(AboutFrame.class.getResource("/fr/isn/bbq/prof/res/app_icon.png")).getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH))); // Permet d'afficher l'icône du projet.
		
		builder.setLength(0);
		builder.append("<html><b>Liste des contributeurs :</b> ");
		builder.append("Thibault Dolley, ");
		builder.append("Bastien Lesachey, ");
		builder.append("Hugo Delaunay, ");
		builder.append("Hugo Meslin et ");
		builder.append("M. Dacié.</html>");
		
		final JLabel lblContributors = new JLabel(builder.toString()); // Permet d'afficher les contributeurs.
		
		builder.setLength(0);
		builder.append("<html><a href=\"https://github.com/Skyost/ProjetBBQ/wiki\">Voir licence, aide et crédits ici.</a></html> ");
		
		final JLabel lblLicenceCredits = new JLabel(builder.toString()); // Permet d'afficher la licence et les crédits.
		openLinkOnClick(lblLicenceCredits, "https://github.com/Skyost/ProjetBBQ/wiki");
		
		lblUpdaterStatus.setHorizontalAlignment(SwingConstants.CENTER);
		openLinkOnClick(lblUpdaterStatus, "https://github.com/Skyost/ProjetBBQ/releases");
		
		final Container content = this.getContentPane();
		
		final GroupLayout groupLayout = new GroupLayout(content);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblIcon, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
						.addComponent(lblUpdaterStatus, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
						.addComponent(lblVersion, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
						.addComponent(lblAppName, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
						.addComponent(lblContributors, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
						.addComponent(lblLicenceCredits, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblAppName)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblVersion)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblIcon, GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblContributors)
					.addGap(3)
					.addComponent(lblLicenceCredits)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblUpdaterStatus)
					.addContainerGap())
		);
		content.setLayout(groupLayout);
	}
	
	@Override
	public final void updaterStarted() {
		lblUpdaterStatus.setFont(lblUpdaterStatus.getFont().deriveFont(Font.ITALIC));
		lblUpdaterStatus.setText("Vérification des mises à jour..."); // On change le texte pour indiquer que l'on vérifie les nouvelles mises à jour.
		lblUpdaterStatus.setIcon(new ImageIcon(ProjetBBQProf.class.getResource("/fr/isn/bbq/prof/res/updater_loading.gif"))); // On ajoute une icône de chargement.
	}
	
	@Override
	public final void updaterException(final Exception ex) {
		lblUpdaterStatus.setFont(lblUpdaterStatus.getFont().deriveFont(Font.PLAIN).deriveFont(Font.BOLD));
		lblUpdaterStatus.setForeground(Color.decode("#E74C3C")); // On change la couleur.
		lblUpdaterStatus.setText("Erreur : \"" + ex.getMessage() + "\"");
		lblUpdaterStatus.setIcon(null); // On enlève l'icône.
	}
	
	@Override
	public final void updaterResponse(final String response) {}
	
	@Override
	public final void updaterUpdateAvailable(final String localVersion, final String remoteVersion) {
		lblUpdaterStatus.setFont(lblUpdaterStatus.getFont().deriveFont(Font.PLAIN).deriveFont(Font.BOLD));
		lblUpdaterStatus.setForeground(Color.decode("#2980B9"));
		lblUpdaterStatus.setText("Une nouvelle version est disponible (v" + remoteVersion + ") !");
		lblUpdaterStatus.setIcon(null);
	}
	
	@Override
	public final void updaterNoUpdate(final String localVersion, final String remoteVersion) {
		lblUpdaterStatus.setFont(lblUpdaterStatus.getFont().deriveFont(Font.PLAIN).deriveFont(Font.BOLD));
		lblUpdaterStatus.setForeground(Color.decode("#27AE60"));
		lblUpdaterStatus.setText("Le logiciel est à jour.");
		lblUpdaterStatus.setIcon(null);
	}
	
	/**
	 * Ajout d'un évènement de clic vers un lien pour un label.
	 * 
	 * @param label Le label.
	 * @param link Le lien.
	 */
	
	private final void openLinkOnClick(final JLabel label, final String link) {
		label.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Main sur le label.
		label.addMouseListener(new MouseListener() {

			@Override
			public final void mouseClicked(final MouseEvent event) {
				if(Desktop.isDesktopSupported()) { // Si il y a un bureau, on ouvre le lien.
					try {
						Desktop.getDesktop().browse(new URL(link).toURI());
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
	}
	
}